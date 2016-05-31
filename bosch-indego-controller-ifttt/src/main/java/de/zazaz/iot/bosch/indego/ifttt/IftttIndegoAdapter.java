/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.zazaz.iot.bosch.indego.ifttt;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.zazaz.iot.bosch.indego.DeviceCommand;
import de.zazaz.iot.bosch.indego.DeviceStateInformation;
import de.zazaz.iot.bosch.indego.DeviceStatus;
import de.zazaz.iot.bosch.indego.IndegoController;
import de.zazaz.iot.bosch.indego.IndegoException;

/**
 * This class connects to an Indego device and provides a simple server which can be used by the IFTTT maker
 * channel.
 */
public class IftttIndegoAdapter {

    /** the logger */
    private static final Logger LOG = LogManager.getLogger(IftttIndegoAdapter.class);

    private static final String URL_IFTTT = "https://maker.ifttt.com/trigger/%s/with/key/%s";

    /** the configuration to use */
    private IftttIndegoAdapterConfiguration configuration;

    /** this is used for indicating, that we are request to shutdown */
    private AtomicBoolean flagShutdown = new AtomicBoolean(false);

    /** semaphore for waking worker thread up */
    private Semaphore semThreadWaker;

    /** a reference to the worker thread */
    private Thread threadWorker;

    /** contains the last command */
    private AtomicReference<String> commandToExecute = new AtomicReference<>();

    /**
     * Initializes the IFTTT Adapter.
     * 
     * @param configuration_ the configuration to use
     */
    public IftttIndegoAdapter (IftttIndegoAdapterConfiguration configuration_)
    {
        // Get a clone, since we don't want to be manipulated during runtime
        configuration = (IftttIndegoAdapterConfiguration) configuration_.clone();
    }

    /**
     * This starts the adapter.
     */
    public synchronized void startup ()
    {
        if ( threadWorker != null ) {
            throw new IllegalStateException("The adapter is already started");
        }
        flagShutdown.set(false);
        semThreadWaker = new Semaphore(0);
        threadWorker = new Thread(new Runnable() {

            @Override
            public void run ()
            {
                runOuter();
            }
        });
        LOG.debug("Starting worker thread");
        try {
            threadWorker.start();
            LOG.debug("Worker thread started");
        }
        catch (RuntimeException ex) {
            LOG.error("Failed to start worker thread", ex);
            threadWorker = null;
            throw ex;
        }
    }

    /**
     * This shuts down the adapter.
     */
    public synchronized void shutdown ()
    {
        if ( threadWorker == null ) {
            throw new IllegalStateException("The adapter is not started");
        }
        try {
            LOG.debug("Requesting worker thread to shut down");
            flagShutdown.set(true);
            semThreadWaker.release();
            LOG.debug("Waiting for worker thread");
            while (true) {
                try {
                    threadWorker.join();
                    break;
                }
                catch (InterruptedException ex) {
                    // Ignored
                }
            }
            LOG.debug("Worker thread terminated, shutdown complete");
        }
        finally {
            threadWorker = null;
            semThreadWaker = null;
            flagShutdown.set(false);
        }
    }

    /**
     * This is the initial entry point for the worker thread.
     */
    private void runOuter ()
    {
        LOG.debug("Worker thread started");
        try {
            while (!flagShutdown.get()) {
                try {
                    runInternal();
                }
                catch (Exception ex) {
                    LOG.fatal("Unhandled exception thrown! Trying a restart...", ex);
                }
            }
        }
        finally {
            LOG.debug("Closing worker thread");
        }
    }

    /**
     * This is the inner run method, which does the actual work.
     */
    private void runInternal ()
    {
        CloseableHttpClient httpClient = buildHttpClient();
        Server httpServer = buildHttpServer();
        IndegoController indegoController = null;
        Semaphore semWakeup = semThreadWaker;

        boolean firstRun = true;
        boolean lastOffline = false;
        DeviceCommand lastCommand = null;
        int lastErrorCode = 0;

        try {
            while (!flagShutdown.get()) {
                if ( indegoController == null ) {
                    LOG.info("No connection to Indego server. Creating connection.");
                    indegoController = connectIndego();
                    if ( indegoController == null ) {
                        LOG.warn("Was not able to connect to Indego server.");
                    }
                }

                DeviceStateInformation currentState = null;
                if ( indegoController != null ) {
                    try {
                        currentState = indegoController.getState();
                    }
                    catch (Exception ex) {
                        LOG.error("Exception during fetching Indego state", ex);
                        disconnect(indegoController);
                        indegoController = null;
                        try {
                            semWakeup.tryAcquire(configuration.getPollingIntervalMs(), TimeUnit.MILLISECONDS);
                        }
                        catch (InterruptedException ex2) {
                            // Ignored
                        }
                        continue;
                    }
                }

                if ( indegoController != null ) {
                    String command = commandToExecute.getAndSet(null);
                    if ( command != null ) {
                        LOG.info(String.format("Received command: %s", command));
                        DeviceCommand cmd = null;
                        try {
                            cmd = DeviceCommand.valueOf(command);
                        }
                        catch (Exception ex) {
                            LOG.error(String.format("Received invalid command from IFTTT (%s)", command), ex);
                        }
                        try {
                            if ( cmd != null ) {
                                LOG.info(String.format("Sending command to Indego: %s", cmd));
                                indegoController.sendCommand(cmd);
                                LOG.info(String.format("Command '%s' was sent successfully", cmd));
                            }
                        }
                        catch (Exception ex) {
                            LOG.error(String.format("Indego was not able to execute command (%s)", command), ex);
                        }
                    }
                }

                boolean currentOffline = indegoController == null;
                DeviceCommand currentCommand = null;
                int currentErrorCode = 0;
                if ( currentState != null ) {
                    currentCommand = DeviceStatus.decodeStatusCode(currentState.getState()).getAssociatedCommand();
                    currentErrorCode = currentState.getError();
                }

                boolean commitOffline = firstRun;
                boolean commitCommand = firstRun;
                boolean commitErrorCode = firstRun;

                if ( !firstRun ) {
                    String percentMowed = currentState != null ? Integer.toString(currentState.getMowed()) : "unknown";

                    if ( currentOffline != lastOffline ) {
                        if ( currentOffline ) {
                            commitOffline = sendIfftTrigger(httpClient, configuration.getIftttOfflineEventName(), "offline", "",
                                    percentMowed);
                        }
                        else {
                            commitOffline = sendIfftTrigger(httpClient, configuration.getIftttOnlineEventName(), "online", "", percentMowed);
                        }
                    }

                    if ( currentCommand != lastCommand ) {
                        String message = currentCommand != null ? currentCommand.toString() : "UNKNOWN";
                        commitCommand = sendIfftTrigger(httpClient, configuration.getIftttStateChangeEventName(), message, "", percentMowed);
                    }

                    if ( currentErrorCode != lastErrorCode ) {
                        if ( currentErrorCode == 0 ) {
                            commitErrorCode = sendIfftTrigger(httpClient, configuration.getIftttErrorClearedEventName(),
                                    Integer.toString(currentErrorCode), "", percentMowed);
                        }
                        else {
                            commitErrorCode = sendIfftTrigger(httpClient, configuration.getIftttErrorEventName(),
                                    Integer.toString(currentErrorCode), "Unknown error", percentMowed);
                        }
                    }
                }

                if ( commitOffline ) {
                    lastOffline = currentOffline;
                }
                if ( commitCommand ) {
                    lastCommand = currentCommand;
                }
                if ( commitErrorCode ) {
                    lastErrorCode = currentErrorCode;
                }
                firstRun = false;

                try {
                    semWakeup.tryAcquire(configuration.getPollingIntervalMs(), TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException ex) {
                    // Ignored
                }
            }
        }
        finally {
            disconnect(indegoController);
            disconnect(httpServer);
            disconnect(httpClient);
        }
    }

    /**
     * This handles a command request
     * 
     * @param req_ the servlet request
     * @param resp_ the servlet response
     */
    private void handleIftttCommand (HttpServletRequest req_, HttpServletResponse resp_)
    {
        String commandStr = req_.getPathInfo();
        int idx = commandStr.lastIndexOf('/');
        if ( idx > 0 ) {
            commandStr = commandStr.substring(idx + 1);
        }
        commandToExecute.set(commandStr);
        semThreadWaker.release();
    }

    private boolean sendIfftTrigger (CloseableHttpClient httpClient, String triggerName, String value1, String value2, String value3)
    {
        if ( triggerName == null ) {
            return true;
        }

        LOG.info(String.format("Sending IFTTT trigger ('%s', '%s', '%s', '%s')", triggerName, value1, value2, value3));

        String json = String.format("{ \"value1\" : \"%s\", \"value2\" : \"%s\", \"value3\" : \"%s\" }", value1, value2, value3);

        HttpPut httpRequest = new HttpPut(String.format(URL_IFTTT, triggerName, configuration.getIftttMakerKey()));
        httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpRequest);
            if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) {
                LOG.debug("IFTT Trigger sent successfully");
            }
            else {
                LOG.error(String.format("IFTTT trigger was not accepted ('%s', '%s', '%s', '%s') => %s", triggerName, value1, value2,
                        value3, response.getStatusLine()));
            }
        }
        catch (IOException ex) {
            LOG.error(String.format("Error while sending IFTTT trigger ('%s', '%s', '%s', '%s')", triggerName, value1, value2, value3), ex);
            return false;
        }
        finally {
            try {
                if ( response != null ) {
                    response.close();
                }
            }
            catch (IOException ex) {
                // Ignored
            }
        }

        return true;
    }

    /**
     * Connects to the Indego server.
     * 
     * @return a connected controller instance; null, if the connection was not successful.
     */
    private IndegoController connectIndego ()
    {
        try {
            LOG.info("Connecting to Indego");
            IndegoController result = new IndegoController(configuration.getIndegoBaseUrl(), 
                    configuration.getIndegoUsername(), configuration.getIndegoPassword());
            result.connect();
            LOG.info("Connection to Indego established");
            return result;
        }
        catch (IndegoException ex) {
            LOG.error("Connection to Indego failed", ex);
            return null;
        }
    }

    /**
     * This creates a HTTP client instance for connecting the IFTTT server.
     * 
     * @return the HTTP client instance
     */
    private CloseableHttpClient buildHttpClient ()
    {
        if ( configuration.isIftttIgnoreServerCertificate() ) {
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted (X509Certificate[] chain_, String authType_) throws CertificateException
                    {
                        return true;
                    }
                });
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
                return HttpClients.custom().setSSLSocketFactory(sslsf).build();
            }
            catch (Exception ex) {
                LOG.error(ex);
                // This should never happen, but we have to handle it
                throw new RuntimeException(ex);
            }
        }
        else {
            return HttpClients.createDefault();
        }
    }

    /**
     * This creates a HTTP server instance for receiving IFTTT commands.
     * 
     * @return the HTTP server instance
     */
    private Server buildHttpServer ()
    {
        if ( configuration.getIftttReceiverPort() == 0 ) {
            return null;
        }

        HttpServlet servlet = new HttpServlet() {

            private static final long serialVersionUID = 1L;

            @Override
            protected void doGet (HttpServletRequest req_, HttpServletResponse resp_) throws ServletException, IOException
            {
                if ( req_.getPathInfo().startsWith(String.format("/%s/command/", configuration.getIftttReceiverSecret())) ) {
                    handleIftttCommand(req_, resp_);
                    resp_.setContentType("text/html");
                    resp_.setStatus(HttpStatus.SC_OK);
                }
                else {
                    super.doGet(req_, resp_);
                }
            }

        };

        Server result = new Server(configuration.getIftttReceiverPort());
        ServletHandler handler = new ServletHandler();
        result.setHandler(handler);
        handler.addServletWithMapping(new ServletHolder(servlet), "/*");
        try {
            result.start();
        }
        catch (Exception ex) {
            LOG.fatal("Was not able to start the command server", ex);
            return null;
        }

        return result;
    }

    /**
     * Disconnects a connected Indego controller.
     * 
     * @param indegoController the controller to disconnect
     */
    private void disconnect (IndegoController indegoController)
    {
        try {
            if ( indegoController != null ) {
                LOG.info("Disconnecting from Indego");
                indegoController.disconnect();
            }
        }
        catch (Exception ex) {
            LOG.warn("Something strange happened while disconnecting from Indego", ex);
        }
    }

    /**
     * Closes the HTTP client.
     * 
     * @param httpClient the HTTP client instance to close
     */
    private void disconnect (CloseableHttpClient httpClient)
    {
        try {
            if ( httpClient != null ) {
                LOG.info("Closing the HTTP client");
                httpClient.close();
            }
        }
        catch (Exception ex) {
            LOG.warn("Something strange happened while closing the HTTP client", ex);
        }
    }

    /**
     * Closes the HTTP server.
     * 
     * @param httpServer the HTTP server to close
     */
    private void disconnect (Server httpServer)
    {
        if ( httpServer != null ) {
            httpServer.destroy();
        }
    }
}

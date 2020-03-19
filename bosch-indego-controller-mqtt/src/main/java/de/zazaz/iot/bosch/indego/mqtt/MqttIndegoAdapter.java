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
package de.zazaz.iot.bosch.indego.mqtt;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import de.zazaz.iot.bosch.indego.DeviceCommand;
import de.zazaz.iot.bosch.indego.DeviceStateInformation;
import de.zazaz.iot.bosch.indego.DeviceStatus;
import de.zazaz.iot.bosch.indego.IndegoController;
import de.zazaz.iot.bosch.indego.IndegoException;
import de.zazaz.iot.bosch.indego.IndegoInvalidCommandException;

/**
 * This class connects to an Indego device and MQTT broker and maps the device status and commands to/from MQTT topics.
 */
public class MqttIndegoAdapter {

    public static final String MQTT_TOPIC_COMMAND = "command";

    public static final String MQTT_TOPIC_ONLINE = "online";

    public static final String MQTT_TOPIC_STATE_CODE = "stateCode";

    public static final String MQTT_TOPIC_ERROR_CODE = "errorCode";

    public static final String MQTT_TOPIC_STATE_MESSAGE = "stateMessage";

    public static final String MQTT_TOPIC_STATE_LEVEL = "stateLevel";

    public static final String MQTT_TOPIC_MOWED_PERCENTAGE = "mowedPercent";

    public static final String MQTT_TOPIC_MAP_SVG_CACHE_TS = "mapSvgCacheTs";

    public static final String MQTT_TOPIC_MAP_UPDATE_AVAILABLE = "mapUpdateAvailable";

    public static final String MQTT_TOPIC_MOWED_TS = "mowedTs";

    public static final String MQTT_TOPIC_MOW_MODE= "mowMode";

    public static final String MQTT_TOPIC_RUNTIME_TOTAL_OPERATE_MINS = "runtimeTotalOperationMins";

    public static final String MQTT_TOPIC_RUNTIME_TOTAL_CHARGE_MINS = "runtimeTotalChargeMins";

    public static final String MQTT_TOPIC_RUNTIME_SESSION_OPERATE_MINS = "runtimeSessionOperationMins";

    public static final String MQTT_TOPIC_RUNTIME_SESSION_CHARGE_MINS = "runtimeSessionChargeMins";

    /** the logger */
    private static final Logger LOG = LogManager.getLogger(MqttIndegoAdapter.class);

    /** the retainment flag for the data topics */
    private static final boolean RETAINMENT = true;

    /** the configuration to use */
    private final MqttIndegoAdapterConfiguration configuration;

    /** this is used for indicating, that we are request to shutdown */
    private AtomicBoolean flagShutdown = new AtomicBoolean(false);

    /** semaphore for waking worker thread up */
    private Semaphore semThreadWaker;

    /** a reference to the worker thread */
    private Thread threadWorker;

    /**
     * This class handles callbacks for commands
     */
    private class MqttIndegoCommandCallback implements MqttCallback {

        /** the command string, which was sent last (null, if there is no command) */
        private String lastCommand;

        /**
         * {@inheritDoc}
         */
        @Override
        public void connectionLost (Throwable arg0)
        {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void deliveryComplete (IMqttDeliveryToken arg0)
        {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void messageArrived (String topic, MqttMessage message) throws Exception
        {
            if ( topic.equals(configuration.getMqttTopicRoot() + MQTT_TOPIC_COMMAND) ) {
                String command = new String(message.getPayload()).trim();
                lastCommand = "".equals(command) ? null : command;
                Thread worker = threadWorker;
                if ( worker != null ) {
                    semThreadWaker.release();
                }
            }
        }

        /**
         * @return the command string, which was sent last (null, if there is no command)
         */
        public String getLastCommand ()
        {
            return lastCommand;
        }

    }

    /**
     * Initializes the MqttAdapter.
     *
     * @param configuration_ the configuration to use
     */
    public MqttIndegoAdapter (MqttIndegoAdapterConfiguration configuration_)
    {
        // Get a clone, since we don't want to be manipulated during runtime
        configuration = (MqttIndegoAdapterConfiguration) configuration_.clone();
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
            while ( true ) {
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
            while ( !flagShutdown.get() ) {
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
        MqttClient mqttClient = null;
        IndegoController indegoController = null;
        MqttIndegoCommandCallback callback = new MqttIndegoCommandCallback();
        Semaphore semWakeup = semThreadWaker;

        try {
            while ( !flagShutdown.get() ) {
                if ( mqttClient == null ) {
                    LOG.info("No MQTT connection. Creating connection.");
                    mqttClient = connectMqtt(callback);
                    if ( mqttClient == null ) {
                        LOG.warn("Was not able to connect to MQTT broker.");
                    }
                }
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
                    }
                }

                if ( mqttClient != null ) {
                    try {
                        if ( currentState != null ) {
                            pushMqttStateOnline(mqttClient, currentState);
                        }
                        else {
                            pushMqttStateOffline(mqttClient);
                        }

                        String deviceCommand = fetchDeviceCommand(mqttClient, callback);
                        if ( indegoController != null && deviceCommand != null ) {
                            LOG.info(String.format("Processing command '%s'", deviceCommand));
                            try {
                                try {
                                    indegoController.sendCommand(DeviceCommand.valueOf(deviceCommand));
                                }
                                catch (IndegoInvalidCommandException ex) {
                                    LOG.warn(String.format("The sent command '%s' was not be processed by the "
                                            + "server because it's invalid in the current device state, ignoring it", deviceCommand), ex);
                                }
                                catch (IllegalArgumentException ex) {
                                    LOG.warn(String.format("Received invalid command '%s', ignoring it", deviceCommand));
                                }
                                clearMqttDeviceCommand(mqttClient);
                            }
                            catch (Exception ex) {
                                LOG.error("Exception during sending command to Indego", ex);
                                disconnect(indegoController);
                                indegoController = null;
                            }
                        }
                    }
                    catch (Exception ex) {
                        LOG.error("Exception during pushing state to MQTT or fetching device command from MQTT", ex);
                        disconnect(mqttClient);
                        mqttClient = null;
                    }
                }

                try {
                    semWakeup.tryAcquire(configuration.getPollingIntervalMs(), TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException ex) {
                    // Ignored
                }
            }
        }
        finally {
            disconnect(mqttClient);
            disconnect(indegoController);
        }
    }

    /**
     * Publishes a single topic on the MQTT broker
     *
     * @param mqttClient the broker connection
     * @param topic the topic to publish (relative to configured topic root)
     * @param data the data to publish
     * @param retained if the data should be retained
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void publish (MqttClient mqttClient, String topic, boolean data, boolean retained)
            throws MqttPersistenceException, MqttException
    {
        publish(mqttClient, topic, data ? "1" : "0", retained);
    }

    /**
     * Publishes a single topic on the MQTT broker
     *
     * @param mqttClient the broker connection
     * @param topic the topic to publish (relative to configured topic root)
     * @param data the data to publish
     * @param retained if the data should be retained
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void publish (MqttClient mqttClient, String topic, int data, boolean retained) throws MqttPersistenceException, MqttException
    {
        publish(mqttClient, topic, Integer.toString(data), retained);
    }

    /**
     * Publishes a single topic on the MQTT broker
     *
     * @param mqttClient the broker connection
     * @param topic the topic to publish (relative to configured topic root)
     * @param data the data to publish
     * @param retained if the data should be retained
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void publish (MqttClient mqttClient, String topic, long data, boolean retained) throws MqttPersistenceException, MqttException
    {
        publish(mqttClient, topic, Long.toString(data), retained);
    }

    /**
     * Publishes a single topic on the MQTT broker
     *
     * @param mqttClient the broker connection
     * @param topic the topic to publish (relative to configured topic root)
     * @param data the data to publish
     * @param retained if the data should be retained
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void publish (MqttClient mqttClient, String topic, String data, boolean retained) throws MqttPersistenceException, MqttException
    {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(String.format("Publishing '%s' to topic '%s' (retained = %s)", data, topic, retained));
        }

        MqttMessage msg = new MqttMessage(data.getBytes());
        msg.setQos(configuration.getMqttQos());
        msg.setRetained(retained);
        mqttClient.publish(configuration.getMqttTopicRoot() + topic, msg);
    }

    /**
     * Fetches the last unprocessed command, which was sent to the adapter.
     *
     * @param mqttClient the connection to use
     * @param callback the callback, which processes published messages
     * @return the command to executre (null, if there is none)
     */
    private String fetchDeviceCommand (MqttClient mqttClient, MqttIndegoCommandCallback callback)
    {
        return callback.getLastCommand();
    }

    /**
     * This clears the command topic after processing.
     *
     * @param mqttClient the connection to use
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void clearMqttDeviceCommand (MqttClient mqttClient) throws MqttPersistenceException, MqttException
    {
        publish(mqttClient, MQTT_TOPIC_COMMAND, "", true);
    }

    /**
     * This writes the given device state to the data topics and sets the online state topic to true.
     *
     * @param mqttClient the connection to use
     * @param state the Indego state to write out
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void pushMqttStateOnline (MqttClient mqttClient, DeviceStateInformation state) throws MqttPersistenceException, MqttException
    {
        LOG.info("Pushing online state to MQTT");

        DeviceStatus status = DeviceStatus.decodeStatusCode(state.getState());

        int stateLevel = -1;
        if (status.getAssociatedCommand() != null){
          switch ( status.getAssociatedCommand() ) {
          case MOW:
              stateLevel = 2;
              break;
          case PAUSE:
              stateLevel = 1;
              break;
          case RETURN:
              stateLevel = 0;
              break;
          default:
              stateLevel = -1;
              break;
          }
        }
        if ( state.getError() != 0 ) {
            stateLevel = -1;
        }

        publish(mqttClient, MQTT_TOPIC_ONLINE, true, true);
        publish(mqttClient, MQTT_TOPIC_STATE_CODE, status.getCode(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_STATE_MESSAGE, status.getMessage(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_ERROR_CODE, state.getError(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_STATE_LEVEL, stateLevel, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MOWED_PERCENTAGE, state.getMowed(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MAP_SVG_CACHE_TS, state.getMapSvgCacheTimestamp(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MAP_UPDATE_AVAILABLE, state.isMapUpdateAvailable(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MOWED_TS, state.getMowedTimestamp(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MOW_MODE, state.getMowMode(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_TOTAL_OPERATE_MINS, state.getRuntime().getTotal().getOperate(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_TOTAL_CHARGE_MINS, state.getRuntime().getTotal().getCharge(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_SESSION_OPERATE_MINS, state.getRuntime().getSession().getOperate(), RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_SESSION_CHARGE_MINS, state.getRuntime().getSession().getCharge(), RETAINMENT);
    }

    /**
     * This marks the Indego device as offline.
     *
     * @param mqttClient the connection to use
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void pushMqttStateOffline (MqttClient mqttClient) throws MqttPersistenceException, MqttException
    {
        LOG.info("Pushing offline state to MQTT");

        publish(mqttClient, MQTT_TOPIC_ONLINE, false, true);
        publish(mqttClient, MQTT_TOPIC_STATE_CODE, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_STATE_MESSAGE, "", RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_ERROR_CODE, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_STATE_LEVEL, -2, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MOWED_PERCENTAGE, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MAP_SVG_CACHE_TS, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MAP_UPDATE_AVAILABLE, false, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_MOWED_TS, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_TOTAL_OPERATE_MINS, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_TOTAL_CHARGE_MINS, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_SESSION_OPERATE_MINS, 0, RETAINMENT);
        publish(mqttClient, MQTT_TOPIC_RUNTIME_SESSION_CHARGE_MINS, 0, RETAINMENT);
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
     * Connects to the MQTT broker by using a given notification callback.
     *
     * @param callback the callback to use
     * @return a connected client instance; null, if the connection was not successful.
     */
    private MqttClient connectMqtt (MqttIndegoCommandCallback callback)
    {
        MqttClient result = null;
        try {
            LOG.info("Connecting to MQTT broker");
            result = new MqttClient(configuration.getMqttBroker(), configuration.getMqttClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(configuration.getMqttUsername());
            options.setPassword(configuration.getMqttPassword().toCharArray());
            options.setWill(configuration.getMqttTopicRoot() + MQTT_TOPIC_ONLINE, "0".getBytes(), 1, true);
            result.setCallback(callback);
            result.connect(options);
            LOG.info("Connection to MQTT broker established");
            LOG.info("Subscribing to MQTT command topics");
            result.subscribe(configuration.getMqttTopicRoot() + MQTT_TOPIC_COMMAND);
            return result;
        }
        catch (MqttException ex) {
            LOG.error("Connection to MQTT broker failed", ex);
            try {
                if ( result.isConnected() ) {
                    result.disconnectForcibly();
                }
            }
            catch (Exception ex2) {
                // Ignored
            }
            return null;
        }
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
     * Disconnects a connected MQTT client.
     *
     * @param mqttClient the client to disconnect
     */
    private void disconnect (MqttClient mqttClient)
    {
        try {
            if ( mqttClient != null ) {
                LOG.info("Disconnecting from MQTT broker");
                try {
                    LOG.debug("Resetting online state topic");
                    publish(mqttClient, MQTT_TOPIC_ONLINE, false, true);
                }
                catch (MqttException ex) {
                    LOG.warn("Was not able to reset the online state topic.", ex);
                }
                try {
                    LOG.debug("Doing MQTT disconnect");
                    mqttClient.disconnect();
                }
                catch (MqttException ex) {
                    LOG.warn("Was not able to disconnect from MQTT broker normally, forcing disconnect.", ex);
                    mqttClient.disconnectForcibly();
                }
            }
        }
        catch (Exception ex) {
            LOG.warn("Something strange happened while disconnecting from MQTT broker", ex);
        }
    }

}

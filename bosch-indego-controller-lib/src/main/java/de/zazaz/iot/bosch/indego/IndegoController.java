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
package de.zazaz.iot.bosch.indego;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Instances of this class handle the communcation the the Indego device and provices methods
 * to control and query the device.
 */
public class IndegoController {

	/** the default url which provices the service for controlling the device */ 
    public static final String BASE_URL_DEFAULT = "https://api.indego.iot.bosch-si.com/api/v1/";
    
    /** for limiting the amount of status query requests on the server a minimum interval is specified */
    public static final long MIN_STATE_QUERY_INTERVAL_MS = 60 * 1000;
    
    /** the url which provices the service for controlling the device */ 
    private final String baseUrl;

    /** for mapping between JSON strings and POJOs */
    private final ObjectMapper mapper = new ObjectMapper();

    /** the encoded authenticated string for basic authentication */
    private final String authentication;

    /** the http client instance for communicating to the server */
    private CloseableHttpClient httpClient;

    /** the respons, which was sent by the server after a successfull authentication (contains session, device serial, etc.) */
    private AuthenticationResponse session;
    
    /** the last timestamp, when the device status was queried */
    private long lastStateQueryTs;
    
    /** this stores the result of the last device status query */
    private DeviceStateInformation deviceStateCache;
    
    /**
     * This initializes the controller instance, but does not connect yet.
     * 
     * @param baseUrl_ the url which provices the service for controlling the device;
     *      if null, the default base url is used
     * @param username_ the username for authenticating
     * @param password_ the password for authenticating
     */
    public IndegoController (String baseUrl_, String username_, String password_)
    {
        baseUrl = baseUrl_ == null ? BASE_URL_DEFAULT : normalizeBaseUrl(baseUrl_);
        authentication = Base64.encodeBase64String((username_ + ":" + password_).getBytes());
    }

    /**
     * This initializes the controller instance, but does not connect yet.
     * 
     * @param username_ the username for authenticating
     * @param password_ the password for authenticating
     */
    public IndegoController (String username_, String password_)
    {
        baseUrl = BASE_URL_DEFAULT;
        authentication = Base64.encodeBase64String((username_ + ":" + password_).getBytes());
    }

    /**
     * @param baseUrl_ the user specified base url
     * @return the reformatted and normalized base url
     */
    private String normalizeBaseUrl (String baseUrl_)
    {
        return baseUrl_.endsWith("/") ? baseUrl_ : baseUrl_ + "/";
    }

    /**
     * This connects to the server and authenticates the session.
     * 
     * @throws IndegoAuthenticationException in case of wrong authentication informations
     * @throws IndegoException in case of any unexpected event
     */
    public void connect () throws IndegoAuthenticationException, IndegoException
    {
        if ( httpClient != null ) {
            throw new IndegoException("You are already connected");
        }

        try {
            httpClient = HttpClients.createDefault();
            session = doAuthenticate();
            lastStateQueryTs = 0;
        }
        catch (IndegoException ex) {
            safeCloseClient();
            throw ex;
        }
        catch (Exception ex) {
            safeCloseClient();
            throw new IndegoException(ex);
        }
    }

    /**
     * This disconnects from the server und shuts down the session.
     */
    public void disconnect ()
    {
        safeCloseClient();
    }

    /**
     * @return the serial number of the associated Indego device
     * @throws IndegoException in case of any unexpected event
     */
    public String getDeviceSerialNumber () throws IndegoException
    {
        return session.getAlmSn();
    }

    /**
     * This queries the device state from the server or returns a cached state if the
     * last query was lass than <code>MIN_STATE_QUERY_INTERVAL_MS</code> milliseconds ago.
     * 
     * @return the device state
     * @throws IndegoException in case of any unexpected event
     */
    public DeviceStateInformation getState () throws IndegoException
    {
    	synchronized ( this ) {
    		if ( deviceStateCache != null 
    				&& System.currentTimeMillis() - MIN_STATE_QUERY_INTERVAL_MS < lastStateQueryTs ) {
    			return deviceStateCache;
    		}
	        DeviceStateInformation state = doGetRequest("alms/" + session.getAlmSn() + "/state",
	                DeviceStateInformation.class);
	        deviceStateCache = state;
	        lastStateQueryTs = System.currentTimeMillis();
	        return state;
    	}
    }
    
    public DeviceCalendar getCalendar () throws IndegoException
    {
        synchronized ( this ) {
            DeviceCalendar calendar = doGetRequest("alms/" + session.getAlmSn() + "/calendar",
                    DeviceCalendar.class);
            return calendar;
        }
    }

    /**
     * This sends a command to the Indego device. 
     * 
     * @param command_ the control command to send to the device.
     * @throws IndegoInvalidCommandException if the command was not processed correctly
     * @throws IndegoException in case of any unexpected event
     */
    public void sendCommand (DeviceCommand command_) throws IndegoInvalidCommandException, IndegoException
    {
    	lastStateQueryTs = 0;
        SetStateRequest request = new SetStateRequest();
        request.setState(command_.getActionCode());
        doPutRequest("alms/" + session.getAlmSn() + "/state", request, null);
    }

    /**
     * Closes the connection and takes care of error handling.
     */
    private void safeCloseClient ()
    {
        try {
        	if ( httpClient != null ) {
        		httpClient.close();
        	}
        }
        catch (IOException ex) {
            // Ignored
        }
        httpClient = null;
    }

    /**
     * This sends an authentication request to the server and unmarshals the result.
     * 
     * @return the result of the authentication request, when the authentication was
     * 		successfull.
     * @throws IndegoAuthenticationException in case of wrong authentication informations
     * @throws IndegoException in case of any unexpected event
     */
    private AuthenticationResponse doAuthenticate () throws IndegoAuthenticationException, IndegoException
    {
        try {
            HttpPost httpPost = new HttpPost(baseUrl + "authenticate");
            httpPost.addHeader("Authorization", "Basic " + authentication);

            AuthenticationRequest authRequest = new AuthenticationRequest();
            authRequest.setDevice("");
            authRequest.setOsType("Android");
            authRequest.setOsVersion("4.0");
            authRequest.setDeviceManufacturer("unknown");
            authRequest.setDeviceType("unknown");
            String json = mapper.writeValueAsString(authRequest);
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            CloseableHttpResponse response = httpClient.execute(httpPost);

            int status = response.getStatusLine().getStatusCode();
            if ( status == HttpStatus.SC_UNAUTHORIZED ) {
                throw new IndegoAuthenticationException("Was not able to authenticate");
            }
            if ( status != HttpStatus.SC_OK && status != HttpStatus.SC_CREATED ) {
                throw new IndegoAuthenticationException("The request failed with error: "
                        + response.getStatusLine().toString());
            }

            String responseContents = EntityUtils.toString(response.getEntity());
            AuthenticationResponse authResponse = mapper.readValue(responseContents,
                    AuthenticationResponse.class);

            return authResponse;
        }
        catch (IOException ex) {
            throw new IndegoException(ex);
        }
    }

    /**
     * This sends a GET request to the server and unmarshals the JSON result.
     * 
     * @param urlSuffix the path, to which the request should be sent
     * @param returnType the class to which the JSON result should be mapped; if null,
     * 		no mapping is tried and null is returned.
     * @return the mapped result of the request
     * @throws IndegoException in case of any unexpected event
    */
    private <T> T doGetRequest (String urlSuffix, Class<? extends T> returnType) throws IndegoException
    {
        try {
            HttpGet httpRequest = new HttpGet(baseUrl + urlSuffix);
            httpRequest.setHeader("x-im-context-id", session.getContextId());
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            if ( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK ) {
                throw new IndegoAuthenticationException("The request failed with error: "
                        + response.getStatusLine().toString());
            }
            String responseContents = EntityUtils.toString(response.getEntity());
            if ( returnType == null ) {
                return null;
            }
            else {
            	if (returnType==String.class)
            		return (T) responseContents;
                T result = mapper.readValue(responseContents, returnType);
                return result;
            }
        }
        catch (IOException ex) {
            throw new IndegoException(ex);
        }
    }

    /**
     * This sends a PUT request to the server and unmarshals the JSON result.
     * 
     * @param urlSuffix the path, to which the request should be sent
     * @param request the data, which should be sent to the server (mapped to JSON)
     * @param returnType the class to which the JSON result should be mapped; if null,
     * 		no mapping is tried and null is returned.
     * @return the mapped result of the request
     * @throws IndegoException in case of any unexpected event
    */
    private <T> T doPutRequest (String urlSuffix, Object request, Class<? extends T> returnType)
            throws IndegoException
    {
        try {
            HttpPut httpRequest = new HttpPut(baseUrl + urlSuffix);
            httpRequest.setHeader("x-im-context-id", session.getContextId());
            String json = mapper.writeValueAsString(request);
            httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR ) {
                throw new IndegoInvalidCommandException("The request failed with error: "
                        + response.getStatusLine().toString());
            }
            if ( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK ) {
                throw new IndegoAuthenticationException("The request failed with error: "
                        + response.getStatusLine().toString());
            }
            String responseContents = EntityUtils.toString(response.getEntity());
            if ( returnType == null ) {
                return null;
            }
            else {
                T result = mapper.readValue(responseContents, returnType);
                return result;
            }
        }
        catch (IOException ex) {
            throw new IndegoException(ex);
        }
    }
    /**
     * this queries the predictive weather forecast
     * 
     * @return the wether forecast
     * @throws IndegoException in case of any unexpected event
     */
    public LocationWeather getWeather() throws IndegoException
    {
        synchronized (this)
        {
            final LocationWeather weather = doGetRequest("alms/" + session.getAlmSn() + "/predictive/weather",
                    LocationWeather.class);
            System.out.println(weather);
            return weather;
        }
    }

    public int getPredictiveAdjustment() throws IndegoException
    {
        synchronized (this)
        {
            final PredictiveAdjustment adjustment = doGetRequest(
                    "alms/" + session.getAlmSn() + "/predictive/useradjustment", PredictiveAdjustment.class);
            return adjustment.getAdjustment();
        }
    }

    public void setPredictiveAdjustment(final int adjust) throws IndegoException
    {
        synchronized (this)
        {
            final PredictiveAdjustment adjustment = new PredictiveAdjustment();
            adjustment.setAdjustment(adjust);
            doPutRequest("alms/" + session.getAlmSn() + "/predictive/useradjustment", adjustment, null);
        }
    }

    public boolean getPredictiveMoving() throws IndegoException
    {
        synchronized (this)
        {
            final PredictiveStatus status = doGetRequest("alms/" + session.getAlmSn() + "/predictive",
                    PredictiveStatus.class);
            return status.isEnabled();
        }
    }

    public void setPredictiveMoving(final boolean enable) throws IndegoException
    {
        synchronized (this)
        {
            final PredictiveStatus status = new PredictiveStatus();
            status.setEnabled(enable);
            doPutRequest("alms/" + session.getAlmSn() + "/predictive", status, null);
        }
    }

    public Date getPredictiveNextCutting() throws IndegoException
    {
        synchronized (this)
        {
            final PredictiveCuttingTime nextCutting = doGetRequest(
                    "alms/" + session.getAlmSn() + "/predictive/nextcutting", PredictiveCuttingTime.class);
            return nextCutting.getNextCuttingAsDate();
        }
    }

    public DeviceCalendar getPredictiveExclusionTime() throws IndegoException
    {
        final DeviceCalendar calendar = doGetRequest("alms/" + session.getAlmSn() + "/predictive/calendar",
                DeviceCalendar.class);
        return calendar;
    }

    public void setPredictiveExclusionTime(final DeviceCalendar calendar) throws IndegoException
    {
        doPutRequest("alms/" + session.getAlmSn() + "/predictive/calendar", calendar, null);
    }

	public void downloadMap(File file) {
		try {
			String svgFile=doGetRequest("alms/" + session.getAlmSn() + "/map", String.class);
			try(FileOutputStream fos=new FileOutputStream(file);){
				fos.write(svgFile.getBytes());
				System.out.println("Wrote SVG map file to:"+file.getAbsolutePath());
			}
		} catch (IOException|IndegoException e) {
			e.printStackTrace();
		}
	}

}

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

/**
 * This contains the configuration to initialize the IFTTT adapter.
 */
public class IftttIndegoAdapterConfiguration implements Cloneable {

    /** The personal key of the maker channel to send events */
    private String iftttMakerKey;

    /** The port number for receiving IFTTT events (0: disabled) */
    private int iftttReceiverPort;

    /** An string, which is built into the receiver path (as password) */
    private String iftttReceiverSecret;

    /** If set to true, the IFTTT server certificate is not checked (required for older Java versions) */
    private boolean iftttIgnoreServerCertificate;

    /** The name of the offline event (null, if no event should be sent) */
    private String iftttOfflineEventName;

    /** The name of the offline event (null, if no event should be sent) */
    private String iftttOnlineEventName;

    /** The name of the state change event (null, if no event should be sent) */
    private String iftttStateChangeEventName;

    /** The name of the error event (null, if no event should be sent) */
    private String iftttErrorEventName;

    /** The name of the error cleared event (null, if no event should be sent) */
    private String iftttErrorClearedEventName;
    
    /** Base url of the Indego web service (if different from default) */
    private String indegoBaseUrl;

    /** the username for connecting to the Indego server */
    private String indegoUsername;

    /** the password for connecting to the Indego server */
    private String indegoPassword;

    /** the polling interval (in ms) */
    private int pollingIntervalMs;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone ()
    {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the personal key of the maker channel to send events.
     *
     * @return the personal key of the maker channel to send events
     */
    public String getIftttMakerKey ()
    {
        return iftttMakerKey;
    }

    /**
     * Sets the personal key of the maker channel to send events.
     *
     * @param iftttMakerKey_ the new personal key of the maker channel to send events
     */
    public void setIftttMakerKey (String iftttMakerKey_)
    {
        iftttMakerKey = iftttMakerKey_;
    }

    /**
     * Gets the port number for receiving IFTTT events (0: disabled).
     *
     * @return the port number for receiving IFTTT events (0: disabled)
     */
    public int getIftttReceiverPort ()
    {
        return iftttReceiverPort;
    }

    /**
     * Sets the port number for receiving IFTTT events (0: disabled).
     *
     * @param iftttReceiverPort_ the new port number for receiving IFTTT events (0: disabled)
     */
    public void setIftttReceiverPort (int iftttReceiverPort_)
    {
        iftttReceiverPort = iftttReceiverPort_;
    }

    /**
     * Gets the an string, which is built into the receiver path (as password).
     *
     * @return the an string, which is built into the receiver path (as password)
     */
    public String getIftttReceiverSecret ()
    {
        return iftttReceiverSecret;
    }

    /**
     * Sets the an string, which is built into the receiver path (as password).
     *
     * @param iftttReceiverSecret_ the new an string, which is built into the receiver path (as password)
     */
    public void setIftttReceiverSecret (String iftttReceiverSecret_)
    {
        iftttReceiverSecret = iftttReceiverSecret_;
    }

    /**
     * Checks if is if set to true, the IFTTT server certificate is not checked (required for older Java versions).
     *
     * @return the if set to true, the IFTTT server certificate is not checked (required for older Java versions)
     */
    public boolean isIftttIgnoreServerCertificate ()
    {
        return iftttIgnoreServerCertificate;
    }

    /**
     * Sets the if set to true, the IFTTT server certificate is not checked (required for older Java versions).
     *
     * @param iftttIgnoreServerCertificate_ the new if set to true, the IFTTT server certificate is not checked (required for older Java versions)
     */
    public void setIftttIgnoreServerCertificate (boolean iftttIgnoreServerCertificate_)
    {
        iftttIgnoreServerCertificate = iftttIgnoreServerCertificate_;
    }

    /**
     * Gets the name of the offline event (null, if no event should be sent).
     *
     * @return the name of the offline event (null, if no event should be sent)
     */
    public String getIftttOfflineEventName ()
    {
        return iftttOfflineEventName;
    }

    /**
     * Sets the name of the offline event (null, if no event should be sent).
     *
     * @param iftttOfflineEventName_ the new name of the offline event (null, if no event should be sent)
     */
    public void setIftttOfflineEventName (String iftttOfflineEventName_)
    {
        iftttOfflineEventName = iftttOfflineEventName_;
    }

    /**
     * Gets the name of the offline event (null, if no event should be sent).
     *
     * @return the name of the offline event (null, if no event should be sent)
     */
    public String getIftttOnlineEventName ()
    {
        return iftttOnlineEventName;
    }

    /**
     * Sets the name of the offline event (null, if no event should be sent).
     *
     * @param iftttOnlineEventName_ the new name of the offline event (null, if no event should be sent)
     */
    public void setIftttOnlineEventName (String iftttOnlineEventName_)
    {
        iftttOnlineEventName = iftttOnlineEventName_;
    }

    /**
     * Gets the name of the state change event (null, if no event should be sent).
     *
     * @return the name of the state change event (null, if no event should be sent)
     */
    public String getIftttStateChangeEventName ()
    {
        return iftttStateChangeEventName;
    }

    /**
     * Sets the name of the state change event (null, if no event should be sent).
     *
     * @param iftttStateChangeEventName_ the new name of the state change event (null, if no event should be sent)
     */
    public void setIftttStateChangeEventName (String iftttStateChangeEventName_)
    {
        iftttStateChangeEventName = iftttStateChangeEventName_;
    }

    /**
     * Gets the name of the error event (null, if no event should be sent).
     *
     * @return the name of the error event (null, if no event should be sent)
     */
    public String getIftttErrorEventName ()
    {
        return iftttErrorEventName;
    }

    /**
     * Sets the name of the error event (null, if no event should be sent).
     *
     * @param iftttErrorEventName_ the new name of the error event (null, if no event should be sent)
     */
    public void setIftttErrorEventName (String iftttErrorEventName_)
    {
        iftttErrorEventName = iftttErrorEventName_;
    }

    /**
     * Gets the name of the error cleared event (null, if no event should be sent).
     *
     * @return the name of the error cleared event (null, if no event should be sent)
     */
    public String getIftttErrorClearedEventName ()
    {
        return iftttErrorClearedEventName;
    }

    /**
     * Sets the name of the error cleared event (null, if no event should be sent).
     *
     * @param iftttErrorClearedEventName_ the new name of the error cleared event (null, if no event should be sent)
     */
    public void setIftttErrorClearedEventName (String iftttErrorClearedEventName_)
    {
        iftttErrorClearedEventName = iftttErrorClearedEventName_;
    }
    
    /**
     * Gets the base url of the Indego web service (if different from default)
     *
     * @return the base url of the Indego web service (if different from default)
     */
    public String getIndegoBaseUrl ()
    {
        return indegoBaseUrl;
    }

    /**
     * Sets the base url of the Indego web service (if different from default)
     *
     * @param indegoBaseUrl the base url of the Indego web service (if different from default) 
     */
    public void setIndegoBaseUrl (String indegoBaseUrl)
    {
        this.indegoBaseUrl = indegoBaseUrl;
    }

    /**
     * Gets the username for connecting to the Indego server.
     * 
     * @return the username for connecting to the Indego server
     */
    public String getIndegoUsername ()
    {
        return indegoUsername;
    }

    /**
     * Sets the username for connecting to the Indego server.
     * 
     * @param indegoUsername the new username for connecting to the Indego server
     */
    public void setIndegoUsername (String indegoUsername)
    {
        this.indegoUsername = indegoUsername;
    }

    /**
     * Gets the password for connecting to the Indego server.
     * 
     * @return the password for connecting to the Indego server
     */
    public String getIndegoPassword ()
    {
        return indegoPassword;
    }

    /**
     * Sets the password for connecting to the Indego server.
     * 
     * @param indegoPassword the new password for connecting to the Indego server
     */
    public void setIndegoPassword (String indegoPassword)
    {
        this.indegoPassword = indegoPassword;
    }

    /**
     * Gets the polling interval (in ms).
     * 
     * @return the polling interval (in ms)
     */
    public int getPollingIntervalMs ()
    {
        return pollingIntervalMs;
    }

    /**
     * Sets the polling interval (in ms).
     * 
     * @param pollingIntervalMs the new polling interval (in ms)
     */
    public void setPollingIntervalMs (int pollingIntervalMs)
    {
        this.pollingIntervalMs = pollingIntervalMs;
    }

}

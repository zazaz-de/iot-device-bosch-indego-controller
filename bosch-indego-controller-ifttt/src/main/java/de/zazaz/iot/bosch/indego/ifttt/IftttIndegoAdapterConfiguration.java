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

    public String getIftttMakerKey ()
    {
        return iftttMakerKey;
    }

    public void setIftttMakerKey (String iftttMakerKey_)
    {
        iftttMakerKey = iftttMakerKey_;
    }

    public int getIftttReceiverPort ()
    {
        return iftttReceiverPort;
    }

    public void setIftttReceiverPort (int iftttReceiverPort_)
    {
        iftttReceiverPort = iftttReceiverPort_;
    }

    public String getIftttReceiverSecret ()
    {
        return iftttReceiverSecret;
    }

    public void setIftttReceiverSecret (String iftttReceiverSecret_)
    {
        iftttReceiverSecret = iftttReceiverSecret_;
    }

    public boolean isIftttIgnoreServerCertificate ()
    {
        return iftttIgnoreServerCertificate;
    }

    public void setIftttIgnoreServerCertificate (boolean iftttIgnoreServerCertificate_)
    {
        iftttIgnoreServerCertificate = iftttIgnoreServerCertificate_;
    }

    public String getIftttOfflineEventName ()
    {
        return iftttOfflineEventName;
    }

    public void setIftttOfflineEventName (String iftttOfflineEventName_)
    {
        iftttOfflineEventName = iftttOfflineEventName_;
    }

    public String getIftttOnlineEventName ()
    {
        return iftttOnlineEventName;
    }

    public void setIftttOnlineEventName (String iftttOnlineEventName_)
    {
        iftttOnlineEventName = iftttOnlineEventName_;
    }

    public String getIftttStateChangeEventName ()
    {
        return iftttStateChangeEventName;
    }

    public void setIftttStateChangeEventName (String iftttStateChangeEventName_)
    {
        iftttStateChangeEventName = iftttStateChangeEventName_;
    }

    public String getIftttErrorEventName ()
    {
        return iftttErrorEventName;
    }

    public void setIftttErrorEventName (String iftttErrorEventName_)
    {
        iftttErrorEventName = iftttErrorEventName_;
    }

    public String getIftttErrorClearedEventName ()
    {
        return iftttErrorClearedEventName;
    }

    public void setIftttErrorClearedEventName (String iftttErrorClearedEventName_)
    {
        iftttErrorClearedEventName = iftttErrorClearedEventName_;
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

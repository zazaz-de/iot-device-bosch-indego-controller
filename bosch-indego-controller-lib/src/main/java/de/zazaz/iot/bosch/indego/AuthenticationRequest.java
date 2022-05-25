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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * POJO for creating JSON request for authenticating to server
 */
@JsonPropertyOrder({ "accept_tc_id", "device", "os_type", "os_version", "dvc_manuf", "dvc_type" })
public class AuthenticationRequest {

    private String accept_tc_id;

    private String device;

    private String osType;

    private String osVersion;

    private String deviceManufacturer;

    private String deviceType;

    @JsonGetter("accept_tc_id")
    public String getAcceptTcId ()
    {
        return accept_tc_id;
    }

    @JsonSetter("accept_tc_id")
    public void setAcceptTcId (String accept_tc_id_)
    {
        accept_tc_id = accept_tc_id_;
    }

    @JsonGetter("device")
    public String getDevice ()
    {
        return device;
    }

    @JsonSetter("device")
    public void setDevice (String device_)
    {
        device = device_;
    }

    @JsonGetter("os_type")
    public String getOsType ()
    {
        return osType;
    }

    @JsonSetter("os_type")
    public void setOsType (String osType_)
    {
        osType = osType_;
    }

    @JsonGetter("os_version")
    public String getOsVersion ()
    {
        return osVersion;
    }

    @JsonSetter("os_version")
    public void setOsVersion (String osVersion_)
    {
        osVersion = osVersion_;
    }

    @JsonGetter("dvc_manuf")
    public String getDeviceManufacturer ()
    {
        return deviceManufacturer;
    }

    @JsonSetter("dvc_manuf")
    public void setDeviceManufacturer (String deviceManufacturer_)
    {
        deviceManufacturer = deviceManufacturer_;
    }

    @JsonGetter("dvc_type")
    public String getDeviceType ()
    {
        return deviceType;
    }

    @JsonSetter("dvc_type")
    public void setDeviceType (String deviceType_)
    {
        deviceType = deviceType_;
    }

}
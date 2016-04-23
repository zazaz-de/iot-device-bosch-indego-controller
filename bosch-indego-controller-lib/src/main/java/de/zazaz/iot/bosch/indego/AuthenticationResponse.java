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
 * POJO for parsing JSON response after authenticating to server
 */
@JsonPropertyOrder({ "contextId", "userId", "alm_sn" })
public class AuthenticationResponse {

    private String contextId;

    private String userId;

    private String almSn;

    @JsonGetter("contextId")
    public String getContextId ()
    {
        return contextId;
    }

    @JsonSetter("contextId")
    public void setContextId (String contextId_)
    {
        contextId = contextId_;
    }

    @JsonGetter("userId")
    public String getUserId ()
    {
        return userId;
    }

    @JsonSetter("userId")
    public void setUserId (String userId_)
    {
        userId = userId_;
    }

    @JsonGetter("alm_sn")
    public String getAlmSn ()
    {
        return almSn;
    }

    @JsonSetter("alm_sn")
    public void setAlmSn (String almSn_)
    {
        almSn = almSn_;
    }

}

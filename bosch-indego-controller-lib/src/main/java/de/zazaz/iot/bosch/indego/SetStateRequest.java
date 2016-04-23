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
 * POJO for creating JSON request for setting a new device state
 */
@JsonPropertyOrder({ "state" })
public class SetStateRequest {

    public final static String STATE_MOW = "mow";

    public final static String STATE_PAUSE = "pause";

    public final static String STATE_RETURN = "returnToDock";

    private String state;

    @JsonGetter("state")
    public String getState ()
    {
        return state;
    }

    @JsonSetter("state")
    public void setState (String state_)
    {
        state = state_;
    }

}

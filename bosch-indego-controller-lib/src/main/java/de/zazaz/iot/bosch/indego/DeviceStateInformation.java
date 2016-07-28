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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * POJO for parsing JSON response after querying the device status. This
 * contains the complete state of the device including operational mode,
 * runtime, etc.
 */
@JsonPropertyOrder({ "state", "mowed", "mowed_ts", "mapsvgcache_ts", "runtime", "error", "map_update_available" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStateInformation {

    @JsonPropertyOrder({ "total", "session" })
    public static class GetStateResponseRuntimes {

        private GetStateResponseRuntime total;

        private GetStateResponseRuntime session;

        @JsonGetter("total")
        public GetStateResponseRuntime getTotal ()
        {
            return total;
        }

        @JsonSetter("total")
        public void setTotal (GetStateResponseRuntime total_)
        {
            total = total_;
        }

        @JsonGetter("session")
        public GetStateResponseRuntime getSession ()
        {
            return session;
        }

        @JsonSetter("session")
        public void setSession (GetStateResponseRuntime session_)
        {
            session = session_;
        }

    }

    @JsonPropertyOrder({ "operate", "charge" })
    public static class GetStateResponseRuntime {

        private long operate;

        private long charge;

        @JsonGetter("operate")
        public long getOperate ()
        {
            return operate;
        }

        @JsonSetter("operate")
        public void setOperate (long operate_)
        {
            operate = operate_;
        }

        @JsonGetter("charge")
        public long getCharge ()
        {
            return charge;
        }

        @JsonSetter("charge")
        public void setCharge (long charge_)
        {
            charge = charge_;
        }

    }

    private int state;
    
    private int error;

    private int mowed;

    private long mowedTimestamp;
    
    private long mowMode;

    private long mapSvgCacheTimestamp;

    private GetStateResponseRuntimes runtime;

    private boolean mapUpdateAvailable;

    @JsonGetter("state")
    public int getState ()
    {
        return state;
    }

    @JsonSetter("state")
    public void setState (int state_)
    {
        state = state_;
    }
    
    @JsonGetter("error")
    public int getError ()
    {
        return error;
    }
    
    @JsonSetter("error")
    public void setError (int error_)
    {
        error = error_;
    }

    @JsonGetter("mowed")
    public int getMowed ()
    {
        return mowed;
    }

    @JsonSetter("mowed")
    public void setMowed (int mowed_)
    {
        mowed = mowed_;
    }

    @JsonGetter("mowed_ts")
    public long getMowedTimestamp ()
    {
        return mowedTimestamp;
    }

    @JsonSetter("mowed_ts")
    public void setMowedTimestamp (long mowed_ts_)
    {
        mowedTimestamp = mowed_ts_;
    }

    @JsonGetter("mowmode")
    public long getMowMode ()
    {
        return mowMode;
    }

    @JsonSetter("mowmode")
    public void setMowMode (long mowMode)
    {
        this.mowMode = mowMode;
    }

    @JsonGetter("mapsvgcache_ts")
    public long getMapSvgCacheTimestamp ()
    {
        return mapSvgCacheTimestamp;
    }

    @JsonSetter("mapsvgcache_ts")
    public void setMapSvgCacheTimestamp (long mapsvgcache_ts_)
    {
        mapSvgCacheTimestamp = mapsvgcache_ts_;
    }

    @JsonGetter("runtime")
    public GetStateResponseRuntimes getRuntime ()
    {
        return runtime;
    }

    @JsonSetter("runtime")
    public void setRuntime (GetStateResponseRuntimes runtime_)
    {
        runtime = runtime_;
    }

    @JsonGetter("map_update_available")
    public boolean isMapUpdateAvailable ()
    {
        return mapUpdateAvailable;
    }

    @JsonSetter("map_update_available")
    public void setMapUpdateAvailable (boolean map_update_available_)
    {
        mapUpdateAvailable = map_update_available_;
    }

}

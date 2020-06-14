/**
 * Copyright (C) 2020 Oliver Schünemann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @since 11.06.2020
 * @version 1.0
 * @author oliver
 */
package de.zazaz.iot.bosch.indego;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author oliver
 *
 */
@JsonPropertyOrder({ "enabled" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictiveStatus
{
    private boolean enabled;

    /**
     * @return the enabled
     */
    @JsonGetter("enabled")
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    @JsonSetter("enabled")
    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }
}

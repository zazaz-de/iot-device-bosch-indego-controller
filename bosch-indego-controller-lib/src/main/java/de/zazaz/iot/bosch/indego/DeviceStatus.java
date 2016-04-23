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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for describing integer status codes.
 */
public class DeviceStatus {

	/** maps known status code to describing objects */
    private static final Map<Integer, DeviceStatus> statusMap = Collections
            .synchronizedMap(new HashMap<Integer, DeviceStatus>());

    /** the numeric status code */
    private int code;

    /** a textual message */
    private String message;

    /** 
     * the command, which has been sent to the device (implicit or
     * explicit) before
     */
    private DeviceCommand associatedCommand;

    /**
     * initializes all known status codes
     */
    private static void addAllKnownStatusCodes ()
    {
        addStatus(0, "Reading status", DeviceCommand.RETURN);
        addStatus(257, "Charging", DeviceCommand.RETURN);
        addStatus(258, "Docked", DeviceCommand.RETURN);
        addStatus(259, "Docked - Software update", DeviceCommand.RETURN);
        addStatus(260, "Docked", DeviceCommand.RETURN);
        addStatus(261, "Docked", DeviceCommand.RETURN);
        addStatus(262, "Docked - Loading map", DeviceCommand.MOW);
        addStatus(263, "Docked - Saving map", DeviceCommand.RETURN);
        addStatus(513, "Mowing", DeviceCommand.MOW);
        addStatus(514, "Relocalising", DeviceCommand.MOW);
        addStatus(515, "Loading map", DeviceCommand.MOW);
        addStatus(516, "Learning lawn", DeviceCommand.MOW);
        addStatus(517, "Paused", DeviceCommand.PAUSE);
        addStatus(518, "Border cut", DeviceCommand.MOW);
        addStatus(519, "Idle in lawn", DeviceCommand.MOW);
        addStatus(769, "Returning to dock", DeviceCommand.RETURN);
        addStatus(770, "Returning to dock", DeviceCommand.RETURN);
        addStatus(771, "Returning to dock - Battery low", DeviceCommand.RETURN);
        addStatus(772, "Returning to dock - Calendar timeslot ended", DeviceCommand.RETURN);
        addStatus(773, "Returning to dock - Battery temp range", DeviceCommand.RETURN);
        addStatus(774, "Returning to dock", DeviceCommand.RETURN);
        addStatus(775, "Returning to dock - Lawn complete", DeviceCommand.RETURN);
        addStatus(776, "Returning to dock - Relocalising", DeviceCommand.RETURN);
        addStatus(1025, "Diagnostic mode", null);
        addStatus(1026, "End of live", null);
        addStatus(1281, "Software update", null);
    }

    /**
     * Add a single status code to map.
     * 
     * @param code_ the numeric status code
     * @param message_ a textual message
     * @param associatedCommand_ the command, which has been sent to the device (implicit 
     * 			or explicit) before 
     */
    private static void addStatus (int code_, String message_, DeviceCommand associatedCommand_)
    {
        statusMap.put(code_, new DeviceStatus(code_, message_, associatedCommand_));
    }

    /**
     * Creates the status code.
     * 
     * @param code_ the numeric status code
     * @param message_ a textual message
     * @param associatedCommand_ the command, which has been sent to the device (implicit 
     * 			or explicit) before 
     */
    private DeviceStatus (int code_, String message_, DeviceCommand associatedCommand_)
    {
        code = code_;
        message = message_;
        associatedCommand = associatedCommand_;
    }

    /**
     * Returns a DeviceStatus instance, which describes the status code.
     * 
     * @param code_ the code to describe
     * @return the describing status code instance
     */
    public static DeviceStatus decodeStatusCode (int code_)
    {
        if ( statusMap.isEmpty() ) {
            addAllKnownStatusCodes();
        }
        DeviceStatus status = statusMap.get(code_);
        if ( status == null ) {
            DeviceCommand command = null;
            if ( (code_ & 0xff00) == 0x100 ) {
                command = DeviceCommand.RETURN;
            }
            if ( (code_ & 0xff00) == 0x200 ) {
                command = DeviceCommand.MOW;
            }
            if ( (code_ & 0xff00) == 0x300 ) {
                command = DeviceCommand.RETURN;
            }
            status = new DeviceStatus(code_, String.format("Unknown status code %d", code_), command);
            statusMap.put(status.getCode(), status);
        }
        return status;
    }

    /**
     * @return the numeric status code
     */
    public int getCode ()
    {
        return code;
    }

    /**
     * @return a textual message
     */
    public String getMessage ()
    {
        return message;
    }

    /**
     * @return the command, which has been sent to the device (implicit 
     * 			or explicit) before 
     */
    public DeviceCommand getAssociatedCommand ()
    {
        return associatedCommand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString ()
    {
        return "DeviceStatus [code=" + code + ", message=" + message + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals (Object obj)
    {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        DeviceStatus other = (DeviceStatus) obj;
        if ( code != other.code ) {
            return false;
        }
        return true;
    }

}

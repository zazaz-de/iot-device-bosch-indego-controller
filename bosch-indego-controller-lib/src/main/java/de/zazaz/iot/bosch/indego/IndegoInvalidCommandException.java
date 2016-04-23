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

/**
 * This exception is thrown, if a command was sent correctly, but was not accepted by the
 * device.
 */
public class IndegoInvalidCommandException extends IndegoException {

    private static final long serialVersionUID = 1L;

    public IndegoInvalidCommandException ()
    {
    }

    public IndegoInvalidCommandException (String message_)
    {
        super(message_);
    }

    public IndegoInvalidCommandException (Throwable cause_)
    {
        super(cause_);
    }

    public IndegoInvalidCommandException (String message_, Throwable cause_)
    {
        super(message_, cause_);
    }

    public IndegoInvalidCommandException (String message_, Throwable cause_, boolean enableSuppression_,
            boolean writableStackTrace_)
    {
        super(message_, cause_, enableSuppression_, writableStackTrace_);
    }

}

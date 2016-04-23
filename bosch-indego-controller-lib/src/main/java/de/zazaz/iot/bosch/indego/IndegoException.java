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
 * This is the base of all Indego related exceptions. This exception is
 * thrown, if a generic error occured during communication with the
 * device.
 */
public class IndegoException extends Exception {

    private static final long serialVersionUID = 1L;

    public IndegoException ()
    {
    }

    public IndegoException (String message_)
    {
        super(message_);
    }

    public IndegoException (Throwable cause_)
    {
        super(cause_);
    }

    public IndegoException (String message_, Throwable cause_)
    {
        super(message_, cause_);
    }

    public IndegoException (String message_, Throwable cause_, boolean enableSuppression_,
            boolean writableStackTrace_)
    {
        super(message_, cause_, enableSuppression_, writableStackTrace_);
    }

}

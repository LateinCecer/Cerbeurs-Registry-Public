/*
 * Cerberus-Registry is a service registry library and the core of the
 * Cerberus Game project.
 * Visit https://cerberustek.com for more details
 * Copyright (c)  2020  Adrian Paskert
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. See the file LICENSE included with this
 * distribution for more information.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.cerberustek.logger;

import com.cerberustek.service.CerberusService;

/**
 * Container object for logs.
 */
public class LogElement {

    /** The ServiceClass of the service that caused the logln-entry */
    private final Class<? extends CerberusService> serviceClass;
    /** The message stored inside of the logln-entry */
    private final String message;
    /** The printed stack trace caused by the logln */
    private final String stackTrace;
    /** The time the information was recorded */
    private final long time;

    /**
     * Creates a logln-element with the specified inputs.
     *
     * @param serviceClass ServiceClass of the service that caused
     *                     the logln
     * @param message The message that was logged with the entry
     * @param stackTrace The printed stackTrace conveyed by
     *                   the Service that caused the entry
     * @param time The time the data was recorded
     */
    LogElement(Class<? extends CerberusService> serviceClass, String message, String stackTrace,
                      long time) {
        this.serviceClass = serviceClass;
        this.message = message;
        this.stackTrace = stackTrace;
        this.time = time;
    }

    /**
     * Returns the service that logged this message.
     *
     * @return ServiceClass
     */
    public Class<? extends CerberusService> getService() {
        return serviceClass;
    }

    /**
     * Returns the message which was logged with the entry
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the printed out stacktrace of the logln
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Returns the system time the data was recorded
     * @return System Time
     */
    public long getTime() {
        return time;
    }
}

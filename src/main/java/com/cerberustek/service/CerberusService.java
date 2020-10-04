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

package com.cerberustek.service;

import java.util.Collection;

public interface CerberusService {

    /**
     * Will cause the service to start, if it is currently
     * not running.
     */
    void start();

    /**
     * Will cause the service to shutdown, if it is currently
     * running.
     */
    void stop();

    /**
     * Returns the service class by which the service is registered
     * in the service registry.
     *
     * @return ServiceClass
     */
    Class<? extends CerberusService> serviceClass();

    /**
     * Returns a collection of all threads owned by this
     * service.
     *
     * Keep mind that this method will not list all thread that
     * the service uses, but only those that it owns. If, for
     * example, the service host some kind of scheduling system
     * which is also used by other services, the threadpool that
     * will only show up in this services's getThreads() method.
     *
     * @return A collection of all threads owned by the
     *              service
     */
    Collection<Thread> getThreads();
}

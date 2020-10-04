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

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(Class<? extends CerberusService> serviceClass) {
        super("No service is registered with service class: " + serviceClass.getName() + "!");
    }

    public ServiceNotFoundException(Thread thread) {
        super("The thread: " + thread + " is not owned by any service!");
    }

    public ServiceNotFoundException(String name) {
        super("No service is registered under the name " + name + "!");
    }
}

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

package com.cerberustek.service.impl.command;

import com.cerberustek.service.terminal.TerminalCommand;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.service.CerberusService;
import com.cerberustek.service.ServiceNotFoundException;
import com.cerberustek.service.TerminalUtil;
import com.cerberustek.service.impl.MainService;
import com.cerberustek.usr.PermissionHolder;

import java.util.Collection;
import java.util.Scanner;

public class ServiceCommand implements TerminalCommand {

    @Override
    public boolean execute(PermissionHolder holder, Scanner scanner, String... args) {
        if (args.length > 0) {

            if (args.length > 1) {
                if (args.length > 2)
                    return false;

                CerberusService service;
                try {
                    service = CerberusRegistry.getInstance().getService(args[1]);
                } catch (ServiceNotFoundException e) {
                    CerberusRegistry.getInstance().warning("No class with name: " + args[1] + " in classpath!");
                    return true;
                } catch (ClassCastException e) {
                    CerberusRegistry.getInstance().warning("Class " + args[1] + " does not" +
                            "implement CerberusService and is therefor not a valid service!");
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "start":
                        if (!holder.hasPermission(MainService.PERMISSION_START)) {
                            CerberusRegistry.getInstance().printNoPermission();
                            break;
                        }

                        CerberusRegistry.getInstance().requestStart(service.serviceClass());
                        CerberusRegistry.getInstance().info("Service is now started!");
                        break;
                    case "stop":
                        if (!holder.hasPermission(MainService.PERMISSION_STOP)) {
                            CerberusRegistry.getInstance().printNoPermission();
                            break;
                        }

                        CerberusRegistry.getInstance().requestStop(service.serviceClass());
                        CerberusRegistry.getInstance().info("Service is now stopped!");
                        break;
                    case "time":
                        if (!holder.hasPermission(MainService.PERMISSION_TIME)) {
                            CerberusRegistry.getInstance().printNoPermission();
                            break;
                        }

                        CerberusRegistry.getInstance().info("Service has been online since: " +
                                TerminalUtil.getInstance().formatTime(CerberusRegistry.
                                        getInstance().getOnlineTime(service.serviceClass())));
                        break;
                    case "status":
                        if (!holder.hasPermission(MainService.PERMISSION_STATUS)) {
                            CerberusRegistry.getInstance().printNoPermission();
                            break;
                        }

                        CerberusRegistry.getInstance().info("Service status is: " +
                                (CerberusRegistry.getInstance().isRunning(service.serviceClass())
                                        ? TerminalUtil.ANSI_GREEN + "ACTIVE" :
                                        TerminalUtil.ANSI_RED + "INACTIVE") + TerminalUtil.ANSI_RESET + "!");
                        break;
                    default:
                        CerberusRegistry.getInstance().warning("Could not find sub-command: " +
                                    args[0] + "!");
                        break;
                }
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "stop":
                    if (!holder.hasPermission(MainService.PERMISSION_STOP)) {
                        CerberusRegistry.getInstance().printNoPermission();
                        break;
                    }

                    CerberusRegistry.getInstance().info("Stopping all services");
                    CerberusRegistry.getInstance().requestStop();
                    break;
                case "start":
                    if (!holder.hasPermission(MainService.PERMISSION_START)) {
                        CerberusRegistry.getInstance().printNoPermission();
                        break;
                    }

                    CerberusRegistry.getInstance().info("Starting all services");
                    CerberusRegistry.getInstance().requestStart();
                    break;
                case "list":
                    if (!holder.hasPermission(MainService.PERMISSION_LIST)) {
                        CerberusRegistry.getInstance().printNoPermission();
                        break;
                    }

                    StringBuilder builder = new StringBuilder();
                    Collection<CerberusService> services = CerberusRegistry.getInstance().services();
                    CerberusRegistry.getInstance().info(TerminalUtil.ANSI_YELLOW + "Here is a list of all registered" +
                            " services:" + TerminalUtil.ANSI_RESET);
                    for (CerberusService service : services) {
                        builder.append(TerminalUtil.ANSI_CYAN).append("\t# ").append(TerminalUtil.ANSI_RESET)
                                .append(service.serviceClass()).append(TerminalUtil.ANSI_CYAN).append(" --> ")
                                .append(TerminalUtil.ANSI_RESET);
                        if (CerberusRegistry.getInstance().isRunning(service.serviceClass()))
                            builder.append(TerminalUtil.ANSI_GREEN).append("ACTIVE").append(TerminalUtil.ANSI_RESET)
                                    .append(" on ").append(TerminalUtil.ANSI_GREEN).append(getThreadSize(service))
                                    .append(TerminalUtil.ANSI_RESET).append(" threads");
                        else
                            builder.append(TerminalUtil.ANSI_RED).append("INACTIVE").append(TerminalUtil.ANSI_RESET);
                        CerberusRegistry.getInstance().info(builder.toString());
                        builder = new StringBuilder();
                    }
                    CerberusRegistry.getInstance().info(TerminalUtil.ANSI_YELLOW +
                            "------------------------------------------" + TerminalUtil.ANSI_RESET);
                    break;
                default:
                    CerberusRegistry.getInstance().warning("Could not find sub-command: " +
                            args[0] + "!");
                    break;
            }
            return true;
        }
        return false;
    }

    private int getThreadSize(CerberusService service) {
        if (service == null)
            return 0;
        if (service.getThreads() == null)
            return 0;
        return service.getThreads().size();
    }

    @Override
    public String executor() {
        return "service";
    }

    @Override
    public String usage() {
        return "service <start, stop, time, status, list> <service>";
    }

    @Override
    public String requiredPermission() {
        return MainService.PERMISSION_REGISTRY;
    }
}

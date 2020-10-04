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

package com.cerberustek.service.impl;

import com.cerberustek.service.impl.command.ExitCommand;
import com.cerberustek.service.impl.command.HelpCommand;
import com.cerberustek.service.impl.command.ServiceCommand;
import com.cerberustek.service.impl.terminal.SimpleTerminal;
import com.cerberustek.service.terminal.Terminal;
import com.cerberustek.service.terminal.TerminalCommand;
import com.cerberustek.service.CerberusService;
import com.cerberustek.service.IllegalServiceStateException;
import com.cerberustek.usr.impl.MasterPermissionHolder;

import java.util.Collection;
import java.util.HashSet;

public class MainService implements CerberusService {

    public static final String PERMISSION_REGISTRY = "de.cerberus.registry";
    public static final String PERMISSION_STOP = PERMISSION_REGISTRY + ".stop";
    public static final String PERMISSION_START = PERMISSION_REGISTRY + ".start";
    public static final String PERMISSION_TIME = PERMISSION_REGISTRY + ".time";
    public static final String PERMISSION_STATUS = PERMISSION_REGISTRY + ".status";
    public static final String PERMISSION_HELP = PERMISSION_REGISTRY + ".help";
    public static final String PERMISSION_LIST = PERMISSION_REGISTRY + ".list";
    public static final String PERMISSION_EXIT = PERMISSION_STOP;

    private final boolean useTerminal;

    private Thread mainThread;
    private Terminal terminal;

    public MainService(boolean useTerminal) {
        this.useTerminal = useTerminal;
    }

    @Override
    public void start() {
        mainThread = Thread.currentThread();

        if (useTerminal) {
            if (System.getProperty("de.cerberus.cap_out_stream", "true").equals("true"))
                System.setOut(new MainServicePrintStream(System.out, false));
            if (System.getProperty("de.cerberus.cap_err_stream", "true").equals("true"))
                System.setErr(new MainServicePrintStream(System.err, true));

            if (System.getProperty("de.cerberus.use_terminal", "true").equals("true")) {
                terminal = new SimpleTerminal(System.out, System.err, System.in, new MasterPermissionHolder());
                terminal.init();

                terminal.getExecutor().registerCommand(new ServiceCommand());
                terminal.getExecutor().registerCommand(new HelpCommand());
                terminal.getExecutor().registerCommand(new ExitCommand());
            }
        }
    }

    public void registerCommand(TerminalCommand command) {
        if (terminal != null)
            terminal.getExecutor().registerCommand(command);
        else
            throw new IllegalServiceStateException(this);
    }

    public void unregisterCommand(TerminalCommand command) {
        if (terminal != null)
            terminal.getExecutor().unregisterCommand(command);
        else
            throw new IllegalServiceStateException(this);
    }

    @Override
    public void stop() {
        System.out.println("Shutting down main service");

        if (terminal != null)
            terminal.destroy();
    }

    @Override
    public Class<? extends CerberusService> serviceClass() {
        return MainService.class;
    }

    @Override
    public Collection<Thread> getThreads() {
        HashSet<Thread> threads = new HashSet<>();
        if (mainThread != null)
            threads.add(mainThread);

        if (terminal != null)
            threads.addAll(terminal.getThreads());
        return threads;
    }

    public Terminal getTerminal() {
        return terminal;
    }
}

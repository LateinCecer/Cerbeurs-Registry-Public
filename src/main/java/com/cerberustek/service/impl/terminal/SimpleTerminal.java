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

package com.cerberustek.service.impl.terminal;

import com.cerberustek.service.terminal.TerminalCommand;
import com.cerberustek.service.terminal.TerminalExecutor;
import com.cerberustek.service.terminal.TerminalProcessCommand;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.service.terminal.Terminal;
import com.cerberustek.usr.PermissionHolder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class SimpleTerminal implements Terminal {

    private final PermissionHolder permissionHolder;

    private final PrintStream infoStream;
    private final PrintStream errorStream;
    private final InputStream inputStream;

    private TerminalExecutor executor;
    private Thread thread;
    private BufferedReader reader;

    private TerminalCommand lastCommand;

    private boolean shouldRun = false;

    public SimpleTerminal(TerminalExecutor executor, PrintStream infoStream, PrintStream errorStream,
                          InputStream inputStream, PermissionHolder permissionHolder) {
        this.permissionHolder = permissionHolder;
        this.executor = executor;
        executor.init();
        this.infoStream = infoStream;
        this.errorStream = errorStream;
        this.inputStream = inputStream;

        thread = new Thread(new TerminalThread(), "Terminal");
    }

    public SimpleTerminal(PrintStream infoStream, PrintStream errorStream, InputStream inputStream,
                          PermissionHolder permissionHolder) {
        this(new SimpleTerminalExecutor(), infoStream, errorStream, inputStream, permissionHolder);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public PrintStream getInfoStream() {
        return infoStream;
    }

    @Override
    public PrintStream getErrorStream() {
        return errorStream;
    }

    @Override
    public TerminalExecutor getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(TerminalExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Collection<Thread> getThreads() {
        return Collections.singleton(thread);
    }

    @Override
    public void destroy() {
        shouldRun = false;
    }

    @Override
    public void init() {
        thread.start();
    }

    private class TerminalThread implements Runnable {

        @Override
        public void run() {
            shouldRun = true;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line;
                Outer : while (shouldRun) {
                    // TODO find a better way to do this
                    while (!reader.ready()) {
                        if (!shouldRun)
                            break Outer;
                        //noinspection BusyWait
                        Thread.sleep(200);
                    }
                    if (!shouldRun)
                        break;
                    line = reader.readLine();

                    String[] command = splitRaw(line);
                    TerminalCommand c = executor.getCommand(command[0]);
                    if (c != null) {
                        if (!permissionHolder.hasPermission(c.requiredPermission()))
                            CerberusRegistry.getInstance().warning("Access denied! Required permission:\n" +
                                    c.requiredPermission());

                        String[] args = new String[command.length - 1];
                        System.arraycopy(command, 1, args, 0, args.length);
                        if (!c.execute(permissionHolder, new Scanner(inputStream), args)) {
                            CerberusRegistry.getInstance().warning("Wrong usage! Try: " + c.usage());
                            continue;
                        }
                        lastCommand = c;
                    } else {
                        if (command[0].isEmpty() && lastCommand instanceof TerminalProcessCommand)
                            ((TerminalProcessCommand) lastCommand).exit();
                        else
                            CerberusRegistry.getInstance().warning("Command \"" + command[0] + "\" not found!");
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            shouldRun = false;
        }

        private String[] splitRaw(String command) {
            ArrayList<String> list = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            char[] chars = command.toCharArray();

            boolean quotation = false;
            for (char c : chars) {
                if (c == '"')
                    quotation = !quotation;
                else if (!quotation && c == ' ') {
                    list.add(builder.toString());
                    builder = new StringBuilder();
                } else
                    builder.append(c);
            }
            list.add(builder.toString());

            String[] output = new String[list.size()];
            list.toArray(output);
            return output;
        }
    }
}

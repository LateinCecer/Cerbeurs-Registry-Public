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
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

public class SimpleTerminalExecutor implements TerminalExecutor {

    private HashMap<String, TerminalCommand> commandMap;

    @Override
    public void init() {
        commandMap = new HashMap<>();
    }

    @Override
    public void registerCommand(@NotNull TerminalCommand command) {
        try {
            if (!hasCommand(command))
                commandMap.put(command.executor(), command);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterCommand(@NotNull TerminalCommand command) {
        try {
            commandMap.remove(command.executor());
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasCommand(TerminalCommand command) {
        try {
            if (command != null)
                return commandMap.containsKey(command.executor());
        } catch (ConcurrentModificationException ignore) {}
        return false;
    }

    @Override
    public TerminalCommand getCommand(String executor) {
        try {
            if (executor != null)
                return commandMap.get(executor);
        } catch (ConcurrentModificationException ignore) {}
        return null;
    }

    @Override
    public Collection<TerminalCommand> commands() {
        return commandMap.values();
    }
}

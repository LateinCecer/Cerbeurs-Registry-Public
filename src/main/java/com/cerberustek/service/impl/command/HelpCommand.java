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
import com.cerberustek.service.terminal.TerminalExecutor;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.service.TerminalUtil;
import com.cerberustek.service.impl.MainService;
import com.cerberustek.usr.PermissionHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

public class HelpCommand implements TerminalCommand {

    @Override
    public boolean execute(PermissionHolder holder, Scanner scanner, String... args) {
        int page = 0;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        CerberusRegistry logger = CerberusRegistry.getInstance();
        TerminalExecutor executor = logger.getTerminal().getExecutor();
        Collection<TerminalCommand> commands = executor.commands();
        Iterator<TerminalCommand> tr = commands.iterator();

        for (int i = 0; i < page * 10; i++) {
            if (tr.hasNext())
                tr.next();
        }
        logger.info(TerminalUtil.ANSI_YELLOW + "Here is a list of all commands:" + TerminalUtil.ANSI_RESET);
        while (tr.hasNext())
            logger.info(TerminalUtil.ANSI_CYAN + "\t# " + TerminalUtil.ANSI_RESET + tr.next().usage());

        int maxPages = (int) Math.ceil((float) commands.size() / 10f);
        logger.info(TerminalUtil.ANSI_YELLOW + "Page " + (page + 1) + "/" +
                maxPages + "." + (maxPages > page ? " Try " +
                    TerminalUtil.ANSI_BLUE + "'help " + (page + 1) + TerminalUtil.ANSI_YELLOW + "' for" +
                " more." : "") + TerminalUtil.ANSI_RESET);
        return true;
    }

    @Override
    public String executor() {
        return "help";
    }

    @Override
    public String usage() {
        return "help <page>";
    }

    @Override
    public String requiredPermission() {
        return MainService.PERMISSION_HELP;
    }
}

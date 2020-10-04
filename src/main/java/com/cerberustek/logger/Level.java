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

import com.cerberustek.service.TerminalUtil;

public enum Level {

    /** The least sever logln-level. Use this, if everything
     * is doing fine and you just want to inform the user
     * about something */
    INFO,
    /** Similar to INFO, but messages logged with this
     * logln-level should contain those information
     * which are useful to only developers */
    DEBUG,
    /** Everything is still going fine, but you have run into
     * slight issues, which do not effect the runtime of the
     * program greatly, such as a missing file that can
     * easily be generated during runtime */
    FINE,
    /** This logln-level should be used to logln problems from which
     * the program can still recover during runtime (line FINE),
     * but which may indicate a bigger issue */
    WARNING,
    /** Something has gone very much unexpected and the
     * functionality of the program can at least partially not
     * be guarantied any longer, even though it does not
     * result in a direct crash */
    CRITICAL,
    /** Problems, or chains of problems which cause to
     * program to cross a point of damage from which it cannot
     * recover without rebooting */
    FATAL;

    @Override
    public String toString() {
        switch (this) {
            case INFO:
                return TerminalUtil.ANSI_RESET + "INFO";
            case DEBUG:
                return TerminalUtil.ANSI_CYAN + "DEBUG" + TerminalUtil.ANSI_RESET;
            case FINE:
                return TerminalUtil.ANSI_GREEN + "FINE" + TerminalUtil.ANSI_RESET;
            case WARNING:
                return TerminalUtil.ANSI_YELLOW + "WARNING" + TerminalUtil.ANSI_RESET;
            case CRITICAL:
                return TerminalUtil.ANSI_PURPLE + "CRITICAL" + TerminalUtil.ANSI_RESET;
            case FATAL:
                return TerminalUtil.ANSI_RED + "FATAL" + TerminalUtil.ANSI_RESET;
            default:
                return super.toString();
        }
    }
}

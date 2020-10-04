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

import com.cerberustek.CerberusRegistry;
import com.cerberustek.logger.Level;

import java.io.PrintStream;

public class MainServicePrintStream extends PrintStream {

    private final boolean errorStream;

    public MainServicePrintStream(PrintStream printStream, boolean isErrorStream) {
        super(printStream);
        this.errorStream = isErrorStream;
    }

    private boolean isErrorStream() {
        return errorStream;
    }

    @Override
    public void println() {
        print("\n");
    }

    @Override
    public void println(boolean x) {
        println(Boolean.toString(x));
    }

    @Override
    public void println(char x) {
        println(Character.toString(x));
    }

    @Override
    public void println(int x) {
        println(Integer.toString(x));
    }

    @Override
    public void println(long x) {
        println(Long.toString(x));
    }

    @Override
    public void println(float x) {
        println(Float.toString(x));
    }

    @Override
    public void println(double x) {
        println(Double.toString(x));
    }

    @Override
    public void println(char[] x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(Object x) {
        if (x != null)
            println(x.toString());
        else
            println("null");
    }

    @Override
    public void println(String x) {
        if (isErrorStream())
            CerberusRegistry.getInstance().log(Level.CRITICAL, x, 3);
        else
            CerberusRegistry.getInstance().log(Level.INFO, x, 3);
    }
}

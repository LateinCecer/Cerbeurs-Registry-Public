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

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class CerberusLogger implements Closeable {

    private final static int BUFFER_SIZE = 4096;
    private final static int MAX_LOGSIZE = 1000;

    private final LogArchive archive;
    private final HashMap<Class<? extends CerberusService>, HashSet<LogElement>> logs = new HashMap<>();
    private final BufferedWriter outWriter;
    private final BufferedWriter errWriter;

    public CerberusLogger(LogArchive archive) throws UnsupportedEncodingException {
        this.archive = archive;
        outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "ASCII"), BUFFER_SIZE);
        errWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.err), "ASCII"), BUFFER_SIZE);
    }

    /**
     * Will logln a message.
     *
     * @param serviceClass The service responsible for the logln
     * @param level The level of the logln entry
     * @param stackTrace The stacktrace of the logln
     * @param message Log message
     */
    public void logln(Class<? extends CerberusService> serviceClass, Level level, StackTraceElement stackTrace,
                      String message) {
        long currentTime = System.currentTimeMillis();
        LogElement element = new LogElement(serviceClass, level + message, stackTrace.toString(), currentTime);
        put(serviceClass, element);
        if (level == Level.FATAL)
            printlnErr('\r' + "[" + getTimeStamp(currentTime) + " | " + serviceClass.getSimpleName() + " | " + level
                    + "]> " + message + " at: \n" + element.getStackTrace());
        else if (level == Level.CRITICAL)
            printlnErr('\r' + "[" + getTimeStamp(currentTime) + " | " + serviceClass.getSimpleName() + " | " + level
                    + "]> " + message);
        else
            println('\r' + "[" + getTimeStamp(currentTime) + " | " + serviceClass.getSimpleName() + " | " + level
                    + "]> " + message);
        dump();
    }

    /**
     * Will log a message without starting a new line.
     *
     * @param serviceClass The service responsible for the log
     * @param level The level of the log entry
     * @param stackTrace The stacktrace of the log
     * @param message Log message
     */
    public void log(Class<? extends CerberusService> serviceClass, Level level, StackTraceElement stackTrace,
                    String message) {
        long currentTime = System.currentTimeMillis();
        LogElement element = new LogElement(serviceClass, level + message, stackTrace.toString(), currentTime);
        put(serviceClass, element);
        if (level == Level.FATAL)
            printErr('\r' + "[" + getTimeStamp(currentTime) + " | " + serviceClass.getSimpleName() + " | " + level
                    + "]> " + message + " at: \n" + element.getStackTrace());
        else if (level == Level.CRITICAL)
            printErr('\r' + "[" + getTimeStamp(currentTime) + " | " + serviceClass.getSimpleName() + " | " + level
                    + "]> " + message);
        else
            print('\r' + "[" + getTimeStamp(currentTime) + " | " + serviceClass.getSimpleName() + " | " + level
                    + "]> " + message);
        dump();
    }

    public void println(String s) {
        try {
            outWriter.write(s + '\n');
            outWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print(String s) {
        try {
            outWriter.write(s);
            outWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printlnErr(String s) {
        try {
            errWriter.write(s + '\n');
            errWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printErr(String s) {
        try {
            errWriter.write(s);
            errWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void put(Class<? extends CerberusService> serviceClass, LogElement logElement) {
        HashSet<LogElement> elements = logs.computeIfAbsent(serviceClass, k -> new HashSet<>());
        elements.add(logElement);
    }

    /**
     * In case more logs are saved by the logger than allowed,
     * this method will transfer all logln data to the archive and
     * delete it's logs afterwards.
     */
    public void dump() {
        if (size() > MAX_LOGSIZE)
            dumpAll();
    }

    private int size() {
        int size = 0;
        for (HashSet<LogElement> set : logs.values())
            size += set.size();
        return size;
    }

    public void dumpAll() {
        for (HashSet<LogElement> set : logs.values())
            archive.archive(set);
        logs.clear();
    }

    public static String getTimeStamp(long time) {
        time = (time / 1000) % 86400;
        int seconds = (int) (time % 60);
        int minutes = (int) ((time % 3600) / 60);
        int hours = (int) (time / 3600);

        return (hours >= 10 ? hours + "" : "0" + hours) + ":" +
                (minutes >= 10 ? minutes + "" : "0" + minutes) + ":" +
                (seconds >= 10 ? seconds + "" : "0" + seconds);
    }

    public LogArchive getArchive() {
        return archive;
    }

    public Collection<LogElement> getElements(Class<? extends CerberusService> serviceClass) {
        return logs.get(serviceClass);
    }

    @Override
    public void close() throws IOException {
        outWriter.flush();
        outWriter.close();

        errWriter.flush();
        errWriter.close();
    }
}

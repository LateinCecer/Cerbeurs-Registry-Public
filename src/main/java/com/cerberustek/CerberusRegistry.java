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

package com.cerberustek;

import com.cerberustek.logger.CerberusLogger;
import com.cerberustek.logger.Level;
import com.cerberustek.logger.LogArchive;
import com.cerberustek.service.CerberusService;
import com.cerberustek.service.IllegalServiceStateException;
import com.cerberustek.service.ServiceNotFoundException;
import com.cerberustek.service.impl.MainService;
import com.cerberustek.service.terminal.Terminal;
import com.cerberustek.service.terminal.TerminalCommand;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class is defined as a singleton
 */
public class CerberusRegistry {

    public static final String VERSION = "1.01";
    /** The map of services */
    private final HashMap<Class<? extends CerberusService>, CerberusService> services;
    /** Map of the start times of all services */
    private final HashMap<Class<? extends CerberusService>, Long> runningMap;

    /** The logger's archive */
    private final LogArchive archive;
    /** Logger Object */
    private final CerberusLogger logger;

    /** Singleton instance */
    private static CerberusRegistry instance;

    /**
     * Returns the currently active Cerberus Registry.
     *
     * This Method will create a new instance of the Cerberus
     * Registry in case there is no valid instance already
     * available.
     *
     * @return Cerberus Registry
     */
    public static CerberusRegistry getInstance() {
        if (instance == null) {
            instance = new CerberusRegistry();
            instance.start(true);
        }

        return instance;
    }

    /**
     * Returns the currently active Cerberus Registry.
     *
     * This Method will create a new instance of the Cerberus
     * Registry in case there is no valid instance already
     * available.
     *
     * @return Cerberus Registry
     */
    public static CerberusRegistry getInstanceNoTerminal() {
        if (instance == null) {
            instance = new CerberusRegistry();
            instance.start(false);
        }

        return instance;
    }

    /** Private Constructor. Doesn't do much, except keeping
     * people from fucking with the singleton nature of this class */
    private CerberusRegistry() {
        services = new HashMap<>();
        runningMap = new HashMap<>();

        archive = new LogArchive();
        CerberusLogger l;
        try {
            l = new CerberusLogger(archive);
        } catch (UnsupportedEncodingException e) {
            l = null;
        }
        logger = l;
    }

    private void start(boolean useTerminal) {
        registerService(new MainService(useTerminal));
        requestStart(MainService.class);

        if (logger != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                requestStop();
                logger.dumpAll();
                try {
                    logger.close();
                } catch (IOException e) {
                    critical("Failed to close logger! " + e);
                }
            }));
        }
    }

    /**
     * Returns a service based on its registered {@code serviceClass}.
     *
     * If there is no service registered to the specified serviceClass,
     * this method will throw a ServiceNotFoundException.
     *
     * @param serviceClass ServiceClass
     * @param <T> The type of the service requested
     * @return Service extending {@code serviceClass}
     */
    public <T extends CerberusService> T getService(Class<T> serviceClass) {
        CerberusService service = services.get(serviceClass);
        if (service != null)
            return serviceClass.cast(service);
        throw new ServiceNotFoundException(serviceClass);
    }

    /**
     * Returns the Service that owns the specified
     * thread.
     *
     * If the thread is not owned by any service, this method
     * will throw a ServiceNotFoundException.
     *
     * @param thread thread to search for
     * @return Owner service
     */
    public CerberusService getService(Thread thread) {
        for (CerberusService service : services.values()) {
            if (service.getThreads() != null && service.getThreads().contains(thread))
                return service;
        }
        throw new ServiceNotFoundException(thread);
    }

    /**
     * Returns a cerberus service based on the simple name of the service.
     *
     * This method will search for a service based on the simple name
     * of the service class. If no service with a matching service class
     * name is found, this method will attempt to find a service with a
     * similar name. If this also fails, this method will throw a
     * service not found exception.
     *
     * @param name name or part of the simple name of a service class
     * @return service
     */
    public CerberusService getService(@NotNull String name) {
        for (CerberusService service : services.values()) {
            if (service.serviceClass().getSimpleName().equals(name))
                return service;
        }

        for (CerberusService service : services.values()) {
            if (service.serviceClass().getSimpleName().equalsIgnoreCase(name) ||
                service.serviceClass().getSimpleName().toLowerCase().contains(name.toLowerCase()))
                return service;
        }
        throw new ServiceNotFoundException(name);
    }

    @Deprecated
    /**
     * Registers a service to a specific service class.
     *
     * Only one service can be registered to a ServiceClass, but theoretically
     * a service can be registered to multiple ServiceClasses.
     *
     * @param serviceClass ServiceClass to register the service to
     * @param service The service to register
     * @param <T> The service Type
     * @return The service registered
     */
    public <T extends CerberusService> T registerService(Class<T> serviceClass, T service) {
        if (!services.containsKey(serviceClass))
            services.put(serviceClass, service);
        return service;
    }

    /**
     * Registers a service to a specific service class.
     *
     * @param service The service to register
     * @param <T> The service Type
     * @return The service registered
     */
    public <T extends CerberusService> T registerService(T service) {
        if (!services.containsKey(service.serviceClass()))
            services.put(service.serviceClass(), service);
        return service;
    }

    /**
     * Returns rather or not a service has been registered to the the
     * service registry.
     *
     * @param clazz The service class to check on
     * @return has been registered?
     */
    public boolean hasService(Class<? extends CerberusService> clazz) {
        return services.containsKey(clazz);
    }

    /**
     * Returns rather the service associated with the {@code serviceClass}
     * is currently running or not.
     *
     * @param serviceClass ServiceClass registered to the service
     *                    in question
     * @return Is the server running?
     */
    public boolean isRunning(Class<? extends CerberusService> serviceClass) {
        return runningMap.containsKey(serviceClass);
    }

    /**
     * Will attempt to start the service in question.
     *
     * If the service linked to {@code serviceClass} does not exist,
     * this method will throw a ServiceNotFoundException. If the service
     * is already running, this Method will throw an
     * IllegalServiceStateException. Otherwise this method will return the
     * service that it has just started
     *
     * @param serviceClass ServiceClass
     * @param <T> The type of the service that has just been started
     * @return The service that has just been started
     */
    public <T extends CerberusService> T requestStart(Class<T> serviceClass) {
        CerberusService service = getService(serviceClass);
        if (isRunning(serviceClass))
            throw new IllegalServiceStateException(service);
        runningMap.put(serviceClass, System.currentTimeMillis());
        service.start();
        return serviceClass.cast(service);
    }

    /**
     * Will send a start request to all services.
     *
     * If a service is already running, this command will have no effect
     * on the service. Otherwise, it will attempt to start the service.
     * Contrary to the <code>requestStart(Class<T> serviceClass)</code>
     * method, this method will not throw any Exceptions if it fails to
     * start a service.
     */
    public void requestStart() {
        for (Class<? extends CerberusService> service : services.keySet()) {
            try {
                requestStart(service);
            } catch (IllegalServiceStateException e) {
                // Ignore this exception
            }
        }
    }

    /**
     * Will attempt to stop the service in question.
     *
     * If the service referenced by {@code serviceClass} is not properly
     * registered, this method will throw a ServiceNotFoundException.
     * If the service is currently not running, this method will throw
     * an IllegalServiceStateException. Otherwise this method will return
     * the service that it has just stopped.
     *
     * @param serviceClass ServiceClass
     * @param <T> The type of the service that has just been stopped
     * @return The service that has just been stopped
     */
    public <T extends CerberusService> T requestStop(Class<T> serviceClass) {
        CerberusService service = getService(serviceClass);
        if (!isRunning(serviceClass))
            throw new IllegalServiceStateException(service);
        runningMap.remove(serviceClass);
        service.stop();
        return serviceClass.cast(service);
    }

    /**
     * Will request the stop all running services.
     */
    public void requestStop() {
        for (Class<? extends CerberusService> serviceClass : runningMap.keySet()) {
            CerberusService service = getService(serviceClass);
            if (service != null)
                service.stop();
        }
        runningMap.clear();
    }

    /**
     * Will force the service in question to shut down.
     *
     * If the service referenced by {@code serviceClass} is not properly
     * registered, this method will throw a ServiceNotFoundException. If
     * the service is currently not running, this method will throw an
     * IllegalServiceStateException. Otherwise this method will return
     * the service that it has just forcibly stopped.
     * Please keep in mind, that calling this method may result in unwanted
     * side effects, including, but not exclusively, data-loss and -corruption,
     * notwork errors and in the worst case, system failure.
     *
     * @param serviceClass ServiceClass
     * @param <T> The type of the service that has just been stopped
     * @return The service that has just been stopped
     */
    public <T extends CerberusService> T forceStop(Class<T> serviceClass) {
        CerberusService service = getService(serviceClass);
        if (!isRunning(serviceClass))
            throw new IllegalServiceStateException(service);
        Collection<Thread> threads = service.getThreads();
        for (Thread t : threads)
            t.interrupt();
        runningMap.remove(serviceClass);
        service.stop();
        return serviceClass.cast(service);
    }

    /**
     * Returns the system time on which the service in question was last
     * started.
     *
     * If the service referenced by {@code serviceClass} is unknown to the
     * system, this method will throw a ServiceNotFoundException.
     *
     * @param serviceClass ServiceClass
     * @return Last known start-time of the service
     */
    public long getOnlineTime(Class<? extends CerberusService> serviceClass) {
        if (isRunning(serviceClass))
            return runningMap.get(serviceClass);
        return -1;
    }

    /**
     * Logs a message to the specific logln-level.
     *
     * @param level level
     * @param message message
     * @param index StackTrace index
     */
    @SuppressWarnings("Duplicates")
    public void log(Level level, String message, int index) {
        Thread currentThread = Thread.currentThread();
        CerberusService currentService;

        try {
            currentService = getService(currentThread);
        } catch (ServiceNotFoundException e) {
            currentService = getService(MainService.class);
        }

        StackTraceElement element;
        if (currentThread.getStackTrace().length > index)
            element = currentThread.getStackTrace()[index];
        else
            element = currentThread.getStackTrace()[0];

        logger.logln(currentService.serviceClass(), level, element, message);
    }

    /**
     * Logs a message without starting a new line.
     *
     * @param level level
     * @param message mesasge
     * @param index StackTrace index
     */
    @SuppressWarnings("Duplicates")
    public void logInLine(Level level, String message, int index) {
        Thread currentThread = Thread.currentThread();
        CerberusService currentService;

        try {
            currentService = getService(currentThread);
        } catch (ServiceNotFoundException e) {
            currentService = getService(MainService.class);
        }

        StackTraceElement element;
        if (currentThread.getStackTrace().length > index)
            element = currentThread.getStackTrace()[index];
        else
            element = currentThread.getStackTrace()[0];

        logger.log(currentService.serviceClass(), level, element, message);
    }

    /**
     * Logs a message to the specific logln-level.
     *
     * @param level level
     * @param message message
     */
    public void log(Level level, String message) {
        log(level, message, 3);
    }

    public void logInLine(Level level, String message) {
        logInLine(level, message, 3);
    }

    public void info(String message) {
        log(Level.INFO, message, 3);
    }

    public void infoInLine(String message) {
        logInLine(Level.INFO, message, 3);
    }

    public void debug(String message) {
        log(Level.DEBUG, message, 3);
    }

    public void debugInLine(String message) {
        logInLine(Level.DEBUG, message, 3);
    }

    public void fine(String message) {
        log(Level.FINE, message, 3);
    }

    public void fineInLine(String message) {
        logInLine(Level.FINE, message, 3);
    }

    public void warning(String message) {
        log(Level.WARNING, message, 3);
    }

    public void warningInLine(String message) {
        logInLine(Level.WARNING, message, 3);
    }

    public void critical(String message) {
        log(Level.CRITICAL, message, 3);
    }

    public void criticalInLine(String message) {
        logInLine(Level.CRITICAL, message, 3);
    }

    public void fatal(String message) {
        log(Level.FATAL, message, 3);
    }

    public void fatalInLine(String message) {
        logInLine(Level.FATAL, message, 3);
    }

    public void printNoPermission() {
        warning("Access denied! If you think this is a mistake contact your local system administrator.");
    }

    /**
     * Returns the logln archive of the Registry.
     *
     * @return LogArchive
     */
    public LogArchive getArchive() {
        return archive;
    }

    public CerberusLogger getLogger() {
        return logger;
    }

    /**
     * Returns the main terminal of the Registry.
     *
     * @return Terminal
     * @throws IllegalServiceStateException Exception in case
     *      that the main service is currently not running
     */
    public Terminal getTerminal() {
        MainService service = getService(MainService.class);
        if (!isRunning(MainService.class))
            throw new IllegalServiceStateException(service);
        return service.getTerminal();
    }

    public void registerTerminalCommand(TerminalCommand command) {
        if (!hasService(MainService.class))
            return;

        MainService service = getService(MainService.class);
        if (!isRunning(MainService.class))
            throw new  IllegalServiceStateException(service);
        service.registerCommand(command);
    }

    public void unregisterTerminalCommand(TerminalCommand command) {
        if (!hasService(MainService.class))
            return;

        MainService service = getService(MainService.class);
        if (!isRunning(MainService.class))
            throw new  IllegalServiceStateException(service);
        service.unregisterCommand(command);
    }

    /**
     * Returns all registered services.
     *
     * @return All registered services
     */
    public Collection<CerberusService> services() {
        return services.values();
    }
}

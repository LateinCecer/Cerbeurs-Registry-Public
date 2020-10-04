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

package com.cerberustek.settings.impl;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.settings.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsImpl implements Settings {

    private final File file;
    private final Properties properties;
    private final boolean xml;

    public SettingsImpl(File file, boolean xml) {
        this.file = file;
        this.properties = new Properties();
        this.xml = xml;
    }

    public SettingsImpl(File file) {
        this.file = file;
        this.properties = new Properties();
        this.xml = true;
    }

    @Override
    public void destroy() {
        save();
    }

    @Override
    public void init() {
        reload();
    }

    @Override
    public void reload() {
        if (!file.exists()) {
            CerberusRegistry.getInstance().fine("Settings file cannot be found!");
            createParentFile();
        } else {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                if (xml)
                    properties.loadFromXML(inputStream);
                else
                    properties.load(inputStream);
            } catch (IOException e) {
                CerberusRegistry.getInstance().warning("Could not read settings file: " + e);
                CerberusRegistry.getInstance().warning("Deleting invalid settings file...");
                file.deleteOnExit();
            }
        }
    }

    @Override
    public void save() {
        createParentFile();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            if (xml)
                properties.storeToXML(outputStream, "~={ Cerberus-Settings file v_" +
                        CerberusRegistry.VERSION + " }=~", "UTF-16");
            else
                properties.store(outputStream, "~={ Cerberus-Settings file v_" +
                        CerberusRegistry.VERSION + " }=~");
        } catch (IOException e) {
            CerberusRegistry.getInstance().warning("Could not save settings: " + e);
        }
    }

    private void createParentFile() {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            CerberusRegistry.getInstance().fine("Parent file for settings does not exist!");
            if (!parent.mkdirs())
                CerberusRegistry.getInstance().warning("Invalid path to settings file!");
            else
                CerberusRegistry.getInstance().info("Created parent directory for settings file!");
        }
    }

    @Override
    public boolean getBoolean(String tag, boolean defaultValue) {
        if (!properties.containsKey(tag))
            setBoolean(tag, defaultValue);
        return Boolean.parseBoolean(properties.getProperty(tag, Boolean.toString(defaultValue)));
    }

    @Override
    public void setBoolean(String tag, boolean value) {
        properties.setProperty(tag, Boolean.toString(value));
    }

    @Override
    public byte getSignedByte(String tag, byte defaultValue) {
        if (!properties.containsKey(tag))
            setByte(tag, defaultValue);

        try {
            return Byte.parseByte(properties.getProperty(tag, Byte.toString(defaultValue)));
        } catch (NumberFormatException e) {
            CerberusRegistry.getInstance().warning("String found in settings where byte was expected! " + tag);
            return defaultValue;
        }
    }

    @Override
    public int getUnsignedByte(String tag, int defaultValue) {
        return Byte.toUnsignedInt(getSignedByte(tag, (byte) (defaultValue & 0xFF)));
    }

    @Override
    public void setByte(String tag, int value) {
        properties.setProperty(tag, Byte.toString((byte) (value & 0xFF)));
    }

    @Override
    public short getSignedShort(String tag, short defaultValue) {
        if (!properties.containsKey(tag))
            setShort(tag, defaultValue);

        try {
            return Short.parseShort(properties.getProperty(tag, Short.toString(defaultValue)));
        } catch (NumberFormatException e) {
            CerberusRegistry.getInstance().warning("String found in settings where short was expected! " + tag);
            return defaultValue;
        }
    }

    @Override
    public int getUnsigedShort(String tag, int defaultValue) {
        return Short.toUnsignedInt(getSignedShort(tag, (short) (defaultValue & 0xFFFF)));
    }

    @Override
    public void setShort(String tag, int value) {
        properties.setProperty(tag, Short.toString((short) (value & 0xFFFF)));
    }

    @Override
    public char getCharacter(String tag, char defaultValue) {
        if (!properties.containsKey(tag))
            setCharacter(tag, defaultValue);

        return properties.getProperty(tag, Character.toString(defaultValue)).charAt(0);
    }

    @Override
    public void setCharacter(String tag, char value) {
        properties.setProperty(tag, Character.toString(value));
    }

    @Override
    public int getInteger(String tag, int defaultValue) {
        if (!properties.containsKey(tag))
            setInteger(tag, defaultValue);

        try {
            return Integer.parseInt(properties.getProperty(tag, Integer.toString(defaultValue)));
        } catch (NumberFormatException e) {
            CerberusRegistry.getInstance().warning("String found in settings where integer was expected! " + tag);
            return defaultValue;
        }
    }

    @Override
    public long getUnsignedInteger(String tag, long defaultValue) {
        return Integer.toUnsignedLong(getInteger(tag, (int) (defaultValue)));
    }

    @Override
    public void setInteger(String tag, int value) {
        properties.setProperty(tag, Integer.toString(value));
    }

    @Override
    public long getLong(String tag, long defaultValue) {
        if (!properties.containsKey(tag))
            setLong(tag, defaultValue);

        try {
            return Long.parseLong(properties.getProperty(tag, Long.toString(defaultValue)));
        } catch (NumberFormatException e) {
            CerberusRegistry.getInstance().warning("String found in settings where long was expected! " + tag);
            return defaultValue;
        }
    }

    @Override
    public void setLong(String tag, long value) {
        properties.setProperty(tag, Long.toString(value));
    }

    @Override
    public float getFloat(String tag, float defaultValue) {
        if (!properties.containsKey(tag))
            setFloat(tag, defaultValue);

        try {
            return Float.parseFloat(properties.getProperty(tag, Float.toString(defaultValue)));
        } catch (NumberFormatException e) {
            CerberusRegistry.getInstance().warning("String found in settings where float was expected! " + tag);
            return defaultValue;
        }
    }

    @Override
    public void setFloat(String tag, float value) {
        properties.setProperty(tag, Float.toString(value));
    }

    @Override
    public double getDouble(String tag, double defaultValue) {
        if (!properties.containsKey(tag))
            setDouble(tag, defaultValue);

        try {
            return Double.parseDouble(properties.getProperty(tag, Double.toString(defaultValue)));
        } catch (NumberFormatException e) {
            CerberusRegistry.getInstance().warning("String found in settings where double was expected! " + tag);
            return defaultValue;
        }
    }

    @Override
    public void setDouble(String tag, double value) {
        properties.setProperty(tag, Double.toString(value));
    }

    @Override
    public String getString(String tag, String defaultValue) {
        if (!properties.containsKey(tag))
            setString(tag, defaultValue);

        return properties.getProperty(tag, defaultValue);
    }

    @Override
    public void setString(String tag, String value) {
        properties.setProperty(tag, value);
    }

    @Override
    public Object getObject(String tag, Object defaultValue) {
        return properties.getOrDefault(tag, defaultValue);
    }

    @Override
    public void setObject(String tag, Object value) {
        properties.getOrDefault(tag, value);
    }


}

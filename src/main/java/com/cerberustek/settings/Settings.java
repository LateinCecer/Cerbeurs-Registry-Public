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

package com.cerberustek.settings;

import com.cerberustek.Destroyable;
import com.cerberustek.Initable;

public interface Settings extends Initable, Destroyable {

    boolean getBoolean(String tag, boolean defaultValue);
    void setBoolean(String tag, boolean value);

    byte getSignedByte(String tag, byte defaultValue);
    int getUnsignedByte(String tag, int defaultValue);
    void setByte(String tag, int value);

    short getSignedShort(String tag, short defaultValue);
    int getUnsigedShort(String tag, int defaultValue);
    void setShort(String tag, int value);

    char getCharacter(String tag, char defaultValue);
    void setCharacter(String tag, char value);

    int getInteger(String tag, int defaultValue);
    long getUnsignedInteger(String tag, long defaultValue);
    void setInteger(String tag, int value);

    long getLong(String tag, long defaultValue);
    void setLong(String tag, long value);

    float getFloat(String tag, float defaultValue);
    void setFloat(String tag, float value);

    double getDouble(String tag, double defaultValue);
    void setDouble(String tag, double value);

    String getString(String tag, String defaultValue);
    void setString(String tag, String value);

    Object getObject(String tag, Object defaultValue);
    void setObject(String tag, Object value);
    
    void reload();
    void save();
}

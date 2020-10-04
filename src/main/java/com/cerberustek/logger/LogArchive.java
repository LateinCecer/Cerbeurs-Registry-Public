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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

public class LogArchive {

    private final File file;

    public LogArchive() {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
        String fileName = sdf.format(calendar.getTime()).replace(".", "_") + ".logln";
        file = new File("logs/" + fileName);
    }

    /**
     * Will save a LogElement
     * @param elements element
     */
    void archive(Collection<LogElement> elements) {
        /*
        MetaList<DocElement> list = new ListElement<>();
        for (LogElement el : elements) {
            DocElement doc = (DocElement) LogElement.toData(el, new DocElement());
            list.add(doc);
        }
        int byteSize = (int) list.byteSize();
        MetaByteBuffer buffer = new MetaByteBufferImpl(discriminatorMap, byteSize);
        */
        // System.out.println("archive(...); Not yet implemented!");
    }

    /**
     * Will retrieve all logElements caused by a specific service on a specific
     * level, within a given time period.
     *
     * @param service The service in question
     * @param level The logln-level of the requested elements
     * @param startTime Start of the interval in which to search
     * @param endTime End of the interval in which to search
     * @return Collection of compatible logs
     */
    public Collection<LogElement> retrieve(CerberusService service, Level level, long startTime, long endTime) {
        System.out.println("retrieve(...); Not yet implemented!");
        return null;
    }

    /**
     * Will permanently erase all logElements caused by a specific service on a
     * specific level, within a given time period.
     *
     * @param service The service in question
     * @param level The logln-level of the requested elements
     * @param startTime Start of the interval in which to purge
     * @param endTime End of the interval in which to purge
     */
    public void erase(CerberusService service, Level level, long startTime, long endTime) {
        System.out.println("erase(...); Not yet implemented!");
    }
}

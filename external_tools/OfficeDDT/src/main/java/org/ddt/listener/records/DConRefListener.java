/*
 * Copyright 2012 Niklas Rehfeld.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 */
package org.ddt.listener.records;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ddt.Link;
import org.ddt.listener.RecordEventListener;
import org.apache.poi.hssf.record.Record;

/**
 * This class works on DConRef Records. These are used to store the source
 * information for PivotTables in Excel documents.
 *
 * @see RecordEventListener
 *
 * @author Niklas Rehfeld
 */
public class DConRefListener extends RecordEventListener
{

    private static final Logger log = Logger.getLogger("org.ddt");

    /**
     * Constructor.
     */
    public DConRefListener()
    {
        super();
    }


    public void processRecord(Record record)
    {
        if (record.getSid() == DConRefRecord.sid)
        {
            DConRefRecord dcr = new DConRefRecord(record.serialize());
            if (!dcr.isExternalRef())
            {
                log.log(Level.FINE, "Not an external ref: {0}", dcr.toString());
                return;
            }

            byte[] path = dcr.getPath();
            String linkString = cleanPath(path);

	    if (linkString != null && !linkString.trim().equals(""));
            {
                Link l = new Link(5);
                byte type = path[1];
                //types relative to startup dir | alt-startup dir | library dir | simple-file-path
                if (type == 0x06 || type == 0x07 || type == 0x08 || type > 0x40)
                    l.addRelativePath(linkString);
                else if (type == 0x01)
                {
                    if (path[2] == 0x40) //UNC path.
                    {
                        l.addAbsolutePath(linkString);
                    } else if (path[2] > 0x40) //relative to drive letter, remove drive letter
                    {
                        char drive = (char) path[2];
                        l.addRelativePath(linkString.replaceFirst(String.valueOf(drive), ""));
                    }
                } else
                    l.addUnkownPath(linkString);
                this.add(l);
            }
        }
    }

    /**
     * Removes the braces and sheet name that is sometimes appended to the end of a path and
     * replaces the
     * <code>\0x0003</code> path separators with '/' characters.
     * <p/>
     * @param path the path to clean.
     * @return a clean version of the file path.
     */
    private String cleanPath(byte[] path)
    {
        String out = new String(path).trim();

        int firstBrace = out.indexOf('[');
        int lastBrace = out.lastIndexOf(']');
        if (firstBrace < 0 || lastBrace < 0)
            return out;
        out = out.replaceFirst("\\[", "");
        out = out.substring(0, lastBrace - 1);
        out = out.replaceAll("\u0003", "/");
        return out;
    }
}

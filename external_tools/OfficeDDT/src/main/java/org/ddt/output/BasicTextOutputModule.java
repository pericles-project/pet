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
package org.ddt.output;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.ddt.Link;

/**
 * Outputs link information as human-readable text.
 *
 * @author Niklas Rehfeld
 */
public class BasicTextOutputModule extends OutputModule
{

    /**
     * used to write strings to the output stream
     */
    private OutputStreamWriter outWriter;

    /**
     * Creates a new BasicTextOutputModule connected to an OutputStream.
     *
     * @param os the OutputStream to connect to
     */
    public BasicTextOutputModule(OutputStream os)
    {
        super(os);
        outWriter = new OutputStreamWriter(os);
    }

    @Override
    public void write() throws IOException
    {
        if (links == null || links.isEmpty())
        {
            return;
        }
        for (Entry<File, Set<Link>> listEntry : links.entrySet())
        {
            outWriter.append(
                    "File: " + listEntry.getKey().getCanonicalPath() + "\n");
            for (Link link : listEntry.getValue())
            {
                outWriter.append("--Link:");
                List<String> paths = link.getAbsolutePaths();
                if (paths != null)
                {
                    for (String s : paths)
                    {
                        outWriter.append(
                                "\tAbsolute Path = " + s + "\n");
                    }
                }
                paths = link.getRelativePaths();
                if (paths != null)
                {
                    for (String s : paths)
                    {
                        outWriter.append(
                                "\tRelative Path = " + s + "\n");
                    }
                }

                paths = link.getUnknownPaths();
                if (paths != null)
                {
                    for (String s : paths)
                    {
                        outWriter.append(
                                "\tOther Path = " + s + "\n");
                    }
                }
                outWriter.append("\tType = " + link.getType() + " \n\n");
                outWriter.flush();
            }
        }
    }
}
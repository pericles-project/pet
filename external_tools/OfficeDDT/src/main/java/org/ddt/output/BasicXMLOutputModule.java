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
import java.io.PrintStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.ddt.Link;

/**
 *XML output module. @see OfficeLinkOutput.dtd
 *
 * \todo let people set the doctype?
 * 
 * @author Niklas Rehfeld
 */
public class BasicXMLOutputModule extends OutputModule
{

    static final String XML_HEADER = "<?xml version=\"1.0\"?>";
    static final String DOCTYPE_DECLARATION =
            "<!DOCTYPE file SYSTEM \"BasicXMLOutput.dtd\">";
    private PrintStream out;

    /**
     * Constructor.
     * @param os OutputStream to write to.
     */
    public BasicXMLOutputModule(OutputStream os)
    {
        super(os);
        out = new PrintStream(outStream);

    }

    @Override
    public void write() throws IOException
    {
        int numLinks = 0;
        out.println(XML_HEADER);
        out.println(DOCTYPE_DECLARATION);
        out.println("<links>");

        if (links.isEmpty())
        {
            return;
        }

        for (Entry<File, Set<Link>> listEntry : links.entrySet())
        {
            out.printf("<file path=\"%s\">\n", listEntry.getKey().
                    getCanonicalPath());
            for (Link l : listEntry.getValue())
            {
                numLinks++;
                out.printf("\t<link type=\"%s\">\n", l.getType());

                List<String> paths = l.getAbsolutePaths();
                if (paths != null)
                {
                    for (String pathString : paths)
                    {
                        out.print("\t\t<path type=\"absolute\">");
                        out.print(removeInvalidChars(pathString));
                        out.println("</path>");
                    }
                }
                paths = l.getRelativePaths();
                if (paths != null)
                {
                    for (String pathString : paths)
                    {
                        out.print("\t\t<path type=\"relative\">");
                        out.print(removeInvalidChars(pathString));
                        out.println("</path>");
                    }
                }
                paths = l.getUnknownPaths();
                if (paths != null)
                {
                    for (String pathString : paths)
                    {
                        out.print("\t\t<path type=\"other\">");
                        out.print(removeInvalidChars(pathString));
                        out.println("</path>");
                    }
                }
                out.println("\t</link>");
            }

            out.println("</file>");
        }
        out.printf("<count>%d</count>", numLinks);
        out.println("</links>");

    }

    /**
     * Removes &<>"' characters and turns all whitespace into spaces
     *
     * @param text text to clean up
     * @return a string with the characters rep[laced with their XML entities
     */
    private String removeInvalidChars(String text)
    {
        if (text == null)
            return "";
        String outString = text.replaceAll("&", "&amp;");
        outString = outString.replaceAll("<", "&lt;");
        outString = outString.replaceAll(">", "&gt;");
        outString = outString.replaceAll("\"", "&quot;");
        outString = outString.replaceAll("'", "&apos;");
        outString = outString.replaceAll("\\s", " ");

        return outString;
    }
}

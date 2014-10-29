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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import org.ddt.Link;

/**
 * This output the links in CSV format, i.e.
 * <code> 
 * sourceFile, [absolutePaths], [relativePaths], [otherPaths] <br>
 * sourceFile, [absolutePaths], [relativePaths], [otherPaths]</code>
 *
 * @author Niklas Rehfeld
 */
public class CSVOutputModule extends OutputModule
{

    /**
     * The separator to use between fields.
     */
    public static String separator = ", ";
    private OutputStreamWriter outWriter;

    /**
     * Constructor.
     *
     * @param o Output Stream to write to.
     */
    public CSVOutputModule(OutputStream o)
    {
        super(o);
        outWriter = new OutputStreamWriter(o);
    }

    @Override
    public void write() throws IOException
    {
        if (links.isEmpty())
        {
            return;
        }
        for (Entry<File, Set<Link>> fileLinks : links.entrySet())
        {
            for (Link l : fileLinks.getValue())
            {
                outWriter.append(fileLinks.getKey().getCanonicalPath() + separator);
//                if (!(l.getAbsolutePath() == null))
//                {
//                    outWriter.append(l.getAbsolutePath());
//                }
//                outWriter.append(separator);
//                if (!(l.getRelativePath() == null))
//                {
//                    outWriter.append(l.getRelativePath());
//                }

                outWriter.append("[");
                List<String> paths = l.getAbsolutePaths();
                for (Iterator<String> i = paths.iterator(); i.hasNext();)
                {
                    outWriter.append(i.next());
                    if (i.hasNext())
                        outWriter.append(separator );
                }
                outWriter.append("]");
               
                outWriter.append(separator) ;

                outWriter.append("[");
                paths = l.getRelativePaths();
                for (Iterator<String> i = paths.iterator(); i.hasNext();)
                {
                    outWriter.append(i.next());
                    if (i.hasNext())
                        outWriter.append(separator );
                }
                outWriter.append("]");

                outWriter.append(separator);

                outWriter.append("[");
                paths = l.getUnknownPaths();
                for (Iterator<String> i = paths.iterator(); i.hasNext();)
                {
                    outWriter.append(i.next());
                    if (i.hasNext())
                        outWriter.append(separator );
                }
                outWriter.append("]");
                
                outWriter.append(" \n");
                outWriter.flush();
            }


        }

    }
//
//    public void setSeparatorString(String separator)
//    {
//        this.separator = separator;
//    }
}

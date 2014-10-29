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
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ddt.Link;

/**
 * Superclass for all output modules.
 *
 * Implementations can be very different for this. The plan is to have
 * implementations for:
 * - human-readable text (BasicTextOutputModule)
 * - XML (BasicXMLOutputModule)
 * - CSV (CSVOutputModule)
 *
 * @author Niklas Rehfeld
 */
public abstract class OutputModule
{
//    /** number of links found, not including duplicates*/
//    private int linksFound;
    private static Logger log = Logger.getLogger("org.ddt");

    /** output stream to write to*/
    protected OutputStream outStream;
//    /**The file that is being looked at*/
//    protected File file;
    /**
     * holds the links, grouped by referencing file, as they get collected.
     */
    protected HashMap<File, Set<Link>> links;

    /**
     * Subclasses need to override or at least call this one.
     * 
     * @param oStream an open OutputStream that can be written to.
     */
    public OutputModule(OutputStream oStream)
    {
        outStream = oStream;
        links = new HashMap<File, Set<Link>>();
    }

    /**
     * Write the information in this module out to the output stream. 
     * 
     * The typical way of using this would be to call this method once per file,
     * after all of the officelink.processor.FileProcessor&thinsp;s have finished processing it.
     *
     * \note This is the only method that actually writes anything, so should 
     * be called for every file processed.
     *
     * @throws IOException 
     */
    public abstract void write() throws IOException;

    /**
     * Set the OutputStream to write to
     * 
     * @param oStream the stream to write to
     */
    public void setOutputStream(OutputStream oStream)
    {
        try
        {
            outStream.close(); //old one...
        } catch (IOException ex)
        {
            log.log(Level.INFO, "Couldn't close old OutputStream.",
                    ex);
        }
        outStream = oStream;
    }

    /**
     * Adds a link to the list of links found. These can then be written out ( to
     * the java.io.OutputStream that was either given in the constructor or set
     * with setOutputStream() ) with writeItem()
     *
     * @param f file that contains the link
     * @param link The link to write out.
     */
    public void addLink(File f, Link link)
    {
        log.log(Level.INFO, "Adding link {0}for file{1}",
                new Object[]
                {
                    link.toString(),
                    f.getName()
                });
        Set<Link> l;
        if (links.containsKey(f))
        {
            l = links.get(f);
            if (!l.contains(link))
                l.add(link);
        } else
        {
            l = new TreeSet<Link>();
            l.add(link);
            links.put(f, l);
        }
    }

    /**
     * Adds a set of links that belong to a file to the output.
     *
     * @param f the File that contains the links
     * @param l the links 
     */
    public void addLinks(File f, Collection<Link> l)
    {
        if (l == null || l.isEmpty())
            return;

        log.log(Level.FINE, "adding list {0} to outputModule", l.
                toString());

        if (links.containsKey(f))
        {
            log.log(Level.FINE, "File exists, adding to list");
            links.get(f).addAll(l);
        } else
        {
            log.log(Level.FINE,
                    "File doesn't exist, adding to map.");
            links.put(f, new TreeSet<Link>(l));
            log.log(Level.FINE, "List now contains: {0}", links.
                    get(f).toString());
        }
    }

    /**
     *
     * @return the number of links.
     */
    public int getNumberLinksFound()
    {
        int count = 0;
        for (Set<Link> s : links.values())
        {
            count += s.size();
        }
        return count;
    }
}

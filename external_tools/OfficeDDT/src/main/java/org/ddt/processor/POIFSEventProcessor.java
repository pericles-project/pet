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
package org.ddt.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import org.ddt.Link;
import org.ddt.listener.POIFSEventListener;

/**
 * This is a wrapper class that allows the use of the POIFS eventfilesystem,
 * without having to start a new reader for each listener.
 *
 * If a file processor is going to use the POIFS event file system, it should
 * probably just register a listener in this class instead of setting up a new
 * reader and going through the whole file again.
 *
 * It is configured to run on all file types.
 *
 * @author Niklas Rehfeld
 */
public final class POIFSEventProcessor extends FileProcessor
{
    
    private static Logger log = Logger.getLogger("org.ddt");
    /** Reader that will process the files */
    private POIFSReader reader;
    /** The listeners that will do process the files */
    private List<POIFSEventListener> listeners;
    /** holds the links that have been collected. Needs to be cleared for each new file processed. */
    private List<Link> links;

    /**
     * Constructor.
     */
    public POIFSEventProcessor()
    {
        links = new ArrayList<Link>();
        reader = new POIFSReader();
        listeners = new ArrayList<POIFSEventListener>();
    }

    /**
     * @see FileProcessor#process(File f)
     */
    @Override
    public List<Link> process(File f) throws FileNotFoundException, IOException
    {
        links.clear();
        for (POIFSEventListener e : listeners)
        {
            e.clear();
        }
        reader.read(new FileInputStream(f));
        for (POIFSEventListener e : listeners)
        {
            links.addAll(e.getLinks());
        }
        log.log(Level.FINE, "returning {0}", links.toString());
        return Collections.unmodifiableList(links);
    }

    /**
     * Delegated method for that calls POIFSReader.registerListener(r)
     *
     * Adds a
     * <code>POIFSEventListener</code> to process all documents.
     *
     * <b> WARNING</b> this causes all other listeners to be ignored, and so should not be used if there are other listeners.
     *
     * @see POIFSReader#registerListener(org.apache.poi.poifs.eventfilesystem.POIFSReaderListener)
     *
     * @param r POIFSReaderListener that is to be registered.
     */
    public void registerListener(POIFSEventListener r)
    {
	log.fine("Added a "+r.getClass().getName() + " for all documents");
        listeners.add(r);
        reader.registerListener(r);
    }

    /**
     * Adds a
     * <code>POIFSEventListener</code> to process documents with a certain name.
     *
     * @param r    POIFSEventListener to use
     * @param name document names to listen on.
     * @see POIFSReader#registerListener(org.apache.poi.poifs.eventfilesystem.POIFSReaderListener,
     * java.lang.String)
     *
     */
    public void registerListener(POIFSEventListener r, String name)
    {
	log.fine("Added a " + r.getClass().getName() + " for documents named "+  name);
        listeners.add(r);
        reader.registerListener(r, name);
    }


    /**
     * Adds a
     * <code>POIFSEventListener</code> to process documents with a certain name in a certain path.
     *
     * @param r    POIFSEventListener to add
     * @param name document name to listen on
     * @param path document path to listen on
     * @see POIFSReader#registerListener(org.apache.poi.poifs.eventfilesystem.POIFSReaderListener,
     * org.apache.poi.poifs.filesystem.POIFSDocumentPath, java.lang.String)
     */
    public void registerListener(POIFSEventListener r, String name,
            POIFSDocumentPath path)
    {
        listeners.add(r);
        reader.registerListener(r, path, name);
    }

}

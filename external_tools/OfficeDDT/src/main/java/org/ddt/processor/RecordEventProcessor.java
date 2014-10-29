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
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.ddt.Constants;
import org.ddt.Link;
import org.ddt.listener.RecordEventListener;

/**
 * This class is used to process Excel records. These are represented by
 * {@link officelink.listener.RecordEventListener} subclasses.
 * <p/>
 * This only runs on Excel files.
 *
 * @author Niklas Rehfeld
 */
public final class RecordEventProcessor extends FileProcessor
{

    private static Logger log = Logger.getLogger("org.ddt");
    private HSSFRequest request;
    private HSSFEventFactory eventFactory;
    /** List of RecordEventListeners that will do the actual processing */
    private List<RecordEventListener> listeners;
    /** List of links that have been collected. Needs to be cleared for each new file processed. */
    private List<Link> links;

    /**
     * Constructor.
     */
    public RecordEventProcessor()
    {
        links = new ArrayList<Link>();
        request = new HSSFRequest();
        fileTypeMask &= Constants.FILETYPE_EXCEL;
        eventFactory = new HSSFEventFactory();
        listeners = new ArrayList<RecordEventListener>();
    }

    /**
     * @bug doesn't like some excel files, that seem to report a different
     * record size than they actually are... Not entirely sure if the files
     * are broken, or the implementation is somewhere.
     */
    @Override
    public List<Link> process(File f) throws FileNotFoundException, IOException
    {
        links.clear();
        for (RecordEventListener l : listeners)
        {
            l.clear();
        }

        FileInputStream is = new FileInputStream(f);
        POIFSFileSystem fs = new POIFSFileSystem(is);
        try
        {
            eventFactory.processWorkbookEvents(request, fs);
        } catch (RecordInputStream.LeftoverDataException lde)
        {
            //this can be thrown when there are broken excel documents, that is,
            //when the reported size of the record and the actual record size
            //differ. this will log the failure and go on.
            log.log(Level.WARNING, "Broken(ish) Excel File: {0}",
                    f.getName());
        } finally
        {
            is.close();
        }
        for (RecordEventListener listener : listeners)
        {
            log.log(Level.FINEST, "Getting links from {0}",
                    listener.getClass().getName());
            links.addAll(listener.getLinks());
        }
        log.log(Level.FINE, "returning {0}", links.toString());
        return Collections.unmodifiableList(links);
    }

    /**
     * Register a {@link officelink.listener.RecordEventListener} to listen on all Records.
     * <p/>
     * delegates to HSSFRequest.addListenerForAllRecords().
     * <p/>
     * @param log listener to register.
     * @see HSSFRequest#addListenerForAllRecords(org.apache.poi.hssf.eventusermodel.HSSFListener)
     */
    public void registerListener(RecordEventListener l)
    {
        listeners.add(l);
        request.addListenerForAllRecords(l);
    }

    /**
     * Register a {@link officelink.listener.RecordEventListener} to listen on Records with a given
     * SID.
     * <p/>
     * delegates to HSSFRequest.addListener()
     * <p/>
     * @param log The listener
     * @param sid The SID of the record to listen to.
     * @see HSSFRequest#addListener(org.apache.poi.hssf.eventusermodel.HSSFListener, short)
     */
    public void registerListener(RecordEventListener l, short sid)
    {
        listeners.add(l);
        request.addListener(l, sid);
    }
}

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
package org.ddt.listener.ole;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ddt.BadOleStreamException;
import org.ddt.Link;
import org.ddt.listener.POIFSEventListener;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndian;

/**
 * Reads
 * <code>\1Ole</code> streams [MS-OLEDS s. 2.3.3] and looks for OLE links within them.
 * <p/>
 * This is set to run on all file types (although it could possibly be removed for ppt files?).
 *
 * @author Niklas Rehfeld
 */
public class OleStreamListener extends POIFSEventListener
{

    private static Logger log = Logger.getLogger("org.ddt");
    /** Valid 'signature' string for an Ole Stream */
    public static final int VALID_VERSION = 0x2000001;

    /**
     * Creates an OleStreamListener.
     */
    public OleStreamListener()
    {
        super();
    }

    /**
     * Reads a "\1Ole" stream and stores the links found in it.
     * <p/>
     * This method returns (fairly) quietly if the stream fails (it doesn't throw exceptions)
     * There are a number of ways that this can fail, not all of which are bad.
     * For instance, POIFSReaderEvent doesn't contain an OLE stream, this is not
     * a disaster, we just need to return quickly.
     *
     * @param event document to process
     */
    public void processPOIFSReaderEvent(POIFSReaderEvent event)
    {
        log.log(Level.FINEST, "Processing Document: {0}/{1}",
                new Object[]
                {
                    event.getPath(),
                    event.getName()
                });
        DocumentInputStream docInStream = event.getStream();

        if (docInStream.available() < LittleEndian.INT_SIZE)
            return;

        if (docInStream.readInt() != VALID_VERSION)
        {
            log.log(Level.INFO,
                    "Invalid signature - not an OLE Stream.");
            docInStream.close();
            return;
        }
        try
        {
            docInStream.skip(LittleEndian.INT_SIZE); //ignore what I think might be LinkUpdateOptions
            //check it's a linked object, not embedded
            if (docInStream.readInt() != 1)
            {
                log.log(Level.FINER, "Not a link");
                docInStream.close();
                return;
            }

            //check reserved field = 0
            if (docInStream.readInt() != 0x000000)
            {
                docInStream.close();
                return;
            }

            Moniker m;
            String relPath = null;
            String absPath = null;
            byte[] clsid = new byte[16];
            //source moniker, not really interesting.
            if ((docInStream.readInt()) > 0)
            {
                docInStream.read(clsid);
                ClassID cid = new ClassID(clsid, 0);
                MonikerFactory.getMoniker(cid, docInStream);
            }
            if ((docInStream.readInt()) > 0)
            {
                docInStream.read(clsid);
                ClassID cid = new ClassID(clsid, 0);
                m = MonikerFactory.getMoniker(cid, docInStream);
                if (m != null)
                    relPath = m.getLink();
            }
            if ((docInStream.readInt()) > 0)
            {
                docInStream.read(clsid);
                ClassID cid = new ClassID(clsid, 0);
                m = MonikerFactory.getMoniker(cid, docInStream);
                if (m != null)
                    absPath = m.getLink();
            }

            Link l = new Link(1);
            l.addRelativePath(cleanURLString(relPath));
            l.addAbsolutePath(absPath);
            this.add(l);
        } catch (IOException ex)
        {
            log.log(Level.FINE, ex.getLocalizedMessage());
        } catch (BadOleStreamException ex)
        {
            log.log(Level.INFO, ex.getMessage());
        } finally
        {
            docInStream.close();
        }
    }

    /**
     * removes the last part of a string, after a "!\0" string.
     *
     * If I remember correctly, the part after that is a sheet reference or something like that.
     *
     * @param s string to clean.
     * @return
     */
    private String cleanURLString(String s)
    {
        if (s == null)
            return null;
        String out = s.trim();
        int idx = s.indexOf("!\u0000");
        if (idx >= 0)
            out = s.substring(0, idx);
        return out;
    }
}

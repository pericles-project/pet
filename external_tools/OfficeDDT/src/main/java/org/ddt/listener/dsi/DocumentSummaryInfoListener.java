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
package org.ddt.listener.dsi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hpsf.*;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.ddt.Link;
import org.ddt.listener.POIFSEventListener;

/**
 * Reads DocumentSummaryInformation streams and looks for links. This is mainly
 * useful for powerpoint files.
 *
 * @author Niklas Rehfeld
 */
public class DocumentSummaryInfoListener extends POIFSEventListener
{

    private static final Logger log = Logger.getLogger("org.ddt");

    /**
     * Constructor.
     */
    public DocumentSummaryInfoListener()
    {
        super();
    }


    public void processPOIFSReaderEvent(POIFSReaderEvent event)
    {
        log.log(Level.FINEST, "reading {0}{1}", new Object[]{event.getPath(), event.getName()});
        DocumentInputStream is = event.getStream();
        try
        {
            PropertySet ps = PropertySetFactory.create(is);

            if (!(ps instanceof DocumentSummaryInformation))
                return;

            Property docparts = null;
            Property headings = null;
            for (Property prop : ps.getProperties())
            {
                if (prop.getID() == PropertyIDMap.PID_HEADINGPAIR) // == 12
                    headings = prop;
                else if (prop.getID() == PropertyIDMap.PID_DOCPARTS)
                    docparts = prop;
            }

            if (docparts == null)
	    {
		log.log(Level.FINE, "No DOCPARTS section");
		return;
	    }

            if (headings == null)
                return;

            HeadingPairVector hdv = new HeadingPairVector((byte[]) headings.getValue(), 0);

            StringVector docpartsVector = new StringVector((byte[]) docparts.getValue(), 0,
                    docparts.getType());

            HeadingPairProperty linkHeader = hdv.getHeadingPairByName(
                    "Links"); //*NOT* null terminated

            if (linkHeader == null)
            {
                log.log(Level.INFO, "No 'Links' header found.");
                return;
            }
            else
            {
                log.log(Level.FINEST, "Found {0} link parts", linkHeader.getPartsCount());
            }

            //need to iterate through all of the ones if there's more than one
            //docpart for the header.
            int part = linkHeader.getOffset();
            for (int i = 0; i < linkHeader.getPartsCount(); i++)
            {
                String url = docpartsVector.get(part).getValue();
                log.log(Level.FINEST, "adding {0} to list of links.", url);
                url = url.trim();
                Link l = new Link(3);
                l.addUnkownPath(url);
                this.add(l);
                part++;
            }

        } catch (NoPropertySetStreamException ex)
        {
            log.log(Level.INFO, "Not a PropertySetStream {0}{1}", new Object[]{event.getPath(),
                    event.getName()});
        } catch (MarkUnsupportedException ex)
        {
            log.log(Level.INFO, "Couldn't create PropertySet: {0}", ex.getLocalizedMessage());
        } catch (UnsupportedEncodingException ex)
        {
            log.log(Level.INFO, null, ex);
        } catch (IOException ex)
        {
            log.log(Level.INFO, null, ex);
        } catch (HPSFException ex)
        {
            log.log(Level.WARNING,
                    "Couldn't construct HeadingPair vector.", ex);
        } finally
        {
            is.close();
        }
    }
}

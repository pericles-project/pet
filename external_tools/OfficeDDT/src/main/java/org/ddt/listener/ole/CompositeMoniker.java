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
import java.util.ArrayList;
import java.util.List;
import org.ddt.BadOleStreamException;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * Represents an ItemMoniker structure.
 *
 * These are Monikers that contain other Monikers. They are (allegedly) designed
 * so that you can compose (i.e. concatenate) several partial paths to give a
 * complete one. 
 *
 * @author Niklas Rehfeld
 */
class CompositeMoniker implements Moniker
{

    /** the number of submonikers in this CompositeMoniker*/
    private int monikerCount;
    /** List of Monikers that are inside this Moniker */
    private List<Moniker> monikerArray;

    /**
     * Creates this moniker and then all of the submonikers as well.
     *
     * @param inStream DocumentInputStream opened at the start of this Moniker.
     * @throws IOException
     * @throws BadOleStreamException
     */
    CompositeMoniker(DocumentInputStream inStream) throws IOException, BadOleStreamException
    {

        monikerCount = inStream.readInt();
        monikerArray = new ArrayList<Moniker>(monikerCount);
        for (int i = 0; i < monikerCount; i++)
        {
            byte[] classID = new byte[16];
            inStream.read(classID);
            ClassID cid = new ClassID(classID, 0);
            Moniker m = MonikerFactory.getMoniker(cid, inStream);
            if (m != null)
            {
                monikerArray.add(m);
            }
        }
    }

    /**
     * Concatenates the composed monikers into one string.
     * This is because usually composite monikers are something like a
     * FileMoniker and then some ItemMonikers or similar, to give a specific
     * part of a document. 
     *
     * @return a string containing all of the submonikers' links concatenated together.
     */
    public String getLink()
    {
        String links = "";
        for (String s : getLinks())
        {
            links += s;
        }
        return links;
    }

    /**
     * Gets all of the links of this Moniker's submonikers.
     * @return a list of all of the submonikers' links.
     */
    private List<String> getLinks()
    {
        List<String> links = new ArrayList<String>(monikerCount);
        for (Moniker m : monikerArray)
        {
            links.add(m.getLink());
        }
        return links;
    }
}

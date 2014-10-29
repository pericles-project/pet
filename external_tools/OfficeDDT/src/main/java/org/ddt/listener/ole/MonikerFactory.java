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
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.ddt.BadOleStreamException;

/**
 * Creates Monikers depending on their ClassIDs. 
 * 
 * So far it creates Monikers of the types: 
 * - FileMoniker
 * - CompositeMoniker
 * - ItemMoniker
 * - URLMoniker
 *
 * Missing are AntiMonikers. But they're horrible so I'm ignoring them.
 *
 * \bug something inside the CompositeMoniker implementation dies sometimes and
 * then there's an unknown classID that throws this class off.
 *
 * @author Niklas Rehfeld
 */
class MonikerFactory
{
    private static Logger log = Logger.getLogger("org.ddt");

    /**She class id/signature of a FileMoniker*/
    static final byte[] MONIKER_TYPE_FILE =
    {
        3, 3, 0, 0, 0, 0, 0, 0, (byte) 0xC0, 0, 0, 0, 0, 0, 0, (byte) 0x46
    };
    /**ClassID of a File Moniker type.*/
    static final ClassID FILE_MONIKER_ID = new ClassID(MONIKER_TYPE_FILE,
            0);
    /**The class id/signature of a CompositeMoniker*/
    static final byte[] MONIKER_TYPE_COMPOSITE =
    {
        9, 3, 0, 0, 0, 0, 0, 0, (byte) 0xC0, 0, 0, 0, 0, 0, 0, (byte) 0x46
    };
    /**ClassID of a Composite Moniker type.*/
     static final ClassID COMPOSITE_MONIKER_ID = new ClassID(
            MONIKER_TYPE_COMPOSITE, 0);
    /** The class id/signature of a URLMonitor*/
    static final byte[] MONIKER_TYPE_URL =
    {
        (byte) 0xE0, (byte) 0xC9, (byte) 0xEA, (byte) 0x79,
        (byte) 0xF9, (byte) 0xBA, 0x11, (byte) 0xCE,
        (byte) 0x8C, (byte) 0x82, 0x00, (byte) 0xAA,
        0x00, 0x4B, (byte) 0xA9, 0x0B
    };
    /**ClassID of a URL Moniker type.*/
    static final ClassID URL_MONIKER_ID = new ClassID(MONIKER_TYPE_URL, 0);
    /**The class id/signature of a Item Moniker*/
    static final byte[] MONIKER_TYPE_ITEM =
    {
        4, 3, 0, 0, 0, 0, 0, 0, (byte) 0xC0, 0, 0, 0, 0, 0, 0, (byte) 0x46
    };
    /**ClassID of an Item Moniker type.*/
    static final ClassID ITEM_MONIKER_ID = new ClassID(MONIKER_TYPE_ITEM,
            0);

    /**
     * Retrieves the appropriate Moniker type for the given class id.
     *
     * The Moniker is initialised from the input stream.
     *
     * @param clsid The class id of the Moniker type
     * @param is The <code>DocumentInputStream</code> to read the Moniker from.
     * @return A Moniker, or <b>null</b> if the class id is not recognised.
     * @throws IOException
     * @throws BadOleStreamException If a malformed Moniker Stream is found. 
     */
    static Moniker getMoniker(ClassID clsid, DocumentInputStream is) throws IOException, BadOleStreamException
    {
        if (clsid.equals(FILE_MONIKER_ID))
        {
            return new FileMoniker(is);
        } else if (clsid.equals(COMPOSITE_MONIKER_ID))
        {
            return new CompositeMoniker(is);
        } else if (clsid.equals(URL_MONIKER_ID))
        {
            return new URLMoniker(is);
        } else if (clsid.equals(ITEM_MONIKER_ID))
        {
            return new ItemMoniker(is);
        } else
        {
            log.log(Level.WARNING, "ClassID unknown: {0}", clsid.
                    toString());
            return null;
        }

    }
}

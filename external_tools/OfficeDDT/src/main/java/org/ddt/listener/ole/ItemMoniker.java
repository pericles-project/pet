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
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * Represents an ItemMoniker, used to identify an object within a container.
 * <p/>
 * These are not very interesting, so it basically just clears the moniker out
 * of the way with as little fuss as possible.
 *
 * \warning This class doesn't return any useful information.
 * \todo get the delimiters. That could be useful for cleaning up links.
 *
 * @author Niklas Rehfeld
 */
class ItemMoniker implements Moniker
{

    /** delimiter field length in bytes. can either mean both the unicode and
     * ansi ones, or just the ansi one if the unicode one doesn't exist. */
    private int delimLength;
    private String delimAnsi;
    /** length of the item string. Can either mean ansi + unicode strings, or
     * just ansi if no unicode string.
     */
    private int itemLength;
//    /** the ansi string of the item. in this implementation this is actually either
//     * the ansi string or the ansi string followed by the unicode string. I'm lazy.
//     */
    private String itemAnsi;

    /**
     *
     * @param is DocumentINputStream opened at the start of the Moniker struct.
     * @throws IOException
     */
    ItemMoniker(DocumentInputStream is) throws IOException
    {
        delimLength = is.readInt();
        byte[] delim = new byte[delimLength];
        is.read(delim);
        delimAnsi = new String(delim);
        itemLength = is.readInt();
        byte[] item = new byte[itemLength];
        is.read(item);
        itemAnsi = new String(item);
    }

    /**
     * Given that these monikers are not really very interesting, this does nothing.
     *
     * @return an empty string.
     */
    public String getLink()
    {
        return delimAnsi + itemAnsi;
    }
}

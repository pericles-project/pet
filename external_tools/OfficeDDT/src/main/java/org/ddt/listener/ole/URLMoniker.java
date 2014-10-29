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
 *
 * @author Niklas Rehfeld
 */
class URLMoniker implements Moniker
{

    /** the length. This can mean *either*
     * - the length of the URL field including terminating NULL char, *or*
     * - the length of the whole structure minus this field. (i.e. URL field + 24)
     * hurrah for microsoft...
     */
    private int length;
    /** The URL string. terminated by a NULL char. */
    private String URL;
    //there are more, but they aren't interesting. (24 bytes more)

    /**
     * Constructor.
     * <p/>
     * @param is an opened DocumentInputStream, positioned at the beginning of
     * the moniker structure.
     * @throws IOException if something goes wrong with the DocumentInputStream.
     */
    URLMoniker(DocumentInputStream is) throws IOException
    {
        length = is.readInt();
        byte[] b = new byte[length];
        is.read(b);
        String tempUrl = new String(b);
        //check what 'length' means.
        int termPos = tempUrl.indexOf("\0");
        if (termPos < (tempUrl.length() - 1))  //it means structure length
        {
            URL = tempUrl.substring(0, termPos);
        } else //it means string length
        {
            URL = tempUrl;
            is.skip(24); //it didn't read to the end of the struct.
        }

    }


    public String getLink()
    {
        return URL;
    }
}

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
import org.ddt.BadOleStreamException;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * Represents a FileMoniker as described in MS-OSHARED Section 2.3.7.
 *
 * @author Niklas Rehfeld
 */
class FileMoniker implements Moniker
{

    /** Correct version number for a FileMoniker */
    public static final short VERSION_NUMBER = (short) 0xDEAD;
    /** unsigned int, no. of parent directory indicators* */
    private short cAnti;            // 2 bytes
    /** unsigned int <= 32767, no. of ANSI chars in ansiPath */
    private int ansiLength;         // 4 bytes
    /** null-terminated array of ANSI chars specifying the path. */
    private byte[] ansiPath;        // variable length
    /** unsigned int, no, of unicode chars used to specify the server portion
     * of the UNC path (if needed). if not a UNC path, must be set to oxFFFF */
    private short endServer;        // 2 bytes
    /** unsigned int, must be 0xDEAD */
    private short versionNumber;    // 2 bytes
    /** unsigned int, size in bytes of ucPathBytes, usKeyValue and unicodePath fields.
     * must be equal to either the size of the path as a unicode string without \0
     * + 6, or zero if there is no unicode path. */
    private int unicodePathSize;   // 4 bytes
    /** unsigned int, specifies the size in bytes of the unicodePath field.
     * Optional, exists iff unicodePathSize > 0. */
    private int unicodePathBytes;   // 4 bytes
    /** unsigned int, exists iff unicodePathSize > 0. must equal 3 if it exists. */
    private short usKeyValue;       // 2 bytes
    /** array of unicode chars. 2 bytes per char for normal chars. no terminating null.
     * The complete unicode representation of the path in ansiPath.
     * Exists iff unicodePathSize >0 */
    private byte[] unicodePath;     // variable length

    /**
     * Constructs a FileMoniker from a DocumentInputStream.
     * This consumes the stream til the end of the FileMoniker object, so that
     * when it is done the stream is positioned just past the FileMoniker.
     * <p/>
     * @param is DocumentInputStream positioned at the start of a (Hyperlink)
     * Moniker Stream, i.e. just before the monikerClsid field.
     * @throws IOException           if there's something bung with the stream.
     * @throws BadOleStreamException
     * <p/>
     */
    FileMoniker(DocumentInputStream is) throws IOException, BadOleStreamException
    {
        //check the stream is ready...
        if (is.available() <= 0)
        {
            throw new IOException("Nothing in the stream");
        }

        DocumentInputStream inStream = is;
        populate(inStream);

    }

    /**
     * Helper function that populates the fields of this moniker from the given stream
     *
     * @param inStream the stream to read from
     * @throws IOException
     * @  throws
     *                                                                                                                                                                                        BadOleStreamException
     */
    private void populate(DocumentInputStream inStream) throws IOException, BadOleStreamException
    {

        cAnti = inStream.readShort();

        ansiLength = inStream.readInt();
        ansiPath = new byte[ansiLength - 1]; //don't get the last char (\0)
        if (inStream.read(ansiPath) != ansiLength - 1)
        {
            throw new BadOleStreamException("Couldn't read Path.");
        }
        inStream.skip(1); //skip the string terminating null char

        endServer = inStream.readShort();

        versionNumber = inStream.readShort();
        if (versionNumber != VERSION_NUMBER)
        {
            throw new BadOleStreamException("wrong version number: " + this.
                    getVersionNumberHexString());
        }

        inStream.skip(20); //skip the two reserved fields. should check that they are both 0.

        unicodePathSize = inStream.readInt();

        //the rest of the fields only exist if unicodePathSize > 0
        if (unicodePathSize <= 0)
        {
            return;
        }

        unicodePathBytes = inStream.readInt();
        usKeyValue = inStream.readShort();

        //should really make this a char array or a string.
        unicodePath = new byte[unicodePathBytes];
        for (int i = 0; i < (unicodePathBytes); i++)
        {
            unicodePath[i] = inStream.readByte();
        }
    }

    /**
     *
     * @return the path that this moniker contains
     */
    String getPath()
    {
        return new String(ansiPath);
    }

    /**
     * @return the length (number of chars) of the ANSI path string.
     */
    int getAnsiLength()
    {
        return ansiLength;
    }

    /**
     * @return the version number of this moniker. For a valid FileMoniker, it
     * should be
     * <code>0xDEAD = 57005</code>.
     */
    short getVersionNumber()
    {
        return versionNumber;
    }

    /**
     *
     * @return hex representation of the version number. Should be
     * <code>0xDEAD</code> for a
     * valid FileMoniker.
     */
    String getVersionNumberHexString()
    {
        return String.format("0x%x", versionNumber);
    }

    /**
     *
     * @return unicode path or null if field doesn't exist.
     */
    byte[] getUnicodePath()
    {
        return unicodePath;
    }

    /**
     * @return String containing the unicode path or null if the field doesn't exist.
     */
    String getUnicodePathString()
    {
        if (unicodePath == null)
        {
            return null;
        }
        String ucPath = "";
        for (int i = 0; i < unicodePathBytes; i += 2)
        {
            ucPath += (char) unicodePath[i];
        }
        return ucPath;
    }

    /**
     * @return size of the unicode path in bytes
     */
    int getUnicodePathSize()
    {
        return unicodePathSize;
    }


    public String getLink()
    {
        if (unicodePathSize > 0)
        {
            return getUnicodePathString();
        }
        return getPath();
    }
}

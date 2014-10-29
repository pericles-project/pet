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

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.util.LittleEndian;

/**
 * Represents a
 * <code>VtVecHeadingPairValue</code> property type. These are the vectors of
 * headers (i.e. human-readable names) that correspond to the sections of the
 * DOCPARTS vector. They are found in DocumentSummaryInformation streams.
 *
 * It is not a
 * <code>VtHeadingPair</code> structure, because the
 * <code>Section.getProperty()</code> method used to get the byte array to
 * construct them seems to eat up the type field.
 *
 * it should look a little like this.
 * <pre>
 * -----------------------------------
 * |cElements | HeadingPairs...      |
 * |          |(HeadingPairProperty) |
 * |----------|----------------------|
 * | 4 bytes  |  variable...         |
 * -----------------------------------
 * </pre>
 * <p/>
 * @author Niklas Rehfeld
 */
class HeadingPairVector
{

    private static final Logger log = Logger.getLogger("org.ddt");
    /** the heading pairs in this vector */
    private HeadingPairProperty[] props;
    /** The number of vector items. This is actually twice the number of heading pairs, as each pair
     * consists of two items. Because it's a pair. */
    private int count;

    /** Read constructor for a VtVecHeadingPairValue
     *
     * @param data   a byte array containing a VtVecHeadingPairValue structure.
     * @param offset the offset into the array that the structure starts at.
     * @throws HPSFException                this will either be a generic
     * <code>HPSFException</code> if the count field of the vector is not even, or an
     * <code>IllegalVariantTypeException</code> if one of the vector items is malformed.
     * @throws UnsupportedEncodingException
     */
    HeadingPairVector(byte[] data, int offset) throws
            UnsupportedEncodingException, HPSFException
    {
        read(data, offset);
    }

    /**
     * Populate the HeadingPairVector from the byte array.
     *
     * @param data   byte array to read from
     * @param offset offset into byte array to start at.
     */
    private void read(byte[] data, int offset) throws HPSFException,
            UnsupportedEncodingException
    {
        int off = offset;
        count = (int) LittleEndian.getUInt(data, off);
        if ((count % 2) != 0)
        {
            throw new HPSFException("The count of a HeadingPairVector must be even.");
        }
        count = count / 2;
        off += LittleEndian.INT_SIZE;

        log.log(Level.FINE, "{0} headers found", count);

        props = new HeadingPairProperty[count];

        int docOffset = 0;
        for (int i = 0; i < count; i++)
        {
            props[i] = new HeadingPairProperty(data, off, docOffset);
            docOffset += props[i].getPartsCount();
            off += props[i].getSize();
        }

    }

    /**
     *
     * @param name
     * @return the
     * <code>HeadingPairProperty</code> with the given name, or
     * <code>null</code> if there is none with that name.
     */
    HeadingPairProperty getHeadingPairByName(String name)
    {
        for (HeadingPairProperty h : props)
        {
            if (h.getName().equalsIgnoreCase(name))
            {
                return h;
            }
        }
        return null;
    }

    /**
     * Gets the offset into the DOCPARTS vector that is the start of the
     * document part that has the given name.
     *
     * @note not case sensitive. (is that a good idea?)
     *
     * @param name the header of the document part.
     * @return an offset into the DOCPARTS vector or -1 if not found.
     */
    int getDocpartOffsetByName(String name)
    {
        for (HeadingPairProperty h : props)
        {
            if (h.getName().equalsIgnoreCase(name))
            {
                return h.getOffset();
            }
        }
        return -1;
    }
}

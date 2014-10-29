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
import org.apache.poi.hpsf.IllegalVariantTypeException;
import org.apache.poi.hpsf.Variant;
import org.apache.poi.util.LittleEndian;

/**
 * Represents a VtHeadingPair structure (MS-OSHARED 2.3.3.1)
 * According to MS this consists of a VtUnalignedString structure and a VT_I4
 * typed property (int32).
 *
 * In this implementation it contains a StringProperty and an int which holds
 * the value of the VT_I4 property.
 *
 * The StringProperty contains the header string and the integer contains the
 * number of document parts that are associated with this header.
 *
 * <pre>
 * -------------------------------
 * | headingString | headerParts |
 * |---------------|-------------|
 * |   variable    |  8 bytes    |
 * -------------------------------
 *                        |
 *              ---------------------
 *              |   type  |  value  |
 *              |---------|---------|
 *              | 4 bytes | 4 bytes |
 *              ---------------------
 * </pre>
 *
 * \note because of the workaround to POI bug #52337, if the array returned is
 * bad, <b>and</b> there are more than 7 links in the "links" docpart, then
 * this will only find the first 7. This is highly unlikely though, as it
 * doesn't seem like PowerPoint documents suffer from this bug, and they
 * seem to be the only ones that store their data this way.
 *
 * @todo error checking.
 * <p/>
 * @author Niklas Rehfeld
 */
class HeadingPairProperty
{

    private static final Logger log = Logger.getLogger("org.ddt");
    /** the header string */
    private StringProperty name;
    /** the number of document parts that are under this header */
    private int partsCount;
    /** the offset into the DOCPARTS vector of this header */
    private int docPartsOffset;
    /** the size in bytes of the structure, i.e. the number of bytes that were
     * read from the byte array. This does not reflect how much storage this
     * actual class takes up.
     */
    private int size;

    /**
     * the constructor.
     *
     *
     * @param data           the data to read from.
     * @param dataOffset     the offset into the
     * <code>data</code> byte array.
     * @param docPartsOffset the offset of the corresponding docparts.
     * @throws IllegalVariantTypeException  if the data is malformed.
     * @throws UnsupportedEncodingException
     */
    HeadingPairProperty(byte[] data, int dataOffset, int docPartsOffset) throws
            IllegalVariantTypeException, UnsupportedEncodingException
    {
        int off = dataOffset;
        name = new StringProperty(data, off);
        off += name.getSize();
        long type = LittleEndian.getUInt(data, off);
        if (type != Variant.VT_I4)
        {
            log.log(Level.WARNING, "Not a proper VT_I4 type.");
            throw new IllegalVariantTypeException(type, name);
        }
        off += LittleEndian.INT_SIZE;
        //this is a horrible workaround, around the bug in HPSF, that returns
        //cutoff byte arrays from Section.getProperty() (HPFS Bug #52337)
        //It hopes that there aren't too many parts per heading (i.e. worst
        //case it can be store in one byte...)
        int left = data.length - off;
        if (left >= LittleEndian.INT_SIZE)
        {
            partsCount = (int) LittleEndian.getUInt(data, off);
            off += LittleEndian.INT_SIZE;
        } else if (left >= LittleEndian.SHORT_SIZE)
        {
            partsCount = LittleEndian.getShort(data, off);
            off += left;
        } else if (left >= LittleEndian.BYTE_SIZE)
        {
            partsCount = LittleEndian.getUByte(data, off);
            off += left;
        } else
        {
            partsCount = 1; //default... maybe not a good idea.
        }
        size = off - dataOffset;

        this.docPartsOffset = docPartsOffset;
    }

    /**
     *
     * @return the header string.
     */
    String getName()
    {
        return name.getValue();
    }

    /**
     *
     * @return the offset into the DOCPARTS vector associated with this structure.
     */
    int getOffset()
    {
        return docPartsOffset;
    }

    /**
     *
     * @return the number of document parts associated with this header string.
     */
    int getPartsCount()
    {
        return partsCount;
    }

    /**
     *
     * @return the size of the structure (i.e. the number of bytes read from the
     * data array)
     */
    int getSize()
    {
        return size;

    }
}

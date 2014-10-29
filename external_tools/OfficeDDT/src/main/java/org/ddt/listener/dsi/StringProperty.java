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
import org.apache.poi.hpsf.IllegalVariantTypeException;
import org.apache.poi.hpsf.Variant;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/**
 * Represents a VtUnalignedString [MS-OSHARED s. 2.3.3.1.12], which contains
 * either a VT_LPSTR (0x001E) or a VT_LPWSTR (0x001F).
 *
 * It can also represent a Lpwstr or a UnalignedLpstr, depending on which
 * constructor is used...
 * maybe I should make this just represent the 'real' types, not the virtual ones...
 *
 * \todo fix the structure, it's a bit messy. there needs to be distinction
 * between the VtUS and Lp*str structures.
 *
 * \todo make it respect the codepage. this should be somewhere in the array...
 *
 * This consists of a type field (2 bytes), padding (2 bytes),
 * a length field (4 bytes) and a string of that length.
 * <pre>
 * ---------------------------------------------|
 * |   type  |  padding  |  length  |   value   |
 * |---------|-----------|----------|-----------|
 * | 2 bytes | 2 bytes   | 4 bytes  |  variable |
 * ----------------------------------------------
 * </pre>
 * <p/>
 * @author Niklas Rehfeld
 */
class StringProperty
{

    /** the type of string */
    private long type;
    /** the length of the string in <b>bytes</b> including terminating null character */
    private long charCount;
    /** the number of characters in the string, including termnating null character */
    private long length;
    /** the String, null terminated */
    private String value;
    /** the number of bytes of padding at the end. Only really relevant for
     * VT_LPWSTR, as VT_LPSTR is unaligned. */
    private int paddingBytes;

    /**
     * Constructor for making a StringProperty out of a VtUnalignedString.
     *
     * @param data   byte array to read data from
     * @param offset offset into the data array to start at
     * @throws IllegalVariantTypeException  if something in the stream is bung.
     * @throws UnsupportedEncodingException
     */
    StringProperty(byte[] data, int offset) throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        type = LittleEndian.getUShort(data, offset);
        offset += LittleEndian.INT_SIZE; //skip the two bytes padding as well
        read(data, offset);
    }

    /**
     * Constructor for making a StringProperty from a type value and a byte array.
     * Used when you are inside a <b>VtVecUnalignedLpstr</b> or
     * <b>VtVecLpwstr</b>. Possibly other places.
     *
     * @param data   data stream to get the string from
     * @param offset offset into the data stream to start reading from
     * @param type   the type of string. Must be either VT_LPWSTR or VT_LPSTR.
     * @throws IllegalVariantTypeException
* throws
     * UnsupportedEncodingException
     */
    StringProperty(byte[] data, int offset, long type) throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        this.type = type;
        read(data, offset);
    }

    /**
     * reads the data from the byte array.
     *
     * @param data   byte array to read from
     * @param offset offset into the data array
     * @throws IllegalVariantTypeException
     */
    private void read(final byte data[], final int offset) throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        int o = offset;
        charCount = LittleEndian.getUInt(data, o);
        length = charCount;
        o += LittleEndian.INT_SIZE;
        if (type == Variant.VT_LPWSTR)
        {
            //the smallest number of bytes to pad it to a multiple of 4... there must be a nicer way
            paddingBytes = (int) (4 - (length % 4)) % 4;
        } else if (type == Variant.VT_LPSTR)
        {
            paddingBytes = 0;
        } else
        {
            throw new IllegalVariantTypeException(type, value,
                    "At offset " + o + ": Not a string, type = " + Long.toHexString(type)
                    + " should be " + Integer.toHexString(Variant.VT_LPSTR) + " or "
                    + Integer.toHexString(Variant.VT_LPWSTR));
        }

        length = Math.min(length, data.length - o);
        if (type == Variant.VT_LPWSTR)
        {
//            value = new String(LittleEndian.getByteArray(data, o,
//                    (int) (length - 2)), "UTF-16LE");
            value = StringUtil.getFromUnicodeLE(data, o, (int) (length - 1));
        } else
        {
//            value = new String(LittleEndian.getByteArray(data, o, (int) (length - 1)));
            value = StringUtil.getFromCompressedUnicode(data, o, (int) (length - 1));
        }
    }

    /**
     * @todo make this function return different things, depending on whether it's
     * representing a Lpwstr/UnalignedLpstr or a VtUnalignedString structure.
     * (VtUnaligneString contains a TYPE field, the others don't.)
     * <p/>
     * @return the size in bytes of the structure.
     */
    int getSize()
    {
        int sz = (LittleEndian.INT_SIZE //type field
                + LittleEndian.INT_SIZE //char count field
                + paddingBytes);            //padding
        sz += (int) ((type == Variant.VT_LPWSTR) ? length * 2 : length); //string length
        return sz;
    }

    /**
     *
     * @return the string that this represents, not including terminating null char
     */
    String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    /**
     *
     * @return the type of string. can be either {@link org.poi.apache.hpsf.Variant#VT_LPSTRING}
     * of {@link org.poi.apache.hpsf.Variant#VT_LPWSTRING}
     */
    int getType()
    {
        return (int) type;
    }
}

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

/**
 * Represents a Vector of String properties.
 * <p/>
 * This class actually represents two slightly different structures, depending on which constructor
 * is used. If the {@link #StringVector(byte[], int)} read constructor is used, it will read either
 * a
 * <code>VtVecUnalignedLpstr</code> [MS-OSHARED 2.3.3.1.10]or a
 * <code>VtVecLpwstr</code> [MS-OSHARED 2.3.3.1.8]structure.
 * <p/>
 * If the {@link #StringVector(byte[], int, long)} read constructor is used, it will read a
 * <code>VtVecUnalignedLpstrValue</code>  [MS-OSHARED 2.3.3.1.9] or a
 * <code>VtVecLpwstrValue</code> [MS-OSHARED 2.3.3.1.7] structure.
 *
 * <pre>
 * (only if StringVector(byte[],int) constructor is used)
 *  ______|_______
 * /              \
 * ---------------------------------------------
 * |   type        |  numElements  |  Strings  |
 * |---------------|---------------|-----------|
 * |   4 bytes     |   4 bytes     | variable  |
 * ---------------------------------------------
 *                      |
 *              ------------------
 *              | StringProperty |
 *              ------------------
 * </pre>
 *
 * \todo lots of error checking.
 *
 * @author Niklas Rehfeld
 */
class StringVector
{
    /**the type of strings */
    private long type;
    /** the number of strings */
    private int numElements;
    /** the actual string structures */
    private StringProperty[] elements;

    /**
     * Constructor for reading from a byte array containing a VtVecLpwstr or
     * VtVecUnalignedLpstr structure.
     *
     * @param data   byte array with data in it.
     * @param offset offset in data array, to the start of a VtVect*str
     * @throws IllegalVariantTypeException
     * @throws UnsupportedEncodingException 
     */
    StringVector(final byte[] data, int offset) throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        type = LittleEndian.getUShort(data, offset);
        offset += LittleEndian.INT_SIZE; //skip padding as well.
        read(data, offset);
    }

    /**
     * Constructor for reading from a byte array containing a VtVecLpwstrValue or
     * VtVecUnalignedLpstrValue structure. These have had the 'type' field already read.
     *
     * @param data   a byte array containing the String Vector to read.
     * @param offset the offset into the
     * <code> data</code> array, must be the
     * start of a VtVec*strValue structure, i.e. just after the
     * <code>type</code>
     * field.
     * @param type   the type of the string vector. Must be either
     * <code>VT_VECTOR | VT_LPSTR</code> <b>(0x101E)</b> or
     * <code>VT_VECTOR | VT_LPWSTR</code> <b>(0x101F)</b>.
     * @throws IllegalVariantTypeException if the type parameter is not valid.
     * @throws UnsupportedEncodingException
     */
    StringVector(final byte[] data, int offset, long type) throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        this.type = type;
        read(data, offset);
    }

    /**
     * reads the data array and fills in the StringVector values.
     * \warning the
     * <code>type</code> field <b>must</b> be populated/initialised
     * before this method is called.
     *
     * @param data   byte array containing a vector of strings.
     * @param offset the offset into the array at which the vector starts.
     * @throws IllegalVariantTypeException
     * @throws UnsupportedOperationException
     */
    private void read(byte[] data, int offset) throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        if ((type != (Variant.VT_VECTOR | Variant.VT_LPSTR))
                && (type != (Variant.VT_VECTOR | Variant.VT_LPWSTR)))
        {
            throw new IllegalVariantTypeException(type, data);
        }

        numElements = (int) LittleEndian.getUInt(data, offset);
        offset += LittleEndian.INT_SIZE;
        elements = new StringProperty[numElements];
        for (int i = 0; i < numElements; i++)
        {
            elements[i] = new StringProperty(data, offset, type & 0x00ff);
            //because the strings are constructed with an already-read type field,
            //we only need to advance over the StringLength field and the string,
            //not the type field (there isn't one in this case).
            offset += elements[i].getSize() - LittleEndian.INT_SIZE;
        }
    }

    /**
     *
     * @param index gets the value of the property at the given index of the vector.
     * @return value of the property at the index.
     */
    StringProperty get(int index)
    {
        if (index >= elements.length)
            throw new IndexOutOfBoundsException("Index " + index
                    + " is larger than the number of elements in this vector ("
                    + elements.length + ")");
        return elements[index];
    }

    /**
     *
     * @return the array of properties in this vector.
     */
    StringProperty[] getAll()
    {
        return elements;
    }
}

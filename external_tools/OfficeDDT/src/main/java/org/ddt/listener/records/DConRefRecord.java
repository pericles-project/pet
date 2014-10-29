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
 * limitations under the License.
 */
package org.ddt.listener.records;

import java.util.Arrays;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianOutput;

/**
 * DConRef records specify a range in a workbook (internal or external) that serves as a data source
 * for pivot tables or data consolidation.
 *
 * Represents a
 * <code>DConRef</code> Structure
 * <a href="http://msdn.microsoft.com/en-us/library/dd923854(office.12).aspx">[MS-XLS s.
 * 2.4.86]</a>, and the contained
 * <code>DConFile</code> structure
 * <a href="http://msdn.microsoft.com/en-us/library/dd950157(office.12).aspx">
 * [MS-XLS s. 2.5.69]</a>. This in turn contains a
 * <code>XLUnicodeStringNoCch</code>
 * <a href="http://msdn.microsoft.com/en-us/library/dd910585(office.12).aspx">
 * [MS-XLS s. 2.5.296]</a>.
 *
 * <pre>
 *         _______________________________
 *        |          DConRef              |
 *(bytes) +-+-+-+-+-+-+-+-+-+-+...+-+-+-+-+
 *        |    ref    |cch|  stFile   | un|
 *        +-+-+-+-+-+-+-+-+-+-+...+-+-+-+-+
 *                              |
 *                     _________|_____________________
 *                    |DConFile / XLUnicodeStringNoCch|
 *                    +-+-+-+-+-+-+-+-+-+-+-+...+-+-+-+
 *             (bits) |h|   reserved  |      rgb      |
 *                    +-+-+-+-+-+-+-+-+-+-+-+...+-+-+-+
 * </pre>
 * Where
 * <ul>
 * <li><code>DConFile.h = 0x00</code> if the characters in<code>rgb</code> are single byte, and
 * <code>DConFile.h = 0x01</code> if they are double byte. <br/>
 * If they are double byte, then<br/>
 * <ul>
 * <li> If it exists, the length of
 * <code>DConRef.un = 2</code>. Otherwise it is 1.
 * <li> The length of
 * <code>DConFile.rgb = (2 * DConRef.cch)</code>. Otherwise it is equal to
 * <code>DConRef.cch</code>.
 * </ul>
 * <li><code>DConRef.rgb</code> starts with
 * <code>0x01</code> if it is an external reference, and with
 * <code>0x02</code> if it is a self-reference.
 * </ul>
 *
 * @todo edit/write path support.
 *
 * @author Niklas Rehfeld
 */
public class DConRefRecord extends StandardRecord
{

    /**
     * The id of the record type,
     * <code>sid = {@value}</code>
     */
    public static final short sid = 0x0051;
    /**
     * A RefU structure specifying the range of cells if this record is part of an SXTBL.
     * <a href="http://msdn.microsoft.com/en-us/library/dd920420(office.12).aspx">
     * [MS XLS s.2.5.211]</a>
     */
    private int firstRow, lastRow, firstCol, lastCol;
    /**
     * the number of chars in the link
     */
    private int charCount;
    /**
     * the type of characters (single or double byte)
     */
    private int charType;
    /**
     * The link's path string. This is the
     * <code>rgb</code> field of a
     * <code>XLUnicodeStringNoCch</code>. Therefore it will contain at least one leading special
     * character (0x01 or 0x02) and probably other ones.
     * <p/>
     * @see
     * <A href="http://msdn.microsoft.com/en-us/library/dd923491(office.12).aspx">
     * DConFile [MS-XLS s. 2.5.77]</A> and
     * <A href="http://msdn.microsoft.com/en-us/library/dd950157(office.12).aspx">
     * VirtualPath [MS-XLS s. 2.5.69]</a>
     * <p/>
     */
    private byte[] path;
    /**
     * unused bits at the end, must be set to 0.
     */
    private byte[] _unused;

    /**
     * Read constructor.
     *
     * @param data byte array containing a DConRef Record, including the header.
     */
    public DConRefRecord(byte[] data)
    {
        int offset = 0;
        if (!(LittleEndian.getShort(data, offset) == DConRefRecord.sid))
            throw new RecordFormatException("incompatible sid.");
        offset += LittleEndian.SHORT_SIZE;

        //length = LittleEndian.getShort(data, offset);
        offset += LittleEndian.SHORT_SIZE;

        firstRow = LittleEndian.getUShort(data, offset);
        offset += LittleEndian.SHORT_SIZE;
        lastRow = LittleEndian.getUShort(data, offset);
        offset += LittleEndian.SHORT_SIZE;
        firstCol = LittleEndian.getUByte(data, offset);
        offset += LittleEndian.BYTE_SIZE;
        lastCol = LittleEndian.getUByte(data, offset);
        offset += LittleEndian.BYTE_SIZE;
        charCount = LittleEndian.getUShort(data, offset);
        offset += LittleEndian.SHORT_SIZE;
        if (charCount < 2)
            throw new org.apache.poi.hssf.record.RecordFormatException(
                    "Character count must be >= 2");

        charType = LittleEndian.getUByte(data, offset);
        offset += LittleEndian.BYTE_SIZE; //7 bits reserved + 1 bit type

        /*
         * bytelength is the length of the string in bytes, which depends on whether the string is
         * made of single- or double-byte chars. This is given by charType, which equals 0 if
         * single-byte, 1 if double-byte.
         */
        int byteLength = charCount * ((charType & 1) + 1);

        path = LittleEndian.getByteArray(data, offset, byteLength);
        offset += byteLength;

        /*
         * If it's a self reference, the last one or two bytes (depending on char type) are the
         * unused field. Not sure If i need to bother with this...
         */
        if (path[0] == 0x02)
            _unused = LittleEndian.getByteArray(data, offset, (charType + 1));

    }

    /**
     * Read Constructor.
     *
     * @param inStream RecordInputStream containing a DConRefRecord structure.
     */
    public DConRefRecord(RecordInputStream inStream)
    {
        if (inStream.getSid() != sid)
            throw new RecordFormatException("Wrong sid: " + inStream.getSid());

        firstRow = inStream.readUShort();
        lastRow = inStream.readUShort();
        firstCol = inStream.readUByte();
        lastCol = inStream.readUByte();
        charCount = inStream.readUShort();
        charType = inStream.readUByte();
        // ^ might need to check that the first bit only is set?
        // byteLength depends on whether we are using single- or double-byte chars.
        int byteLength = charCount * (charType + 1);

        path = new byte[byteLength];
        inStream.readFully(path);

        

        if (path[0] == 0x02)
            _unused = inStream.readRemainder();

    }

    /**
     * assuming this wants the number of bytes returned by {@link serialize(LittleEndianOutput)},
     * that is,
     * <code>length - 4</code>
     *
     * @return the size of the data portion of the record.
     */
    @Override
    protected int getDataSize()
    {
        // sz = ref + charCOunt + fHighByte (charType) + path + ?unused
        int sz = 9 + path.length;
        if (path[0] == 0x02)
            sz += _unused.length;
        return sz;
    }

    @Override
    protected void serialize(LittleEndianOutput out)
    {
        out.writeShort(firstRow);
        out.writeShort(lastRow);
        out.writeByte(firstCol);
        out.writeByte(lastCol);
        out.writeShort(charCount);
        out.writeByte(charType);
        out.write(path);
        if (path[0] == 0x02)
            out.write(_unused);
    }

    @Override
    public short getSid()
    {
        return sid;
    }

    /**
     * @return The first column of the range.
     */
    public int getFirstColumn()
    {
        return firstCol;
    }

    /**
     * @param firstCol The first column of the range. Must be &lt;= last column.
     */
    public void setFirstColumn(int firstCol)
    {
        if (this.lastCol < firstCol)
            throw new IndexOutOfBoundsException(
                    "Last column must be >= first column");
        this.firstCol = firstCol;
    }

    /**
     * @return The first row of the range.
     */
    public int getFirstRow()
    {
        return firstRow;
    }

    /**
     * @param firstRow The first row of the range. Must be &lt;= last column.
     */
    public void setFirstRow(int firstRow)
    {
        if (firstRow > this.lastRow)
            throw new IndexOutOfBoundsException("First row must be <= last row");
        this.firstRow = firstRow;
    }

    /**
     * @return The last column of the range.
     */
    public int getLastColumn()
    {
        return lastCol;
    }

    /**
     * @param lastCol The last column of the range. Must be &gt;= last column.
     */
    public void setLastColumn(int lastCol)
    {
        if (lastCol < this.firstCol)
            throw new IndexOutOfBoundsException(
                    "Last column must be >= first column");
        this.lastCol = lastCol;
    }

    /**
     * @return The last row of the range.
     */
    public int getLastRow()
    {
        return lastRow;
    }

    /**
     * @param lastRow The last row of the range. Must be &gt;= the first row.
     */
    public void setLastRow(int lastRow)
    {
        if (firstRow > lastRow)
            throw new IndexOutOfBoundsException("First row must be <= last row");
        this.lastRow = lastRow;
    }

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("DConRef Record (SID = ").append(sid).append(")\n");
        b.append("[Size: ").append(charCount).append("\n");
        b.append("Path: ").append(getPath()).append("\n");
        b.append("Type: ").append(path[0]).append("]\n");
        return b.toString();
    }

    /**
     *
     * @return raw path byte array.
     */
    public byte[] getPath()
    {
	//return Arrays.copyOf(path, path.length); //java6 only.
	return path;
    }

    /**
     * @return the link's path, with the special characters stripped/replaced. May be null.
     * @see MS-XLS 2.5.277 (VirtualPath)
     */
    public String getReadablePath()
    {
        if (path != null)
        {
            //all of the path strings start with either 0x02 or 0x01 followed by zero or
            //more of 0x01..0x08
            int offset = 1;
            while (path[offset] < 0x20 && offset < path.length)
            {
                offset++;
            }
	    //Arrays.copyOf() is java6 only. 
            // String out = new String(Arrays.copyOfRange(path, offset, path.length)); 
	    String out = new String(path, offset, path.length - offset);
            //UNC paths have \u0003 chars as path separators.
            out = out.replaceAll("\u0003", "/");
            return out;
        }
        return null;
    }

    /**
     * Checks if the data source in this reference record is external to this sheet or internal.
     * <p/>
     * @return true iff this is an external reference.
     */
    public boolean isExternalRef()
    {
        if (path[0] == 0x01)
            return true;
        return false;
    }
}

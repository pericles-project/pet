/*
 * Copyright 2012 Niklas Rehfeld .
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
package org.ddt;

import java.util.Arrays;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/**
 *
 *
 * @author Niklas Rehfeld
 */
public class ByteArrayUtils
{

    public static byte[] addString(byte[] dest, String s, boolean unicode)
    {
        int stringLength = unicode ? 2 * s.length() : s.length();

        byte[] result = Arrays.copyOf(dest, dest.length + stringLength);
        if (unicode)
            StringUtil.putUnicodeLE(s, result, dest.length);
        else
            StringUtil.putCompressedUnicode(s, result, dest.length);

        return result;
    }

    public static byte[] addInt(byte[] dest, int i)
    {
        byte[] result = Arrays.copyOf(dest, dest.length + 4);
        LittleEndian.putInt(result, dest.length, i);
        return result;
    }

    public static byte[] addShort(byte[] dest, short s)
    {
        byte[] result = Arrays.copyOf(dest, dest.length + 2);
        LittleEndian.putShort(result, dest.length, s);
        return result;
    }

    public static byte[] addLong(byte[] dest, long l)
    {
        byte[] result = Arrays.copyOf(dest, dest.length + LittleEndian.LONG_SIZE);
        LittleEndian.putLong(result, dest.length, l);
        return result;
    }

    public static byte[] addBytes(byte[] dest, byte[] bytes)
    {
        byte[] result = Arrays.copyOf(dest, dest.length + bytes.length);
        for (int i = 0; i < bytes.length; i++)
        {
            result[i + dest.length] = bytes[i];
        }
        return result;
    }
//    public static byte[] addGUID(byte[] dest, int p1, short p2, short p3, )
}

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
package org.ddt.listener.dsi;

import org.ddt.listener.dsi.StringProperty;
import java.io.UnsupportedEncodingException;
import org.apache.poi.hpsf.IllegalVariantTypeException;
import org.apache.poi.hpsf.Variant;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Unit tests for StringProperty class.
 * <p/>
 * @author Niklas Rehfeld
 */
public class StringPropertyTest
{

    /**
     * a string of single-byte (ansi) characters.
     */
    final byte[] ansiString = new byte[]
    {
        (byte) Variant.VT_LPSTR, 0, 0, 0,//type = ansi
        12, 0, 0, 0, //cch
        'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o',
        'r', 'l', 'd', 0
    };
    /**
     * a string of double-byte (unicode UTF-16LE) characters.
     */
    final byte[] unicodeString = new byte[]
    {
        (byte) Variant.VT_LPWSTR, 0, 0, 0, //type = unicode
        12, 0, 0, 0,//cch
        (byte) 'h', 0, (byte) 'e', 0, (byte) 'l', 0, (byte) 'l', 0,
        (byte) 'o', 0, (byte) ' ', 0, (byte) 'w', 0, (byte) 'o', 0,
        (byte) 'r', 0, (byte) 'l', 0, (byte) 'd', 0, 0, 0
    };
    StringProperty ansiInstance, unicodeInstance;

    @Before
    public void setUp() throws IllegalVariantTypeException, UnsupportedEncodingException
    {
        ansiInstance = new StringProperty(ansiString, 0);
        unicodeInstance = new StringProperty(unicodeString, 0);
    }

    /**
     * Test of getSize method, of class StringProperty.
     */
    @Test
    public void testGetSize() throws IllegalVariantTypeException, UnsupportedEncodingException
    {
        assertEquals("single-byte string struct size", ansiString.length, ansiInstance.getSize());
        assertEquals("double-byte string struct size", unicodeString.length,
                unicodeInstance.getSize());
    }

    /**
     * Test of getValue method, of class StringProperty.
     */
    @Test
    public void testGetValue() throws IllegalVariantTypeException, UnsupportedEncodingException
    {
        String expResult = "hello world";
        assertEquals("single-byte string value", expResult, ansiInstance.getValue());
        assertEquals("double-byte string value", expResult, unicodeInstance.getValue());

    }

    @Test
    public void testGetType() throws IllegalVariantTypeException, UnsupportedEncodingException
    {
        assertEquals("single-byte string type", Variant.VT_LPSTR, ansiInstance.getType());
        assertEquals("double-byte string type", Variant.VT_LPWSTR, unicodeInstance.getType());

    }
}

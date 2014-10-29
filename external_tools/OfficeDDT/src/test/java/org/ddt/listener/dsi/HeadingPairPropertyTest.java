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

import org.ddt.listener.dsi.HeadingPairProperty;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.apache.poi.hpsf.IllegalVariantTypeException;
import org.apache.poi.hpsf.Variant;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Niklas Rehfeld
 */
public class HeadingPairPropertyTest
{

    /** tests should pass with this. name = 'hello world', headingparts = 3 */
    byte[] validAnsi = new byte[]
    {
        (byte) Variant.VT_LPSTR, 0, 0, 0,//type = ansi
        12, 0, 0, 0, //cch
        'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd', 0, //header name
        3, 0, 0, 0, //type = I4
        3, 0, 0, 0 //value = 1
    };
    /** tests should fail, throwing a IllegalVariantTypeException.. */
    byte[] invalidAnsi = new byte[]
    {
        (byte) Variant.VT_LPSTR, 0, 0, 0,//type = ansi
        12, 0, 0, 0, //cch
        'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd', 0,
        4, 0, 0, 0, //type = R4 = wrong.
        2, 0, 0, 0
    };
    /** tests should pass with this. name = 'hello world', value = 4 */
    byte[] validUni = new byte[]
    {
        (byte) Variant.VT_LPWSTR, 0, 0, 0, //type = unicode
        12, 0, 0, 0,//cch
        (byte) 'h', 0, (byte) 'e', 0, (byte) 'l', 0, (byte) 'l', 0, (byte) 'o', 0, (byte) ' ', 0,
        (byte) 'w', 0, (byte) 'o', 0, (byte) 'r', 0, (byte) 'l', 0, (byte) 'd', 0, 0, 0,
        3, 0, 0, 0, //type = I4
        4, 0, 0, 0 //value = 4
    };
    /** tests should fail, throwing a IllegalVariantTypeException.. */
    byte[] invalidUni = new byte[]
    {
        (byte) Variant.VT_LPWSTR, 0, 0, 0, //type = unicode
        12, 0, 0, 0,//cch
        (byte) 'h', 0, (byte) 'e', 0, (byte) 'l', 0, (byte) 'l', 0, (byte) 'o', 0, (byte) ' ', 0,
        (byte) 'w', 0, (byte) 'o', 0, (byte) 'r', 0, (byte) 'l', 0, (byte) 'd', 0, 0, 0,
        7, 0, 0, 0, //type = DATE = wrong.
        2, 0, 0, 0
    };
    private HeadingPairProperty ansiProperty;
    private HeadingPairProperty unicodeProperty;

    public HeadingPairPropertyTest()
    {
    }

    @Before
    public void setUp() throws IllegalVariantTypeException, UnsupportedEncodingException
    {
        ansiProperty = new HeadingPairProperty(validAnsi, 0, 0);
        unicodeProperty = new HeadingPairProperty(validUni, 0, 0);
    }

    @Test
    public void testConstructorFailures() throws UnsupportedEncodingException
    {
        //these should throw exceptions.
        HeadingPairProperty instance;
        try
        {
            instance = new HeadingPairProperty(invalidAnsi, 0, 3);
            fail("Didn't throw an IllegalVariantException.");
        } catch (IllegalVariantTypeException ex)
        {
        }
        try
        {
            instance = new HeadingPairProperty(invalidUni, 0, 2);
            fail("Didn't throw an IllegalVariantException.");
        } catch (IllegalVariantTypeException ex)
        {
        }

    }

    /**
     * This test tests the workaround for HPFS Bug #52337, which returns truncated byte arrays.
     * it should be removed once the bug is fixed in HPFS.
     */
    @Test
    public void testWorkaround() throws IllegalVariantTypeException, UnsupportedEncodingException
    {
        System.out.println("HPFS Bug#52337 workaround");
        byte[] b;
        for (int i = 0; i < 4; i++)
        {
            b = Arrays.copyOfRange(validAnsi, 0, validAnsi.length - i);
            HeadingPairProperty p = new HeadingPairProperty(b, 0, 0);
            assertEquals(3, p.getPartsCount());
            assertEquals("hello world", p.getName());
        }

        b = Arrays.copyOfRange(validAnsi, 0, validAnsi.length - 4);
        HeadingPairProperty p = new HeadingPairProperty(b, 0, 0);
        assertEquals(1, p.getPartsCount());
        assertEquals("hello world", p.getName());

        for (int i = 0; i < 4; i++)
        {
            b = Arrays.copyOfRange(validUni, 0, validUni.length - i);
            p = new HeadingPairProperty(b, 0, 0);
            assertEquals(4, p.getPartsCount());
            assertEquals("hello world", p.getName());
        }

        b = Arrays.copyOfRange(validAnsi, 0, validAnsi.length - 4);
        p = new HeadingPairProperty(b, 0, 0);
        assertEquals(1, p.getPartsCount());
        assertEquals("hello world", p.getName());

    }

    /**
     * Test of getName method, of class HeadingPairProperty.
     */
    @Test
    public void testGetName()
    {
        String expResult = "hello world";

        assertEquals("ansi, name ", expResult, ansiProperty.getName());
        assertEquals("unicode, name", expResult, unicodeProperty.getName());
    }

    /**
     * Test of getPartsCount method, of class HeadingPairProperty.
     */
    @Test
    public void testGetPartsCount()
    {
        //ansi = 3, uni = 4
        int expResult = 3;
        assertEquals("ansi, parts count", expResult, ansiProperty.getPartsCount());
        expResult = 4;
        assertEquals("unicode, parts count", expResult, unicodeProperty.getPartsCount());
    }

    /**
     * Test of getSize method, of class HeadingPairProperty.
     */
    @Test
    public void testGetSize()
    {
        assertEquals("ansi, size", validAnsi.length, ansiProperty.getSize());
        assertEquals("unicode, size", validUni.length, unicodeProperty.getSize());
    }
}

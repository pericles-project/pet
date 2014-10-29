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
import org.ddt.listener.dsi.HeadingPairVector;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.hpsf.Variant;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for HeadingPairVector class.
 *
 * @author Niklas Rehfeld
 */
public class HeadingPairVectorTest
{

    /** tests should pass with this. 2 pairs, first one is "hello world", in ANSI, with 3 parts,
     * second one is "seeya world" in Unicode, with 4 parts. */
    final byte[] validVector = new byte[]
    {
        4, 0, 0, 0,//count --> 2 pairs
        (byte) Variant.VT_LPSTR, 0, 0, 0,//type = ansi
        12, 0, 0, 0, //cch
        'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd', 0, //header name
        3, 0, 0, 0, //type = I4
        3, 0, 0, 0, //value = 3
        // -- pair 2, unicode string
        (byte) Variant.VT_LPWSTR, 0, 0, 0, //type = unicode
        12, 0, 0, 0,//cch
        (byte) 's', 0, (byte) 'e', 0, (byte) 'e', 0, (byte) 'y', 0, (byte) 'a', 0, (byte) ' ', 0,
        (byte) 'w', 0, (byte) 'o', 0, (byte) 'r', 0, (byte) 'l', 0, (byte) 'd', 0, 0, 0,
        3, 0, 0, 0, //type = I4
        4, 0, 0, 0 //value = 4
    };

    private HeadingPairVector vector;

    @Before
    public void setUp() throws UnsupportedEncodingException, HPSFException
    {
        vector = new HeadingPairVector(validVector, 0);
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of getHeadingPairByName method, of class HeadingPairVector.
     */
    @Test
    public void testGetHeadingPairByName()
    {
        //haven't overridden equals(), so have to test that they are the same manually.
        HeadingPairProperty hello = vector.getHeadingPairByName("hello world");
        HeadingPairProperty seeya = vector.getHeadingPairByName("seeya world");

        assertEquals("first item name", hello.getName(), "hello world");
        assertEquals("first item offset", hello.getOffset(), 0);
        assertEquals("first item part count", hello.getPartsCount(), 3);

        assertEquals("second item name", seeya.getName(), "seeya world");
        assertEquals("second item offset", seeya.getOffset(), 3);
        assertEquals("second item parts count", seeya.getPartsCount(), 4);

        assertNull("non-existant item", vector.getHeadingPairByName("not here"));
    }

    /**
     * Test of getDocpartOffsetByName method, of class HeadingPairVector.
     */
    @Test
    public void testGetDocpartOffsetByName()
    {
        assertEquals(vector.getDocpartOffsetByName("hello world"), 0);
        assertEquals(vector.getDocpartOffsetByName("seeya world"), 3);
        assertEquals("non-existant item", vector.getDocpartOffsetByName("not here"), -1);
    }

    @Test
    public void failIfCountNotEven() throws UnsupportedEncodingException
    {
        byte[] invalid = Arrays.copyOf(validVector, validVector.length);
        invalid[0] = 5;
        try
        {
            HeadingPairVector v = new HeadingPairVector(invalid, 0);
        } catch (HPSFException ex)
        {
            //don't know if it's good to make this test so specific, as I will probably want to
            //change the exception...
            if (ex.getMessage().equals("The count of a HeadingPairVector must be even."))
                return;
            fail("threw a strange exception" + ex.getLocalizedMessage());
        }
        fail("didn't throw an HPSFException. ");
    }
    
}

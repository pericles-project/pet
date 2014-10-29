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

import org.ddt.listener.dsi.StringVector;
import org.ddt.listener.dsi.StringProperty;
import java.io.UnsupportedEncodingException;
import org.apache.poi.hpsf.IllegalVariantTypeException;
import org.apache.poi.hpsf.Variant;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Niklas Rehfeld
 */
public class StringVectorTest
{

    byte[] ansiVector = new byte[]
    {
        (byte) 0x1E, (byte) 0x10, 0, 0, //type = ANSI
        2, 0, 0, 0, //string count
        12, 0, 0, 0, //cch
        'h', 'e', 'l', 'l',
        'o', ' ', 'w', 'o',
        'r', 'l', 'd', 0,
        17, 0, 0, 0, //cch
        'h', 'e', 'l', 'l',
        'o', ' ', 'w', 'o',
        'r', 'l', 'd', ',',
        ' ', 'b', 'l', 'a',
        0 //no padding needed.
    };
    byte[] unicodeVector = new byte[]
    {
        (byte) 0x1F, (byte) 0x10, 0, 0,//type = UNICODE
        2, 0, 0, 0, //string count
        12, 0, 0, 0,//cch
        (byte) 'h', 0, (byte) 'e', 0,
        (byte) 'l', 0, (byte) 'l', 0,
        (byte) 'o', 0, (byte) ' ', 0,
        (byte) 'w', 0, (byte) 'o', 0,
        (byte) 'r', 0, (byte) 'l', 0,
        (byte) 'd', 0, 0, 0,
        5, 0, 0, 0,//cch
        (byte) 's', 0, (byte) 'o', 0,
        (byte) 'u', 0, (byte) 'p', 0,
        0, 0, 0, 0 // terminating \0 + padding
    };
    StringVector ansiTypeRead;
    StringVector ansiTypeGiven;
    StringVector unicodeTypeRead;
    StringVector unicodeTypeGiven;

    public StringVectorTest()
    {
    }

    @Before
    public void setUp() throws IllegalVariantTypeException, UnsupportedEncodingException
    {
        ansiTypeGiven = new StringVector(ansiVector, 4, Variant.VT_VECTOR | Variant.VT_LPSTR);
        unicodeTypeGiven = new StringVector(unicodeVector, 4, Variant.VT_VECTOR | Variant.VT_LPWSTR);
        ansiTypeRead = new StringVector(ansiVector, 0);
        unicodeTypeRead = new StringVector(unicodeVector, 0);
    }

    @Test
    public void testVectorConstructors() throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        ansiTypeRead = new StringVector(ansiVector, 0);
        unicodeTypeRead = new StringVector(unicodeVector, 0);
    }

    @Test
    public void testVectorValueConstructors() throws IllegalVariantTypeException,
            UnsupportedEncodingException
    {
        ansiTypeGiven = new StringVector(ansiVector, 4, Variant.VT_VECTOR | Variant.VT_LPSTR);
        unicodeTypeGiven = new StringVector(unicodeVector, 4, Variant.VT_VECTOR | Variant.VT_LPWSTR);
    }

    @Test
    public void testGetAnsi()
    {
        StringProperty property = ansiTypeRead.get(0);
        assertEquals("[0] ansi, type = read ", property.getType(), Variant.VT_LPSTR);
        assertEquals("[0] ansi, type = read, value ", property.getValue(), "hello world");

        property = ansiTypeGiven.get(0);
        assertEquals("[0] ansi, type = given ", property.getType(), Variant.VT_LPSTR);
        assertEquals("[0] ansi, type = given, value ", property.getValue(), "hello world");

        property = ansiTypeRead.get(1);
        assertEquals("[1] ansi, type = read ", property.getType(), Variant.VT_LPSTR);
        assertEquals("[1] ansi, type = read, value ", property.getValue(), "hello world, bla");

        property = ansiTypeGiven.get(1);
        assertEquals("[1] ansi, type = given ", property.getType(), Variant.VT_LPSTR);
        assertEquals("[1] ansi, type = given, value ", property.getValue(), "hello world, bla");
    }

    @Test
    public void testGetUnicode()
    {
        StringProperty property = unicodeTypeRead.get(0);
        assertEquals("[0] unicode, type = read ", property.getType(), Variant.VT_LPWSTR);
        assertEquals("[0] unicode, type = read, value", "hello world", property.getValue());

        property = unicodeTypeGiven.get(0);
        assertEquals("[0] unicode, type = given ", property.getType(), Variant.VT_LPWSTR);
        assertEquals("[0] unicode, type = given, value ", "hello world", property.getValue());

        property = unicodeTypeGiven.get(1);
        assertEquals("[1] unicode, type = given ", property.getType(), Variant.VT_LPWSTR);
        assertEquals("[1] unicode, type = given, value ", "soup", property.getValue());

        property = unicodeTypeRead.get(1);
        assertEquals("[1] unicode, type = read, value", property.getType(), Variant.VT_LPWSTR);
        assertEquals("[1] unicode, type = read, value", "soup", property.getValue());


    }
}

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
package org.ddt.moniker;

import org.ddt.ByteArrayUtils;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Niklas Rehfeld
 */
public class MonikerFactoryTest
{

    private static byte[] validUrlMoniker ;
    private static byte[] validFileMoniker;
    private static byte[] validCompositeMoniker;

    public MonikerFactoryTest()
    {
    }
/*
    @BeforeClass
    public static void setUpClass() throws Exception
    {
        String s  = "http://bla.com/foo";
        int length = s.length() + 24;
        validUrlMoniker = new byte[0];
        validUrlMoniker = ByteArrayUtils.addInt(validUrlMoniker, length);
        validUrlMoniker = ByteArrayUtils.addString(validUrlMoniker, s, false);
        validUrlMoniker = ByteArrayUtils.addBytes(validUrlMoniker, MonikerFactory.MONIKER_TYPE_URL);
        validUrlMoniker = ByteArrayUtils.addInt(validUrlMoniker, 0);
        validUrlMoniker = ByteArrayUtils.addInt(validUrlMoniker, 0); //no flags, not really realistic.

        s = "some/path/of/sorts";
        validFileMoniker = new byte[0];
        validFileMoniker = ByteArrayUtils.addShort(validFileMoniker, (short)1);
        validFileMoniker = ByteArrayUtils.addShort(validFileMoniker, (short) s.length());
        validFileMoniker = ByteArrayUtils.addString(validFileMoniker, s, false);
        validFileMoniker = ByteArrayUtils.addShort(validFileMoniker, (short)0xffff);//not a UNC path
        validFileMoniker = ByteArrayUtils.addShort(validFileMoniker, (short)0xDEAD); //version
        validFileMoniker = ByteArrayUtils.addLong(validFileMoniker, 0); //20 bytes of reserved
        validFileMoniker = ByteArrayUtils.addLong(validFileMoniker, 0);
        validFileMoniker = ByteArrayUtils.addInt(validFileMoniker, 0);
        validFileMoniker = ByteArrayUtils.addLong(validFileMoniker, 0); //no unicode.
        
    }
*/
    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
        System.out.println("MonikerFactory Unit tests not implemented.");
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of getMoniker method, of class MonikerFactory.
     */
    @Test
    public void testGetURLMoniker() throws Exception
    {
//        Moniker m = MonikerFactory.getMoniker(MonikerFactory.URL_MONIKER_ID, new LittleEndianByteArrayInputStream(validUrlMoniker));

    }

    @Test
    public void testGetFileMoniker() throws Exception
    {
    }

    @Test
    public void testGetCompositeMoniker() throws Exception
    {
    }
}

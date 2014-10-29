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
package org.ddt.listener.ole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.ddt.Link;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Niklas Rehfeld
 */
public class OleStreamListenerTest
{

    private String testdir = "testfiles/";

    public OleStreamListenerTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    /**
     * Test of processPOIFSReaderEvent method, of class OleStreamListener.
     */
    @Test
    public void test2003XLSInsertAsObject() throws IOException
    {
        File f = new File(this.getClass().getResource("/xls/2003/insert-as-object.xls").getFile());
//        POIFSFileSystem pfs = new POIFSFileSystem(new )
        POIFSReader r = new POIFSReader();
        OleStreamListener l = new OleStreamListener();
        r.registerListener(l);
        r.read(new FileInputStream(f));

        List<String> collectedPaths = new ArrayList<String>();
        for (Link link : l)
        {
            collectedPaths.addAll(link.getPaths());
//            System.out.println(l.toString());
        }
        assertTrue("Contains 2007 link", containsFileNameString("2007-normalsource.xls",
                collectedPaths));
        assertTrue("Contains 2010 link", containsFileNameString("2010-normalsource.xls",
                collectedPaths));
        assertTrue("Contains 2003 link", containsFileNameString("2003-normalsource.xls",
                collectedPaths));

    }

    /**
     * Test of processPOIFSReaderEvent method, of class OleStreamListener.
     */
    @Test
    public void test2007XLSInsertAsObject() throws IOException
    {
    	File f = new File(this.getClass().getResource("/xls/2003/insert-as-object.xls").getFile());

    	
//        POIFSFileSystem pfs = new POIFSFileSystem(new )
        POIFSReader r = new POIFSReader();
        OleStreamListener l = new OleStreamListener();
        r.registerListener(l);
        r.read(new FileInputStream(f));

        List<String> collectedPaths = new ArrayList<String>();
        for (Link link : l)
        {
            collectedPaths.addAll(link.getPaths());
//            System.out.println(l.toString());
        }
        assertTrue("Contains 2007 link", containsFileNameString("2007-normalsource.xls",
                collectedPaths));
        assertTrue("Contains 2010 link", containsFileNameString("2010-normalsource.xls",
                collectedPaths));
        assertTrue("Contains 2003 link", containsFileNameString("2003-normalsource.xls",
                collectedPaths));

    }

    /**
     * @param expected the filename that the link should contain.
     * @param link     the link to check.
     * @return true iff one of the path names in the link end in the expected string.
     */
    private boolean containsFileNameString(String expected, List<String> link)
    {
        for (String s : link)
        {
            if (s.endsWith(expected))
            {
                System.out.println("found '" + expected + "', in " + s);
                return true;
            }
                
//            if (s.matches(".*" + expected + ".*"))
//                return true;
        }
        return false;
    }
}

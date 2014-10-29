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

import org.ddt.Link;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Link class.
 *
 * @author Niklas Rehfeld
 */
public class LinkTest extends TestCase
{

    /**
     * Test of addPath and GetPath methods, of class Link.
     */
    @Test
    public void testAddGetPath()
    {
        Link instance = new Link();
        instance.addPath(Link.PATH_TYPE_ABSOLUTE, "test/path/to/something");
        instance.addPath(Link.PATH_TYPE_RELATIVE, "another/test/path/to/something");
        instance.addPath(Link.PATH_TYPE_UNKNOWN, "test/path/to/something/else");

        List<String> paths = instance.getPaths();
        assertEquals("path size", 3, paths.size());
        assertTrue("contains first test path", paths.contains("test/path/to/something"));
        assertTrue("contains second test path", paths.contains("another/test/path/to/something"));
        assertTrue("contains third test path", paths.contains("test/path/to/something/else"));

    }

    /**
     * Test of equals method, of class Link.
     */
    @Test
    public void testEquals()
    {
        Link l1 = new Link(2);
        Link l2 = new Link(3);

        assertTrue("empty links", l1.equals(l2)); //empty links the same.
        assertEquals("equals reflexive for empty links", l1.equals(l2), l2.equals(l1)); //reflexivity
        l1.addAbsolutePath("test/path");
        assertFalse("one empty, one not", l1.equals(l2));
        assertEquals("equals reflexive for unequal links", l1.equals(l2), l2.equals(l1));
        l2.addAbsolutePath("test/path");
        assertTrue("links with same single path", l1.equals(l2));
        assertEquals("equals reflexive for equal links", l1.equals(l2), l2.equals(l1));
        //should still be equal even if one of them has an extra path.
        l1.addRelativePath("bla/test/more");
        assertTrue("both contain the same path, plus others", l1.equals(l2));
        assertEquals("equals reflexive when differing path counts", l1.equals(l2), l2.equals(l1));
        l2.addUnkownPath("another");
        assertTrue("equal as long as one path the same", l1.equals(l2));
        assertEquals("reflexive with one path the same", l1.equals(l2), l2.equals(l1));
    }

    @Test
    public void testCompareToConsistentWithEquals()
    {
        Link l1 = new Link();
        Link l2 = new Link();
        assertTrue("equals consistent with compareTo for empty links", l1.compareTo(l2) == 0);
        l1.addPath(Link.PATH_TYPE_ABSOLUTE, "hello");
        l2.addAbsolutePath("hello");
        assertTrue("equals consistent with compareTo", l1.compareTo(l2) == 0);

    }

    /**
     * Test of compareTo method, of class Link.
     */
    @Test
    public void testCompareTo()
    {
        Link o = new Link();
        Link instance = new Link();
        o.addAbsolutePath("foo");
        instance.addAbsolutePath("bar");
        int result = instance.compareTo(o);
        assertEquals("compareTo consistent with String.compareTo", "bar".compareTo("foo"), result);

    }
}

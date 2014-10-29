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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a link/dependency that has been found.
 *
 * @todo Maybe use a Map<String,String> so we can map link type/name and the actual link for
 * outputting.
 * The other thing that could be done is to make a link collection class, that could be passed to a
 * output module. This could represent the collection of links for a file.
 *
 * @todo need to clean this up, there are some kind of redundant fields etc in here...
 *
 * @todo implement some sort of ordering of the path/link types, as some tend to
 * give more reliable/detailed information than others.
 *
 * @author Niklas Rehfeld
 */
public class Link implements Comparable<Link>
{
    private static final Logger log = Logger.getLogger("org.ddt");

    /** the type of link used mainly for debugging. */
    private int type;
    /**
     * determines that the path type is a relative path.
     */
    public static final int PATH_TYPE_RELATIVE = 1;
    /**
     * determines that the path type is an absolute path.
     */
    public static final int PATH_TYPE_ABSOLUTE = 2;
    /**
     * determines that the path type is an unknown path type.
     */
    public static final int PATH_TYPE_UNKNOWN = 0;
    /**
     * The paths that make up this length. Some links have several paths, e.g.
     * relative and absolute links in Ole Stream monikers. Others just have a
     * single path.
     */
    private Map<Integer, List<String>> paths;

    /**
     * creates an empty link object.
     */
    public Link()
    {
        paths = new HashMap<Integer, List<String>>();
        this.type = 0; //unknown type...
    }

    /**
     *
     * @param type the type of link this is.
     */
    public Link(int type)
    {
        paths = new HashMap<Integer, List<String>>();
        this.type = type;
    }

    /**
     * Add a link path to this link.
     *
     * @param pathType the type of path. this can be one of
     * @param path     the path string.
     *
     * @see #PATH_TYPE_RELATIVE
     * @see #PATH_TYPE_ABSOLUTE
     * @see #PATH_TYPE_UNKNOWN
     */
    public void addPath(int pathType, String path)
    {
	if (path == null || path.equals(""))
        {
            log.log(Level.FINE, "Not adding empty path string.");
            return;
        }
        if (!paths.containsKey(pathType))
        {
            paths.put(pathType, new ArrayList<String>());
        }
        paths.get(pathType).add(path);

    }

    /**
     *
     * @return all of the paths of all types.
     */
    public List<String> getPaths()
    {
        List<String> out = new ArrayList<String>();
        for (List<String> l : paths.values())
        {
            out.addAll(l);
        }
        return out;
    }

    /**
     *
     * @return the paths of unknown type. May be empty.
     */
    public List<String> getUnknownPaths()
    {
        return paths.get(PATH_TYPE_UNKNOWN);
    }

    /**
     * @param path path of unknown type to add.
     */
    public void addUnkownPath(String path)
    {
        addPath(PATH_TYPE_UNKNOWN, path);
    }

    /**
     *
     * @return the absolute paths. May be empty.
     */
    public List<String> getAbsolutePaths()
    {
        return paths.get(PATH_TYPE_ABSOLUTE);
    }

    /**
     *
     * @param absPath The absolute path of the linked object
     */
    public void addAbsolutePath(String absPath)
    {
        addPath(PATH_TYPE_ABSOLUTE, absPath);
    }

    /**
     *
     * @return The relative path of the linked object
     */
    public List<String> getRelativePaths()
    {
        return paths.get(PATH_TYPE_RELATIVE);
    }

    /**
     *
     * @param relPath The relative path of the linked object
     */
    public void addRelativePath(String relPath)
    {
        addPath(PATH_TYPE_RELATIVE, relPath);
    }

    public String toString()
    {
        String output = "";
        List<String> tempPaths = getRelativePaths();
        if (tempPaths != null)
        {
            for (String s : tempPaths)
            {
                output += "Relative Path: " + s + "\n";
            }
        }
        tempPaths = getAbsolutePaths();
        if (tempPaths != null)
        {
            for (String s : tempPaths)
            {
                output += "Absolute Path: " + s + "\n";
            }
        }
        tempPaths = getUnknownPaths();
        if (tempPaths != null)
        {
            for (String s : tempPaths)
            {
                output += "Unkown Path: " + s + "\n";
            }
        }
        output += "Type: " + type + "\n";

        return output;
    }

    /**
     * This has been implemented so that we can avoid duplicate links. It doesn't
     * really mean that the links are equal, but that they are duplicates.
     * <p/>
     * Returns true if any one of this link's paths is equal to any of the other link's paths, e.g.
     * it will return true if one of a's absolute paths is the same as one of b's absolute paths, or
     * if one of a's relative paths is equal to one of b's absolute paths etc.
     * If both the links are empty (i.e. they have no paths) they are also considered equal. 
     * <p/>
     * The reason for this behaviour is that the terms 'relative path', 'absolute path' and 'unkown
     * path' don't really mean anything for some of the link types, and only one is set.
     * <p/>
     * <b>Note:</b> This method ignores the case of the paths. Not sure if that's a good idea, but
     * it probably is, seeing as we'll mainly be working with files created on Windows, which
     * doesn't care about case.
     * <p/>
     * @todo see if this is still the required behaviour as we now have a third type, 'unknown',
     * which should get all of the ones that are not proper relative or absolute types. It probably
     * should, because, if we find the same link by two different methods, we probably want to
     * discard one of them.
     *
     * @param o Other Link object to compare to.
     * @return true if any of the paths inside the links are equal.
     */
    public boolean equals(Object o)
    {

        if (!(o instanceof Link))
            return false;
        
        Link l = (Link) o;
	if (l.paths.isEmpty() && this.paths.isEmpty())
            return true;
        
        for (String s : getPaths())
        {
            if (l.getPaths().contains(s))
                return true;

        }
        return false;
    }

    /**
     *
     * @return the type of link.
     */
    public int getType()
    {
        return type;
    }

    /**
     * This *should* make it compatible with
     * <code>equals()</code>
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     *
     * @param o another Link to compare.
     * @return 0 if
     * <code>this.equals(o)</code>, or if one of the Links has no
     * paths. Otherwise it returns the result of comparing the first paths. i.e.
     * <code>getPaths().get(0).compareToIgnoreCase(o.getPaths().get(0))</code>
     */
    public int compareTo(Link o)
    {
        if (equals(o))
            return 0;
        if (!getPaths().isEmpty() && !o.getPaths().isEmpty())
            return getPaths().get(0).compareToIgnoreCase(o.getPaths().get(0));
        return 0;
    }
}

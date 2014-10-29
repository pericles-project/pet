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
 * limitations under the License
 *
 */
package org.ddt.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.ddt.Constants;
import org.ddt.Link;

/**
 * This is the superclass of all classes that process/search files for links.
 * <p/>
 * Classes that extend this class must implement the {@link #process(java.io.File)} method. If
 * they do not need to run for all file types, then they should set the {@link #fileTypeMask} field.
 * <p/>
 * @author Niklas Rehfeld
 */
public abstract class FileProcessor
{

    /**
     * The file type mask that determines what files it will process.
     * <p/>
     * A subclass of this should set this as the bitwise OR of all of the file types that the class
     * should be run for.
     *
     * For example, if your subclass needs to be run for .doc and .ppt documents, it should set this
     * to
     * <code> Constants.FILETYPE_WORD | Constants.FILETYPE_POWERPOINT</code>.
     * <p/>
     * The default value for this is
     * <code>0xffffffff</code>, which will accept everything.
     *
     * @see Constants#FILETYPE_EXCEL
     * @see Constants#FILETYPE_POWERPOINT
     * @see Constants#FILETYPE_WORD
     */
    int fileTypeMask = 0xffffffff;

    /**
     * Looks for links in the file and returns them as a list.
     * <p/>
     * @param f the file to process.
     * @return The links that have been found.
     * <p/>
     * @throws FileNotFoundException have a guess when.
     * @throws IOException
     */
    public abstract List<Link> process(File f) throws FileNotFoundException, IOException;

    /**
     * Checks if the given file type is processed by the FileProcessor.
     *
     * @see officelink.Constants
     *
     * @param filetype an integer that represents the file type of a file.
     * @return true if the FileProcessor works on the given file type. Formally it returns true iff
     * <code>(filetype & this.fileTypeMask) != 0</code>
     *
     */
    public final boolean acceptsFileType(int filetype)
    {
        if ((fileTypeMask & filetype) != 0)
            return true;
        return false;
    }
}

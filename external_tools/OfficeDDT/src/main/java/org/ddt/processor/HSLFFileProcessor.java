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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hslf.exceptions.OldPowerPointFormatException;
import org.apache.poi.hslf.model.Hyperlink;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.ddt.Constants;
import org.ddt.Link;

/**
 * Processor that looks through a powerpoint file and looks at each slide, and discovers any
 * hyperlinks in textruns and shapes.
 *
 * @author Niklas Rehfeld
 */
public final class HSLFFileProcessor extends FileProcessor
{
    /** Holds the links that have been collected. Needs to be cleared for each new file processed.*/
    private  List<Link> links;
    private static Logger log = Logger.getLogger("org.ddt");
    /**
     * constructor.
     */
    public HSLFFileProcessor()
    {
        links = new ArrayList<Link>();
        fileTypeMask &= Constants.FILETYPE_POWERPOINT; //ppt only
    }

    @Override
    public List<Link> process(File f) throws FileNotFoundException, IOException
    {
        links.clear();
        SlideShow ppt = null;
        FileInputStream fis = new FileInputStream(f);
        try
        {
            ppt = new SlideShow(fis);
        } catch (OldPowerPointFormatException ex)
        {
	    log.log(Level.WARNING, "Old (ancient) Powerpoint format not supported, file: {0}", f.getName());
            return null;
        } finally
        {
            fis.close();
        }


        Slide[] s = ppt.getSlides();

        for (int i = 0; i < s.length; i++)
        {
            //text
            TextRun[] tr = s[i].getTextRuns();
            for (TextRun t : tr)
            {
                Hyperlink[] textLinks = t.getHyperlinks();
                if (textLinks != null)
                {
                    for (Hyperlink hl : textLinks)
                    {
                        Link l = new Link(2);
                        l.addUnkownPath(hl.getTitle());
                        l.addUnkownPath(hl.getAddress());
                    }
                }
                else
                    log.finest("no text links.");
            }

            //shapes
            Shape[] sh = s[i].getShapes();
            for (Shape shape : sh)
            {
                Hyperlink h = shape.getHyperlink();
                if (h != null)
                {
                     Link l = new Link(2);
                        l.addUnkownPath(h.getTitle());
                        l.addUnkownPath(h.getAddress());
                }
                else
                    log.finest("no links in shape.");
            }
        }
        log.log(Level.FINE, "returning {0}", links.toString());
        return Collections.unmodifiableList(links);
    }
}

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
package org.ddt.listener.records;

import org.ddt.Link;
import org.ddt.listener.RecordEventListener;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SupBookRecord;

/**
 * This class works on SupBook records. These hold Links from formulas, charts
 * and linked files in Excel files. 
 *
 * @author Niklas Rehfeld
 */
public class SupBookListener extends RecordEventListener
{

    /**
     * Constructor.
     */
    public SupBookListener()
    {
        super();
    }


    public void processRecord(Record record)
    {
        if (record instanceof SupBookRecord)
        {
            SupBookRecord sbr = (SupBookRecord) record;
            if (sbr.isExternalReferences())
            {
                String url = cleanLink(sbr.getURL());
		if (url ==null || url.trim().equals(""))
                    return;
                Link l = new Link(4);
                l.addUnkownPath(url);
                this.add(l);
            }
        }
    }

    /**
     * returns a 'clean' version of the url...
     * removes the $Program.$Type.$Version\0003 bits that sometimes occur at the 
     * start of a link string.
     *
     * This may cause some problems.
     * 
     * @param linkString
     * @return clean version of the string.
     */
    private String cleanLink(String linkString)
    {
        String out = linkString;

        int idx = linkString.indexOf("\03");
        if (idx >= 0)
        {
            out = linkString.substring(idx + 1);
        }
        //this bit is for some random links that don't come out right and have
        // 0x03 characters in places that look like they should be file separators.
        out = out.replaceAll("\03", "/");
        return out;

    }
}

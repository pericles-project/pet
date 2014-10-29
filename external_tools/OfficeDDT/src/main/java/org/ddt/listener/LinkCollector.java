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
package org.ddt.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.ddt.Link;

/**
 * Superclass for classes that collect links. Really just a more useful name for a List. 
 *
 * It is intended to be used by FileProcessors that have several parts which
 * collect links, e.g. POIFSEventProcessor and RecordEventProcessor
 *
 * @author Niklas Rehfeld
 */
public class LinkCollector extends ArrayList<Link>
{

//    List<Link> links;

    /**
     * Constructor.
     */
    public LinkCollector()
    {
        super();
//        links = new ArrayList<Link>();
    }

    /**
     * retrieves the list of links that this LinkCollector has collected.
     *
     * @return a list of links.
     */
    public List<Link> getLinks()
    {
        return Collections.unmodifiableList(this);
//        return Collections.unmodifiableList(links);
    }

}

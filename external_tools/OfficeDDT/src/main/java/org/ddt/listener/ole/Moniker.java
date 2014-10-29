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
package org.ddt.listener.ole;

/**
 * Common interface for all of the Moniker types found in a HyperlinkMoniker
 * (see MS-OSHARED section 2.3.7.2). These are identifiable by their ClassID.
 * <p/>
 * @author Niklas Rehfeld
 */
interface Moniker
{

    /**
     * Returns a string representation of the link that this moniker represents.
     * The actual format of the link will depend on the implementation, i.e. on
     * the type of moniker that it is.
     *
     * @return Link that the Moniker refers to.
     */
    String getLink();
}

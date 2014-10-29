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

/**
 * Constants that are used by various bits.
 *
 * @author Niklas Rehfeld
 */
public class Constants
{

    /**
     * filetype for word documents.
     */
    public static final int FILETYPE_WORD = 1;
    /**
     * filetype for excel documents
     */
    public static final int FILETYPE_EXCEL = 1 << 1;
    /**
     * filetype for powerpoint documents
     */
    public static final int FILETYPE_POWERPOINT = 1 << 2;

    /**
     * filetype for all documents.
     */
    public static final int FILETYPE_ALL = 0xffffffff;
}

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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Niklas Rehfeld
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{
    org.ddt.LinkTest.class,
    org.ddt.listener.dsi.PropertiesTestSuite.class,
    org.ddt.listener.records.RecordsTestSuite.class,
    org.ddt.moniker.MonikerFactoryTest.class
})
public class AllClassTests
{

}

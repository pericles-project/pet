/**
* Copyright (c) 2014, Fabio Corubolo - University of Liverpool and Anna Eggers - Göttingen State and University Library
* The work has been developed in the PERICLES Project by Members of the PERICLES Consortium.
* This work was supported by the European Commission Seventh Framework Programme under Grant Agreement Number FP7- 601138 PERICLES.
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
* an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without
* limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR
* PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise,
* unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including
* any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this
* License or out of the use or inability to use the Work.
* See the License for the specific language governing permissions and limitation under the License.
*/
package modules;

import model.ExtractionResult;
import model.KeyValueResult;

/**
 * Module that extracts a list of meta data attributes via java
 * System.getProperty(..).
 */
public class SystemProperiesModule extends AbstractEnvironmentModule {
	@Override
	public ExtractionResult extractInformation() {
		ExtractionResult pr = new ExtractionResult(this);
		KeyValueResult sp = new KeyValueResult("System properties");

		sp.add("file_seperator", System.getProperty("file.seperator"));
		sp.add("user_language", System.getProperty("user.language"));
		sp.add("user_name", System.getProperty("user.name"));
		sp.add("user_timezone", System.getProperty("user.timezone"));
		sp.add("user_home", System.getProperty("user.homes"));
		sp.add("os_version", System.getProperty("os.version"));
		sp.add("os_name", System.getProperty("os.name"));
		pr.setResults(sp);
		return pr;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module extracts the following operating system properties:"
				+ "\n- file seperator used by the system"
				+ "\n- user language"
				+ "\n- user timezone"
				+ "\n- user home"
				+ "\n- operating system version"
				+ "\n- operating system name"
				+ "\n\nThe module uses Javas native System.getProperty(...) method.";
		return description;
	}

	@Override
	public void setModuleName() {
		moduleName = "Operating System properties snapshot";
	}

	@Override
	public void setVersion() {
	}
}

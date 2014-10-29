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
package model;

import static configuration.Constants.TEMPLATES_DIRECTORY;
import static utility.FileUtils.createDirectory;
import static utility.FileUtils.deleteDirectory;
import static utility.FileUtils.deleteFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import modules.AbstractModule;
import storage.ConfigSaver;

/**
 * A template class for a {@link Profile}, to save and load a pre-configured set
 * of {@link AbstractModule}s used for the fast creation of a {@link Profile}.
 */
public class ProfileTemplate {
	/** Name of the template, mostly the same name as the profile */
	public String name;
	/** List of pre-configured modules */
	public List<AbstractModule> moduleList = new ArrayList<AbstractModule>();

	/**
	 * Constructs a ProfileTemplate with a specified name
	 * 
	 * @param name
	 *            name of the template
	 */
	public ProfileTemplate(String name) {
		this.name = name;
	}

	/**
	 * Prints the template name
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Saves a template class to a file, which can be send to other PET users to
	 * create similar {@link Profile}s on multiple machines.
	 */
	public void save() {
		String outputDirectory = TEMPLATES_DIRECTORY + name;
		deleteDirectory(outputDirectory);
		createDirectory(outputDirectory);
		for (File file : new File(outputDirectory).listFiles()) {
			// remove "old" modules from fewer templates:
			deleteFile(file.toPath());
		}
		for (AbstractModule module : moduleList) {
			File outputFile = new File(outputDirectory, module.moduleName + "_"
					+ module.getHash());
			ConfigSaver.saveModuleConfiguration(outputFile, module.getConfig());
		}
	}
}

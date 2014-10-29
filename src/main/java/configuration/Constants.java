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
package configuration;

import static utility.FileUtils.createDirectory;

import java.io.File;

import utility.FileUtils;

public class Constants {
	public static final String VERSION = "0.5";

	public static String WORKING_DIRECTORY;
	public static String PROJECT_HOME;
	public static String CONFIG_DIRECTORY;
	public static String LOG_DIRECTORY;
	public static String OUTPUT_DIRECTORY;
	public static String EXTRACTION_PROFILES_DIRECTORY;
	public static String TEMPLATES_DIRECTORY;
	public static String EXPORTED_TABLES_DIRECTORY;

	public static String EXTRACTION_PREFERENCES_FILE;
	public static String SI_LOCK_FILE;
	public static String EVENT_STORAGE_FILE;

	/**
	 * Configures the constants of the projects directories and configuration
	 * files with a specified working directory as parent directory.
	 * 
	 * @param workingDirectory
	 *            Projects working directory
	 */
	public Constants(String workingDirectory) {
		if (workingDirectory == null || workingDirectory.trim().length() == 0) {
			workingDirectory = FileUtils.getCurrentJarFolder(Constants.class)
					+ "";
		}
		WORKING_DIRECTORY = workingDirectory + File.separator;
		PROJECT_HOME = WORKING_DIRECTORY + "PET_data" + File.separator;
		CONFIG_DIRECTORY = PROJECT_HOME + "config" + File.separator;
		LOG_DIRECTORY = PROJECT_HOME + "logs" + File.separator;
		OUTPUT_DIRECTORY = PROJECT_HOME + "extracted_meta_data"
				+ File.separator;
		EXTRACTION_PROFILES_DIRECTORY = CONFIG_DIRECTORY
				+ "extraction_profiles" + File.separator;
		TEMPLATES_DIRECTORY = CONFIG_DIRECTORY + "profile_templates"
				+ File.separator;
		EXPORTED_TABLES_DIRECTORY = OUTPUT_DIRECTORY + "exported_tables"
				+ File.separator;

		EXTRACTION_PREFERENCES_FILE = CONFIG_DIRECTORY
				+ "extractionPreferences";
		SI_LOCK_FILE = CONFIG_DIRECTORY + "lockfile.running";
		EVENT_STORAGE_FILE = OUTPUT_DIRECTORY + "events.json";
		createProjectDirectories();
	}

	/**
	 * Creates all project directories and necessary configuration files.
	 */
	private static void createProjectDirectories() {
		createDirectory(PROJECT_HOME);
		createDirectory(CONFIG_DIRECTORY);
		createDirectory(LOG_DIRECTORY);
		createDirectory(OUTPUT_DIRECTORY);
		createDirectory(EXTRACTION_PROFILES_DIRECTORY);
		createDirectory(TEMPLATES_DIRECTORY);
		createDirectory(EXPORTED_TABLES_DIRECTORY);
	}

}

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
package cli;

/**
 * Class to print the help dialog, that shows the commands that the user can
 * call in the interactive command line mode;
 */
public class CLIHelp {
	/**
	 * Method that prints usage help at the command line, and is called by the
	 * {@link CLIParser}, if the "help" command was called by the user.
	 */
	public static void help() {
		System.out.println("######## General commands ########");
		out("help", "Print this dialog");
		out("status", "Print the current extraction status");
		out("start", "Start continuous extraction");
		out("stop", "Stop continuous extraction");
		out("extract", "Start a single extraction");
		out("gui", "Start GUI");
		out("exit", "Exit the tool");

		System.out.println("######## Profiles ########");
		out("profiles", "List all profiles");
		out("create [NAME]", "Create a new profile with name");
		out("delete [PROFILE_UUID]", "Delete profile with UUID");
		out("profile [PROFILE_UUID]",
				"Show files, modules and information about a profile");
		out("enable [PROFILE_UUID]", "Enable a profile for extraction");
		out("disable [PROFILE_UUID]",
				"Disable profile. It will not be extracted.");
		out("templates", "Show list of templates for profiles");
		out("template2profile [TEMPLATE_NAME]",
				"Create a profile from a template");
		// change module configuration?

		System.out.println("######## Files ########");
		out("add [FILE]", "Add file to the default profile");
		out("add2Profile [FILE] [PROFILE_UUID]", "Add file to profile");
		out("removeFile [FILE] [PROFILE_UUID]", "Remove file from profile");

		System.out.println("######## Modules ########");
		out("modules", "Shows available templates for extraction modules");
		out("module [MODULE]", "Show description of the module");
		out("addModule [MODULE] [PROFILE_UUID]",
				"Add module to profile (prbably the module will need further configuration in order to run)");
		out("removeModule [MODULE] [PROFILE_UUID]",
				"Remove module from profile");
		out("show [MODULE] [PROFILE_UUID]",
				"Show the configuration of the module");
		out("select [MODULE] [PROFILE_UUID]",
				"Adds an module to the list of modules that will be extracted");
		out("select all [PROFILE_UUID]", "Selects all modules to be extracted");
		out("unselect [MODULE] [PROFILE_UUID]",
				"Removes an module from the list of modules that will be extracted");
		out("unselect all [PROFILE_UUID]", "Unselects all modules");
	}

	protected static void out(String option, String description) {
		System.out.println(option + "\t- " + description);
	}
}

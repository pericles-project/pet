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
 * The CLIParser parses the user commands from the interactive command line
 * interface, and calls the right handling functions.
 */
public class CLIParser {

	/**
	 * Method to execute user commands given by the {@link CLI}.
	 * 
	 * @param args
	 *            The raw entered user commands
	 * @param cli
	 *            Reference to the {@link CLI} class, that handles the methods
	 *            to be executed, when the commands are parsed.
	 */
	public static void execute(String args[], CLI cli) {
		final String command = args[0];
		switch (command.toLowerCase()) {
		case "help":
		case "h":
			CLIHelp.help();
			break;
		case "exit":
		case "end":
			cli.controller.exit();
			break;
		case "status":
			cli.status();
			break;
		case "start":
			cli.controller.extractor.setUpdateExtraction(true);
			cli.controller.extractor.startDaemons();
			cli.status();
			break;
		case "stop":
			cli.controller.extractor.setUpdateExtraction(false);
			cli.controller.extractor.stopDaemons();
			cli.status();
			break;
		case "extract":
			cli.controller.extractor.extract(
					cli.controller.profileController.getProfiles(), false,
					false);
			break;
		case "gui":
			cli.controller.startGui();
			break;

		case "add": // add [FILE]
			if (args.length >= 2) {
				String fileName = args[1];
				if (args.length > 2) {
					fileName = getWholeName(args, fileName);
				}
				cli.addFile(fileName);
			}
			break;
		case "add2profile": // add [FILE] [PROFILE_UUID]
			if (args.length >= 3) {
				String profileUUID = args[args.length - 1];
				String fileName = args[1];
				if (args.length > 3) {
					fileName = getWholeNameMinusOne(args, fileName);
				}
				cli.addFileToProfile(fileName, profileUUID);
			}
			break;
		case "addmodule": // addModule [MODULE] [PROFILE_UUID]
			if (args.length >= 3) {
				String profileUUID = args[args.length - 1];
				String moduleName = args[1];
				if (args.length > 3) {
					moduleName = getWholeNameMinusOne(args, moduleName);
				}
				cli.addModuleToProfile(moduleName, profileUUID);
			}
			break;
		case "removefile": // removeFile [FILE] [PROFILE_UUID]
			if (args.length >= 3) {
				String profileUUID = args[args.length - 1];
				String fileName = args[1];
				if (args.length > 3) {
					fileName = getWholeNameMinusOne(args, fileName);
				}
				cli.removeFileFromProfile(fileName, profileUUID);
			}
			break;
		case "removemodule": // removeModule [MODULE] [PROFILE_UUID]
			if (args.length >= 3) {
				String profileUUID = args[args.length - 1];
				String moduleName = args[1];
				if (args.length > 3) {
					moduleName = getWholeNameMinusOne(args, moduleName);
				}
				cli.removeModuleFromProfile(moduleName, profileUUID);
			}
			break;
		case "create": // create [NAME]
			if (args.length >= 2) {
				String profileName = args[1];
				if (args.length > 2) {
					profileName = getWholeName(args, profileName);
				}
				cli.controller.profileController.createProfile(profileName);
			}
			break;
		case "delete": // delete [PROFILE_UUID]
			if (args.length == 2) {
				cli.controller.profileController
						.remove(cli.controller.profileController
								.getProfile(args[1]));
			}
			break;
		case "modules":
			cli.showModules();
			break;
		case "module": // module [MODULE]
			if (args.length >= 2) {
				String moduleName = args[1];
				if (args.length > 2) {
					moduleName = getWholeName(args, moduleName);
				}
				cli.showDescription(moduleName);
			}
			break;
		case "templates":
			cli.showTemplates();
			break;
		case "template2profile":
			cli.createProfileFromTemplate(args);
			break;
		case "profiles":
		case "ls":
			cli.showProfiles();
			break;
		case "profile": // profile [PROFILE_UUID]
			if (args.length == 2) {
				cli.showProfile(args[1]);
			}
			break;
		case "enable":
			if (args.length == 2) {
				cli.enableProfile(args[1], true);
			}
			break;
		case "disable":
			if (args.length == 2) {
				cli.enableProfile(args[1], false);
			}
			break;
		case "show": // show [MODULE] [PROFILE_UUID]
			if (args.length >= 3) {
				String profileUUID = args[args.length - 1];
				String moduleName = args[1];
				if (args.length > 3) {
					moduleName = getWholeNameMinusOne(args, moduleName);
				}
				cli.showModuleConfiguration(moduleName, profileUUID);
			}
			break;
		case "select": // select [MODULE] [PROFILE_UUID]
			// select all [PROFILE_UUID]
			if (args.length >= 3) {
				String profileUUID = args[args.length - 1];
				if (args[1].equals("all")) {
					cli.selectAll(profileUUID, true);
				} else {
					String moduleName = args[1];
					if (args.length > 3) {
						moduleName = getWholeNameMinusOne(args, moduleName);
					}
					cli.enableModule(moduleName, profileUUID, true);
				}
			}
			break;
		case "unselect": // unselect [MODULE] [PROFILE_UUID]
			// unselect all [PROFILE_UUID]
			if (args.length >= 3) {
				String profileUUID = args[args.length - 1];
				if (args[1].equals("all")) {
					cli.selectAll(profileUUID, false);
				} else {
					String moduleName = args[1];
					if (args.length > 3) {
						moduleName = getWholeNameMinusOne(args, moduleName);
					}
					cli.enableModule(moduleName, profileUUID, false);
				}
			}
			break;
		default:
			System.out.println("No such option: " + command);
			System.out.println("Enter \'help\' for help dialog");
			break;
		}
	}

	private static String getWholeName(String[] args, String name) {
		for (int i = 2; i < args.length; i++) {
			name += " " + args[i];
		}
		return name;
	}

	private static String getWholeNameMinusOne(String[] args, String name) {
		for (int i = 2; i < args.length - 1; i++) {
			name += " " + args[i];
		}
		return name;
	}
}

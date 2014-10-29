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

import static utility.FileUtils.fileExists;

import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;

import model.GenericModule;
import model.Part;
import model.Profile;
import model.ProfileTemplate;
import modules.AbstractModule;
import storage.ConfigSaver;

import com.fasterxml.jackson.core.JsonProcessingException;

import controller.ExtractionController;
import controller.ModuleController;

/**
 * This class provides an interactive command line interface for the user. It
 * uses the {@link CLIParser} class for parsing the user input.
 */
public class CLI implements Runnable {
	private final Scanner scanner;
	protected final ExtractionController controller;

	/**
	 * Constructor called by the {@link ExtractionController} to start the
	 * command line interface at application start.
	 * 
	 * @param controller
	 *            reference to the applications {@link ExtractionController}
	 */
	public CLI(ExtractionController controller) {
		this.controller = controller;
		System.out.println("[P]ericles [E]xtraction [T]ool (prototype)");
		scanner = new Scanner(new InputStreamReader(System.in));
		Thread thread = new Thread(this);
		thread.start();
		thread.setName("Pericles CLI thread");
	}

	/**
	 * run method of the cli thread that prints continuously the prompt and
	 * executes entered user commands.
	 */
	@Override
	public void run() {
		while (true) {
			printPrompt();
			executeUserCommands();
			sleep();
		}
	}

	private void printPrompt() {
		System.out.println("PET > ");
	}

	private synchronized void executeUserCommands() {
		if (scanner.hasNext()) {
			String args[] = scanner.nextLine().split(" ");
			CLIParser.execute(args, this);
		}
	}

	private void sleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Method executed, if the "status" command was entered: View if continuous
	 * extraction and daemon observation is active.
	 */
	protected void status() {
		System.out.println("\tContinuous extraction: "
				+ controller.extractor.getUpdateExtraction());
		System.out.println("Environment monitor daemons running: "
				+ controller.extractor.getDaemonsRunning());
	}

	/**
	 * Method called to enable or disable all {@link AbstractModule}s belonging
	 * to a {@link Profile}.
	 * 
	 * @param profileUUID
	 *            UUID of the {@link Profile}
	 * @param enable
	 *            flag if the modules should be enabled or disabled
	 */
	protected void selectAll(String profileUUID, boolean enable) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			for (AbstractModule module : profile.getModules()) {
				module.setSelected(enable);
			}
			if (enable) {
				System.out.println("All modules of profile " + profileUUID
						+ " enabled.");
			} else {
				System.out.println("All modules of profile " + profileUUID
						+ " disabled.");
			}
		}
	}

	/**
	 * Add a file to the default profile
	 * 
	 * @param filePath
	 *            path to the file that should be added
	 */
	protected void addFile(String filePath) {
		Path file = Paths.get(filePath);
		if (!fileExists(file)) {
			System.out.println("File does not exist: " + file);
			return;
		}
		Profile defaultProfile = controller.profileController
				.getDefaultProfile();
		Part part = getPartOrPrintError(filePath, defaultProfile);
		if (part != null) {
			defaultProfile.addPart(part);
			System.out.println("File " + file
					+ " added to the default profile.");
		}
	}

	/**
	 * Show a list of all available {@link GenericModule}s via CLI, if the
	 * "modules" command is executed.
	 */
	protected void showModules() {
		System.out.println("List of available extraction module templates:\n");
		GenericModule[] gm = ModuleController.getGenericModuleSet().toArray(
				new GenericModule[0]);
		Arrays.sort(gm, new Comparator<GenericModule>() {
			@Override
			public int compare(GenericModule o1, GenericModule o2) {
				return o1.moduleName.compareTo(o2.moduleName);
			}
		});
		for (GenericModule module : gm) {
			System.out.println(module.moduleName);
		}
		System.out
				.println("\nIf you want to create a module from one of the templates, type \"addModule [MODULE] [PROFILE_UUID].\"");
		System.out
				.println("You can get a description of the templates, if you type: \"module [MODULE]\"");
	}

	/**
	 * Shows a list of all current {@link Profile}s, when the user types
	 * "profiles".
	 */
	protected void showProfiles() {
		System.out.println("Profiles:");
		for (Profile profile : controller.profileController.getProfiles()) {
			System.out.println("Name: [" + profile.getName() + "] UUID: ["
					+ profile.getUUID() + "]");
		}
	}

	/**
	 * Lists all {@link Part}s and {@link AbstractModule}s of a {@link Profile},
	 * when the user types "profile [PROFILE_UUID]"
	 * 
	 * @param UUID
	 */
	protected void showProfile(String profileUUID) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			System.out.println("Profile UUID: " + profile.getUUID());
			System.out.println("Profile name: " + profile.getName());
			System.out.println("Enabled: " + profile.isEnabled());
			System.out.println("\nParts: ");
			for (Part part : profile.getParts()) {
				System.out.println(part.getPath());
			}
			System.out.println("\nModules: ");
			for (AbstractModule module : profile.getModules()) {
				System.out.println(module.moduleName);
			}
		}
	}

	/**
	 * Removes a {@link Part} from a {@link Profile}, when the user types
	 * "removeFile [FILE] [PROFILE_UUID]"
	 * 
	 * @param file
	 *            String path to the file that is represented by the
	 *            {@link Part} to be removed
	 * @param profileUUID
	 *            UUID of the {@link Profile} were the {@link Part} belongs to
	 */
	protected void removeFileFromProfile(String file, String profileUUID) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			profile.removePart(getPartOrPrintError(file, profile));
		}
	}

	/**
	 * Adds a file as {@link Part} to a {@link Profile}, when the user types
	 * "add2Profile [FILE] [PROFILE_UUID]".
	 * 
	 * @param file
	 * @param profileUUID
	 *            UUID of the {@link Profile} were the {@link Part} belongs to
	 */
	protected void addFileToProfile(String file, String profileUUID) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			Part part = getPartOrPrintError(file, profile);
			if (part != null) {
				profile.addPart(part);
			} else {
				System.out.println("");
			}
		}
	}

	/**
	 * TODO: problem if there are more then one modules with the same name,
	 * because of the identification via module name.
	 * 
	 * Enables or disables the extraction of an {@link AbstractModule}, when the
	 * user types "select [MODULE] [PROFILE_UUID]" or
	 * "unselect [MODULE] [PROFILE_UUID]"
	 * 
	 * @param moduleName
	 *            name of the module
	 * @param profileUUID
	 *            UUID of the {@link Profile} where the module belongs to
	 * @param enable
	 *            flags whether the module should be enabled or disabled.
	 */
	protected void enableModule(String moduleName, String profileUUID,
			boolean enable) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			for (AbstractModule module : profile.getModules()) {
				if (module.moduleName.equals(moduleName)) {
					module.setSelected(enable);
					System.out.println("Module " + moduleName + " selected: "
							+ enable);
				}
			}
		}
	}

	/**
	 * Shows the description of a {@link GenericModule}, if the user types
	 * "module [MODULE]"
	 * 
	 * @param moduleName
	 */
	protected void showDescription(String moduleName) {
		GenericModule module = ModuleController.getGenericModule(moduleName);
		if (module != null) {
			System.out.println("Description for module: " + moduleName);
			System.out.println(module.moduleDescription);

		} else {
			System.out.println("No module available with the name: "
					+ moduleName);
		}
	}

	/**
	 * Adds an {@AbstractModule} to a {@link Profile}, when the
	 * user types "addModule [MODULE] [PROFILE_UUID]" whereas [MODULE] is the
	 * name of the {@link GenericModule} that serves as template for the
	 * {@link AbstractModule}.
	 * 
	 * @param moduleName
	 *            name of the template {@link GenericModule}
	 * @param profileUUID
	 *            UUID of the {@link Profile} where the module should be added.
	 */
	protected void addModuleToProfile(String moduleName, String profileUUID) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			AbstractModule module = ModuleController.loadModule(moduleName);
			if (module != null) {
				profile.addModule(module);
				System.out
						.println("New module with name "
								+ moduleName
								+ " added to profile "
								+ profileUUID
								+ ".\nCheck if the module needs further configuration!");

			} else {
				System.out.println("No module available with name "
						+ moduleName);
			}
		}
	}

	/**
	 * TODO: problem if there are more then one modules with the same name,
	 * because of the identification via module name.
	 * 
	 * Removes an {@link AbstractModule} form a {@link Profile} when the user
	 * types "removeModule [MODULE] [PROFILE_UUID]"
	 * 
	 * @param moduleName
	 * @param profileUUID
	 */
	public void removeModuleFromProfile(String moduleName, String profileUUID) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			boolean moduleFound = false;
			for (AbstractModule module : profile.getModules()) {
				if (module.moduleName.equals(moduleName)) {
					System.out.println("Module " + moduleName
							+ " removed from profile " + profileUUID + ".");
					moduleFound = true;
					profile.removeModule(module);
					break;
				}
			}
			if (moduleFound == false) {
				System.out.println("No module with name " + moduleName
						+ " in profile " + profileUUID + ".");
			}
		}
	}

	/**
	 * TODO: problem if there are more then one modules with the same name,
	 * because of the identification via module name.
	 * 
	 * Shows the configuration of an {@link AbstractModule} when the user types
	 * "show [MODULE] [PROFILE_UUID]"
	 * 
	 * @param moduleName
	 *            name of the module
	 * @param profileUUID
	 *            UUID of the {@link Profile} where the module belongs to
	 */
	public void showModuleConfiguration(String moduleName, String profileUUID) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			boolean moduleFound = false;
			for (AbstractModule module : profile.getModules()) {
				if (module.moduleName.equals(moduleName)) {
					System.out.println("Module: " + module.getConfig().moduleDisplayName);
					System.out.println("Module type: " + module.moduleName);
					System.out.println("Description: "
							+ module.getModuleDescription());
					System.out.println("Configuration: ");
					try {
						String JSONconfig = ConfigSaver
								.saveModuleConfiguration(module.getConfig());
						System.out.println(JSONconfig);
					} catch (JsonProcessingException e) {
						System.out
								.println("Error while reading configuration file.");
					}
					moduleFound = true;
				}
			}
			if (moduleFound == false) {
				System.out.println("No module with name " + moduleName
						+ " in profile " + profileUUID + ".");
			}
		}
	}

	/**
	 * Enables or disables a {@link Profile}, when the user types
	 * "enable [PROFILE_UUID]".
	 * 
	 * @param profileUUID
	 *            UUID of the profile, given by the user
	 * @param enable
	 *            flag if the profile should be enabled, or disabled.
	 */
	protected void enableProfile(String profileUUID, boolean enable) {
		Profile profile = getProfileOrPrintError(profileUUID);
		if (profile != null) {
			profile.setEnabled(enable);
		}
	}

	/**
	 * Lists all available {@link ProfileTemplate}s, when the user types
	 * "templates"
	 */
	public void showTemplates() {
		HashSet<ProfileTemplate> templates = controller.profileController
				.getTemplates();
		for (ProfileTemplate template : templates) {
			System.out.println(template.name);
			for (AbstractModule module : template.moduleList) {
				System.out.println("\t- " + module.moduleName);
			}
			System.out.println("");
		}
	}

	/**
	 * Creates a {@link Profile} from a {@link ProfileTemplate}, when the user
	 * types "template2profile [TEMPLATE_NAME]"
	 * 
	 * @param args
	 *            user enter separated by spaces
	 */
	public void createProfileFromTemplate(String[] args) {
		String templateName = "";
		for (int i = 1; i < args.length; i++) {
			templateName += args[i] + " ";
		}
		for (ProfileTemplate template : controller.profileController
				.getTemplates()) {
			if ((template.name + " ").equals(templateName)) {
				controller.profileController
						.createProfileFromTemplate(template);
				System.out.println("Profile created from template "
						+ template.name + "!");
				return;
			}
		}
		System.out
				.println("Error: Profile not created! Template cannot be found.");
	}

	/**
	 * Gets a {@link Profile} from the {@link ExtractionController}, prints the
	 * error message in case of a failure.
	 * 
	 * @param profileUUID
	 *            profiles UUID given by the user
	 * @return profile, if available, or null otherwise
	 */
	private Profile getProfileOrPrintError(String profileUUID) {
		Profile profile = controller.profileController.getProfile(profileUUID);
		if (profile == null) {
			System.out.println("Error: No profile with UUID " + profileUUID
					+ " available.");
		}
		return profile;
	}

	/**
	 * Gets a {@link Part} belonging to a {@link Profile}, or prints the error
	 * message in case of a failure.
	 * 
	 * @param file
	 *            String path to file represented by the {@link Part}
	 * @param profile
	 *            profile where the part belongs to
	 * @return {@link Part}, if available, or null otherwise
	 */
	private Part getPartOrPrintError(String file, Profile profile) {
		Part part = Part.create(Paths.get(file), profile.getUUID());
		if (part == null) {
			System.out.println("Error: No file with the path " + file
					+ "available.");
		}
		return part;
	}
}

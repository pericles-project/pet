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
package controller;

import static configuration.Constants.EXTRACTION_PROFILES_DIRECTORY;
import static configuration.Constants.TEMPLATES_DIRECTORY;
import static utility.FileUtils.createDirectory;
import static utility.FileUtils.deleteDirectory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import model.GenericModule;
import model.Part;
import model.Profile;
import model.ProfileTemplate;
import modules.AbstractModule;
import storage.ConfigSaver;
import configuration.Constants;
import configuration.Log;
import configuration.ModuleConfiguration;

/**
 * The ProfileController manages all {@link Profile}s and provides a default
 * profile. Furthermore it provides a set of {@link ProfileTemplate}s to allow a
 * fast creation of a predefined Profile.
 */
public class ProfileController {
	/** The default profile */
	private Profile defaultProfile;
	/** List of all currently existing profiles */
	private final HashSet<Profile> profiles = new HashSet<Profile>();
	/** List of all available templates to create profiles */
	private final HashSet<ProfileTemplate> templates = new HashSet<ProfileTemplate>();
	///** The applications {@link ExtractionController} */
	public final ExtractionController controller;

	/**
	 * List of used UUIDs to check if a UUID already exists at time of
	 * generation.
	 */
	private final List<String> profileUUIDs = new ArrayList<String>();

	/**
	 * Constructor of ProfileController will be called by the
	 * {@link ExtractionController}. Saved profiles and templates will be loaded
	 * and the default profile created.
	 * 
	 * @param controller
	 *            The applications ExtractionController
	 */
	public ProfileController(ExtractionController controller) {
		this.controller = controller;
		loadProfiles();
		loadTemplates();
		createDefaultProfile();
	}
	public ProfileController(String dataFolder) {
		this.controller = null;
		new Constants(dataFolder);
		loadProfiles();
		//loadTemplates();
		//createDefaultProfile();
	}
	/**
	 * Loads all stored {@link Profile}s.
	 */
	private void loadProfiles() {
		File profilesDirectory = new File(EXTRACTION_PROFILES_DIRECTORY);
		String[] UUIDs = profilesDirectory.list();
		if (UUIDs == null)
			return;
		for (String uuid : UUIDs) {
			if (new File(EXTRACTION_PROFILES_DIRECTORY + uuid).isDirectory()) {
				Profile profile = new Profile(this, uuid);
				profileUUIDs.add(uuid);
				profiles.add(profile);
			}
		}
	}

	/**
	 * Loads all available {@link ProfileTemplate}s.
	 */
	private void loadTemplates() {
		File templatesParentDirectory = new File(TEMPLATES_DIRECTORY);
		loadTemplatesFrom(templatesParentDirectory);
		boolean defaultTemplatesAvailable = !templates.isEmpty();
		
		if (!defaultTemplatesAvailable) {
			File f = utility.FileUtils.getCurrentJarFolder(this.getClass());
			File f2 = new File(f, "profile_templates");
			if (!f2.exists()) {
				f2 = new File("profile_templates");
			}
			loadTemplatesFrom(f2);
		}
	}

	/**
	 * Loads all available {@link ProfileTemplate}s from a specific directory.
	 * 
	 * @param templatesParentDirectory
	 *            The path to the directory where the templates are stored.
	 */
	public void loadTemplatesFrom(File templatesParentDirectory) {
		for (File templateDirectory : templatesParentDirectory.listFiles()) {
			if (!templateDirectory.isDirectory()) {
				continue;
			}
			ProfileTemplate template = new ProfileTemplate(
					templateDirectory.getName());
			File[] moduleFileNames = templateDirectory.listFiles();
			if (moduleFileNames != null) {
				for (File moduleFile : moduleFileNames) {
					ModuleConfiguration configuration = ConfigSaver
							.loadModuleConfiguration(moduleFile);
					if (configuration == null) {
						Log.MODULE_LOGGER.log(Level.SEVERE,
								"Problem loading module" + moduleFile);
						continue;
					}
					AbstractModule module = ModuleController
							.loadModule(configuration.moduleName);
					if (module == null) {
						Log.MODULE_LOGGER.log(Level.SEVERE,
								"Problem loading module"
										+ configuration.moduleName);
					} else {
						module.setConfig(configuration);
						template.moduleList.add(module);
					}
				}
			}
			templates.add(template);
		}
	}

	/**
	 * Creates the default profile, if it doesn't exist.
	 */
	private void createDefaultProfile() {
		if (defaultProfile == null) {
			defaultProfile = createProfile("defaultProfile");
//			for (GenericModule genericModule : ModuleController
//					.getGenericModuleSet()) {
//				defaultProfile.addModule(genericModule.getInstance());
//			}
		}
		profiles.add(defaultProfile);
	}

	/**
	 * Creates a new {@link Profile}
	 * 
	 * @param name
	 *            the name of the new profile
	 * @return the new profile
	 */
	public Profile createProfile(String name) {
		String uuid = generateUUID();
		Profile profile = new Profile(this, name, uuid);
		profileUUIDs.add(uuid);
		profiles.add(profile);
		return profile;
	}

	/**
	 * Creates a new {@link Profile} from a {@link ProfileTemplate}.
	 * 
	 * @param template
	 *            The template that serves to create the profile
	 * @return the new profile
	 */
	public Profile createProfileFromTemplate(ProfileTemplate template) {
		Profile templateProfile = createProfile(template.name);
		templateProfile.addAllModules(template.moduleList);
		return templateProfile;
	}

	/**
	 * Removes a {@link Profile} form the application.
	 * 
	 * @param profile
	 *            Profile to be removed.
	 */
	public void remove(Profile profile) {
		if (profile != null && profile != defaultProfile) {
			profileUUIDs.remove(profile.getUUID());
			profiles.remove(profile);
		}
	}

	/**
	 * Returns a {@link Profile} with a specific UUID
	 * 
	 * @param UUID
	 *            the UUID of the returned profile
	 * @return the profile
	 */
	public Profile getProfile(String UUID) {
		if (UUID == null || UUID == "")
			return null;
		for (Profile profile : profiles) {
			if (profile.getUUID().equals(UUID))
				return profile;
		}
		return null;
	}

	/**
	 * Returns the default profile (the only profile that does always exist)
	 * 
	 * @return default profile
	 */
	public Profile getDefaultProfile() {
		return defaultProfile;
	}

	/**
	 * Returns a list of all current {@link Profile}s.
	 * 
	 * @return applications profiles
	 */
	public HashSet<Profile> getProfiles() {
		return profiles;
	}

	/**
	 * Utility data structure to manage a file ({@link Part}) that belongs to a
	 * specific {@Profile}.
	 */
	protected class ProfilePart {
		public Profile profile;
		public Part part;

		public ProfilePart(Profile profile, Part part) {
			super();
			this.profile = profile;
			this.part = part;
		}
	}

	/**
	 * Returns a list of all {@link Profile}s that contain a specific file.s
	 * 
	 * @param file
	 *            path to the file
	 * @return list of profiles that contain the passed file
	 */
	public HashSet<ProfilePart> getProfilesWithPath(Path file) {
		HashSet<ProfilePart> profilesWithPart = new HashSet<ProfilePart>();
		for (Profile profile : profiles) {
			profile.cleanupParts();
			for (Part part : profile.getParts()) {
				if (part.getFile().equals(file)) {
					profilesWithPart.add(new ProfilePart(profile, part));
				}
			}
		}
		return profilesWithPart;
	}

	/**
	 * Deletes all extracted information.
	 */
	public void deleteAllMetadata() {
		for (Profile profile : profiles) {
			profile.deleteAllMetadata();
		}
	}

	/**
	 * Saves all {@link Profile}s and {@link ProfileTemplate}s during
	 * application shutdown.
	 */
	public void saveConfig() {
		deleteDirectory(EXTRACTION_PROFILES_DIRECTORY);
		createDirectory(EXTRACTION_PROFILES_DIRECTORY);
		for (Profile profile : profiles) {
			profile.save();
		}
		for (ProfileTemplate template : templates) {
			template.save();
		}
	}

	/**
	 * Makes a {@link Profile} the default profile.
	 * 
	 * @param profile
	 */
	public void setDefaultProfile(Profile profile) {
		this.defaultProfile = profile;
	}

	/**
	 * Getter for available templates
	 * 
	 * @return list of templates for profile creation
	 */
	public HashSet<ProfileTemplate> getTemplates() {
		return templates;
	}

	/**
	 * Add a newly created template (profile -> export as template) to the list
	 * of templates
	 * 
	 * @param template
	 *            ProfileTemplate to be added
	 */
	public void addTemplate(ProfileTemplate template) {
		templates.add(template);
	}

	/**
	 * Generates an UUID used at profile creation.
	 * 
	 * @return The generated UUID.
	 */
	private String generateUUID() {
		String uuID = UUID.randomUUID().toString();
		if (profileUUIDs.contains(uuID))
			return generateUUID();
		else
			return uuID;
	}
}

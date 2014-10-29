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

import static configuration.Constants.EXTRACTION_PROFILES_DIRECTORY;
import static configuration.Log.EXCEPTION_LOGGER;
import static utility.FileUtils.createDirectory;
import static utility.FileUtils.deleteDirectory;
import static utility.FileUtils.fileExists;
import static utility.FileUtils.isDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import modules.AbstractDaemonModule;
import modules.AbstractModule;
import storage.ConfigSaver;
import configuration.Log;
import configuration.ModuleConfiguration;
import controller.ModuleController;
import controller.ProfileController;

/**
 * A Profile is a data structure that contains a list of files ({@link Part})
 * and a list of configured {@link AbstractModule}s to be used on these files.
 * Furthermore a {@link Environment} instance is contained, to keep the results
 * of the profiles file-independent extraction modules.
 * 
 * Each profile has a final UUID to be identified and a changeable name which is
 * shown at the user interfaces.
 */
public class Profile {
	private final HashSet<Part> parts = new HashSet<Part>();
	private final HashSet<AbstractModule> modules = new HashSet<AbstractModule>();
	private final String UUID;
	private String name;
	private final ProfileController profileController;
	private final Environment environment;
	private boolean enabled = true;

	/**
	 * Profile constructor, if no name is given.
	 * 
	 * @param profileController
	 *            The applications {@link ProfileController}
	 * @param uuID
	 *            Has to be created by ProfileController
	 */
	public Profile(ProfileController profileController, String uuID) {
		this(profileController, "unnamed profile", uuID);
	}

	/**
	 * Profile constructor to create a Profile with a specific name
	 * 
	 * @param profileController
	 *            The applications {@link ProfileController}
	 * @param name
	 *            The name of the Profile to be shown at the user interfaces
	 * @param uuID
	 *            The identifier of the Profile
	 */
	public Profile(ProfileController profileController, String name, String uuID) {
		this.profileController = profileController;
		this.environment = new Environment(uuID);
		UUID = uuID;
		setName(name);
		loadProperties();
		loadModules();
		loadParts();
	}

	/**
	 * Loads the Profiles settings
	 */
	protected void loadProperties() {
		if (!fileExists(getOutputDirectory() + "properties"))
			return;
		Properties properties = new Properties();
		try {
			FileInputStream inputStream = new FileInputStream(
					getOutputDirectory() + "properties");
			properties.loadFromXML(inputStream);
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at loadProperties() of profile: " + UUID, e);
			return;
		}
		String propName = properties.getProperty("name");
		if (propName != null) {
			setName(propName);
		}
		String isDefaultProfile = properties.getProperty("defaultProfile");
		if (isDefaultProfile != null) {
			profileController.setDefaultProfile(this);
		}
		String enabled = properties.getProperty("enabled");
		if (enabled != null && enabled.equals("true")) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

	/**
	 * Loads the {@link AbstractModule}s belonging to this Profile.
	 */
	protected void loadModules() {
		File profileDirectory = new File(getOutputDirectory());
		String[] fileNames = profileDirectory.list();
		if (fileNames == null)
			return;
		for (String name : fileNames) {
			if (name.equals("PARTS") || name.equals("properties")
					|| name.startsWith(".")) {
				continue;
			}
			ModuleConfiguration moduleConfig = ConfigSaver
					.loadModuleConfiguration(new File(getOutputDirectory(),
							name));
			if (moduleConfig == null) {
				Log.MODULE_LOGGER.severe("Will ignore module config: " + name
						+ " that is in profile " + this.getName()
						+ " as it's broken");
				continue;
			}
			AbstractModule module = ModuleController
					.loadModule(moduleConfig.moduleName);
			if (module == null) {
				Log.MODULE_LOGGER.severe("Will ignore module: "
						+ moduleConfig.moduleName + " that is in profile "
						+ this.getName() + " as it's missing");
				continue;
			}
			module.setConfig(moduleConfig);
			addModule(module);
		}
	}

	/**
	 * Loads the {@link Part}s belonging to this Profile.
	 */
	protected void loadParts() {
		String partsFile = getOutputDirectory() + "PARTS";
		if (!fileExists(partsFile))
			return;
		addAllPartsFromPaths(ConfigSaver.loadPathSet(partsFile), false);
	}

	/**
	 * Saves the complete Profile during application shutdown.
	 */
	public void save() {
		String outputDirectory = getOutputDirectory();
		deleteDirectory(outputDirectory);
		createDirectory(outputDirectory);
		for (AbstractModule module : modules) {
			File outputFile = new File(outputDirectory, module.moduleName + "_"
					+ module.getHash());
			ConfigSaver.saveModuleConfiguration(outputFile, module.getConfig());
		}
		ConfigSaver.savePartSet(parts, outputDirectory + File.separator
				+ "PARTS");
		Properties profileProperties = new Properties();
		profileProperties.put("name", name);
		if (this.equals(profileController.getDefaultProfile())) {
			profileProperties.put("defaultProfile", "true");
		}
		profileProperties.put("enabled", "" + isEnabled());
		try {
			FileOutputStream outputStream = new FileOutputStream(
					outputDirectory + "properties");
			profileProperties.storeToXML(outputStream, UUID);
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE, "Exception at save of profile: "
					+ UUID, e);
		}
	}

	/**
	 * Changes the name of this Profile
	 * 
	 * @param newName
	 *            new name string
	 */
	public void setName(String newName) {
		if (newName == null || newName.equals("")) {
			name = "unnamed profile";
		} else {
			name = newName;
		}
	}

	/**
	 * Returns the current name of this Profile
	 * 
	 * @return current Profiles name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the UUID of this Profile
	 * 
	 * @return UUID of this Profile
	 */
	public String getUUID() {
		return UUID;
	}

	/**
	 * Returns the {@link Environment} of this Profile
	 * 
	 * @return the environment of this profile
	 */
	public Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * Removes all {@link Part}s from this Profile, that refer to non-existing
	 * files.
	 */
	public void cleanupParts() {
		HashSet<Part> invalidParts = new HashSet<Part>();
		for (Part part : parts) {
			if (!fileExists(part.getFile())) {
				invalidParts.add(part);
			}
		}
		parts.removeAll(invalidParts);
	}

	/**
	 * Returns all {@link Part}s belonging to this profile.
	 * 
	 * @return HashSet of all Parts contained in this Profile.
	 */
	public HashSet<Part> getParts() {
		return parts;
	}

	/**
	 * Returns a string path to the output directory of this profile.
	 * 
	 * @return output directory paths
	 */
	public String getOutputDirectory() {
		String outputDirectory = EXTRACTION_PROFILES_DIRECTORY + UUID
				+ File.separator;
		if (!isDirectory(outputDirectory)) {
			createDirectory(outputDirectory);
		}
		return outputDirectory;
	}

	/**
	 * Returns all {@link AbstractModule}s contained in this Profile.
	 * 
	 * @return HashSet of all profiles AbstractModules
	 */
	public HashSet<AbstractModule> getModules() {
		return modules;
	}

	/**
	 * Add a {@link Part} to this Profile.
	 * 
	 * @param part
	 */
	public void addPart(Part part) {
		addPart(part, false);
	}

	/**
	 * Add a {@link Part} to this Profile. An initial extraction will be started
	 * for this part, which can be asynchronous or synchronous.
	 * 
	 * @param part
	 *            Part to be added
	 * @param async
	 *            Indicates whether the initial extraction run should be
	 *            asynchronous.
	 */
	public void addPart(Part part, boolean async) {
		if (part == null || !fileExists(part.getFile()))
			return;
		for (Part existingPart : parts) {
			if (("" + existingPart.getFile()).equals("" + part.getFile()))
				return;
		}
		parts.add(part);
		profileController.controller.fileMonitorDaemon.registerFile(part
				.getFile());
		Part[] p = new Part[] { part };
		profileController.controller.extractor.extractNewParts(p, this, async);
	}

	/**
	 * Remove a specific {@link Part} from this profile.s
	 * 
	 * @param part
	 *            Part to be removed.
	 */
	public void removePart(Part part) {
		for (Part profilePart : parts) {
			if (profilePart.getFile().equals(part.getFile())) {
				parts.remove(profilePart);
				profileController.controller.fileMonitorDaemon.update();
				return;
			}
		}
	}

	/**
	 * Removes all {@link Part}s from this profile.
	 */
	public void removeAllParts() {
		parts.clear();
	}

	/**
	 * Adds all {@link AbstractModule}s of a list to this profile.
	 * 
	 * @param modules
	 */
	public void addAllModules(List<AbstractModule> modules) {
		for (AbstractModule module : modules) {
			addModule(module);
		}
	}

	/**
	 * Adds an {@link AbstractModule} to this profile.
	 * 
	 * @param module
	 *            AbstractModule to be added.
	 */
	public void addModule(AbstractModule module) {
		if (module == null)
			return;
		modules.add(module);
		File configFile = new File(getOutputDirectory(), module.moduleName
				+ "_" + module.getHash());
		if (!configFile.isFile()) {
			// create module config file, if it doesn't exist yet
			ConfigSaver.saveModuleConfiguration(configFile, module.getConfig());
		}
		if (enabled) {
			if (module instanceof AbstractDaemonModule) {
				AbstractDaemonModule adm = (AbstractDaemonModule) module;
				if (profileController.controller.extractor
						.getUpdateExtraction()) {
					adm.start();
				}
			}
		}
	}

	/**
	 * Removes all {@link AbstractModule}s from this profile.
	 */
	public void removeAllModules() {
		modules.clear();
	}

	/**
	 * Removes a specific {@link AbstractModule} from this profile.
	 * 
	 * @param module
	 */
	public void removeModule(AbstractModule module) {
		if (module instanceof AbstractDaemonModule) {
			((AbstractDaemonModule) module).stop();
		}
		File profileDirectory = new File(getOutputDirectory());
		File configFile = new File(profileDirectory, module.moduleName + "_"
				+ module.getHash());
		configFile.delete();
		modules.remove(module);
	}

	/**
	 * Stops all daemon modules of this profile.
	 */
	public void stopAllDaemons() {
		for (AbstractModule m : modules)
			if (m instanceof AbstractDaemonModule) {
				AbstractDaemonModule adm = (AbstractDaemonModule) m;
				adm.stop();
			}
	}

	/**
	 * Starts all daemon modules of this profile.
	 */
	public void startAllDaemons() {
		for (AbstractModule m : modules)
			if (m instanceof AbstractDaemonModule) {
				AbstractDaemonModule adm = (AbstractDaemonModule) m;
				adm.start();
			}
	}

	/**
	 * Overrides toString() and returns profiles name.
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Deletes all extracted information.
	 */
	public void deleteAllMetadata() {
		environment.deleteExtractedInformation();
		for (Part part : parts) {
			part.deleteExtractedInformation();
		}
	}

	/**
	 * All all files defines by a Path set to the profile.
	 * 
	 * @param paths
	 *            Paths to the files that should be added.
	 */
	public void addAllPartsFromPaths(HashSet<Path> paths, boolean extract) {
		HashSet<Part> partSet = new HashSet<Part>();
		for (Path path : paths) {
			if (fileExists(path)) {
				Part newPart = Part.create(path, UUID);
				if (newPart != null) {
					partSet.add(newPart);
				}
			}
			if (isDirectory(path)) {
				partSet.addAll(getPartsFromDirectoryFiles(path.toFile()));
			}
		}
		Iterator<Part> pit = partSet.iterator();
		while (pit.hasNext()) {
			Part part = pit.next();
			boolean inSet = false;
			for (Part existingPart : parts) {
				if ((existingPart.getFile() + "").equals(part.getFile() + "")) {
					inSet = true;
				}
			}
			if (!inSet) {
				profileController.controller.fileMonitorDaemon
						.registerFile(part.getFile());
				parts.add(part);
			} else {
				pit.remove();
			}
		}
		if (extract && partSet.size() > 0)
			profileController.controller.extractor.extractNewParts(
					partSet.toArray(new Part[0]), this, true);
	}

	/**
	 * Loads all saved {@link Part}s from a specific directory.
	 * 
	 * @param directory
	 *            Path of the directory to load the Parts from.
	 * @return List of all loaded Parts
	 */
	protected HashSet<Part> getPartsFromDirectoryFiles(File directory) {
		HashSet<Part> partSet = new HashSet<Part>();
		if (directory == null || !directory.exists())
			return partSet;
		if (directory.listFiles().length == 0)
			return partSet;
		for (File child : directory.listFiles()) {
			if (child.isFile()) {
				Part part = Part.create(child.toPath(), UUID);
				if (part != null) {
					partSet.add(part);
				}
			} else if (child.isDirectory()) {
				partSet.addAll(getPartsFromDirectoryFiles(child));
			}
		}
		return partSet;
	}

	/**
	 * Returns if the extraction will be started for this Profile.
	 * 
	 * @return true if extraction is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables the extraction of this Profile.
	 * 
	 * @param enable
	 *            Enables the extraction if true.
	 */
	public void setEnabled(boolean enable) {
		if (enabled && !enable) {
			stopAllDaemons();
		} else if (!enabled && enable) {
			startAllDaemons();
		}
		enabled = enable;
	}

	/**
	 * Exports the profile as {@link ProfileTemplate}: The list of configured
	 * {@link AbstractModule}s survives, and the name of the Profile.
	 */
	public void exportAsTemplate() {
		ProfileTemplate template = new ProfileTemplate(name);
		template.moduleList.addAll(modules);
		template.save();
		profileController.addTemplate(template);
	}
}

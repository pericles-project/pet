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

import java.util.HashSet;

import model.OperatingSystem;
import model.OperatingSystem.OsName;
import modules.AbstractModule;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Configuration for an {@link AbstractModule}. The modules are distinguished by
 * a hash value of their configurations. The configuration is serialized to
 * JSON, when the tool is closed.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public class ModuleConfiguration {
	/** Not-changeable module name */
	public String moduleName;
	/** Changeable module name, only used for displaying purposes */
	public String moduleDisplayName;
	/** Current version of the module */
	public String moduleVersion;

	/** flags, if the module is currently used for extraction */
	public boolean enabled;
	/** records events at event file */
	public boolean recordEvents = false;
	private HashSet<OperatingSystem.OsName> supportedSystems = new HashSet<OperatingSystem.OsName>();
	/** includes or excludes file types */
	public FileFilter fileFilter = new FileFilter();
	/** when this event is generated, the part is added to profile UUID */
	public String eventAddToProfile;

	/**
	 * Used for the serialization. Don't use this constructor for other
	 * purposes.
	 */
	@Deprecated
	public ModuleConfiguration() {
	}

	/**
	 * Constructor to be called by the associated {@link AbstractModule}.
	 * 
	 * @param moduleName
	 *            not-changeable module name
	 * @param version
	 *            current version of the module
	 */
	public ModuleConfiguration(String moduleName, String version) {
		this.moduleName = this.moduleDisplayName = moduleName;
		this.moduleVersion = version;
		this.enabled = true;
		this.supportedSystems = new HashSet<OperatingSystem.OsName>();
		this.supportedSystems.add(OsName.SYSTEM_INDEPENDENT);
	}

	/**
	 * Override this method, if a customized module configuration needs
	 * validation!
	 * 
	 * @return flag if the configuration is valid
	 */
	public boolean configurationIsValid() {
		return true;
	}

	/**
	 * The default is "system independent". If a system is added to the list of
	 * supported systems, the default will be removed.
	 * 
	 * @param os
	 * @return config
	 */
	public ModuleConfiguration addSupportedSystem(OsName os) {
		if (supportedSystems.size() == 1
				&& supportedSystems.contains(OsName.SYSTEM_INDEPENDENT)) {
			supportedSystems.clear();
		}
		supportedSystems.add(os);
		return this;
	}

	/**
	 * Set of {@link OperatingSystem}s supported by the module
	 * 
	 * @return supported operating systems
	 */
	public HashSet<OperatingSystem.OsName> getSupportedSystems() {
		return supportedSystems;
	}
}

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

import model.OperatingSystem;
import storage.ConfigSaver;
import configuration.ModuleConfiguration;
import controller.Extractor;

/**
 * The abstract base class for all attribute extraction modules. The extractors
 * are calling these modules to extract the attributes.
 * 
 * @see Extractor
 */
public abstract class AbstractModule {
	public ModuleConfiguration config;

	public ModuleConfiguration getConfig() {
		return config;
	}

	public void setConfig(ModuleConfiguration config) {
		this.config = config;
	}

	public String moduleName;
	public String version = "0.1";

	/**
	 * By implementing this abstract method the name of the module is specified.
	 * Write a method body like {this.moduleName = "NAME";}.
	 */
	abstract public void setModuleName();

	abstract public void setVersion();

	abstract public String getModuleDescription();

	public AbstractModule() {
		setModuleName();
		setVersion();
		setConfig(new ModuleConfiguration(moduleName, version));
	}

	/**
	 * @return If the module will be called at the extraction process.
	 *         Unselected modules are inactive until they are reselected.
	 */
	public final boolean isSelected() {
		return this.getConfig().enabled;
	}

	/**
	 * Change the selection state of this module.
	 * 
	 * @param selected
	 *            setSelected(true) will enable the extraction of this module.
	 */
	public void setSelected(boolean selected) {
		this.getConfig().enabled = selected;
	}

	/**
	 * The given operating system specifies the kind of external application
	 * call and return value parsing. Returns true, if this algorithm is
	 * available for the given operating system. Default is
	 * <code>OperatingSystem.OsName.SYSTEM_INDEPENDENT</code>.
	 * 
	 * @return True, if the module works at the used operating system.
	 */
	public boolean supportsThisOS() {
		if (getConfig().getSupportedSystems().contains(
				OperatingSystem.getCurrentOS().genericName)
				|| getConfig().getSupportedSystems().contains(
						OperatingSystem.OsName.SYSTEM_INDEPENDENT))
			return true;
		return false;
	}

	public String getHash() {
		return ConfigSaver.hash(getConfig());
	}

	@Override
	public String toString() {
		return moduleName;
	}
}

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

import modules.AbstractModule;
import controller.ModuleController;

/**
 * Generic modules are used to provide a list of available modules without
 * configuration. They allow access to the module description without the need
 * to instantiate a module, and provide an instantiation mechanism for a real
 * {@link AbstractModule}.
 */
public class GenericModule {
	/**
	 * Reference to the belonging {@link AbstractModule}. Call
	 * <code>this.getInstance()</code> to get an instance of these module class.
	 */
	public Class<? extends AbstractModule> moduleClass;
	public String moduleName;
	public String moduleDescription;

	public GenericModule(Class<? extends AbstractModule> moduleClass,
			String moduleName, String moduleDescription) {
		this.moduleClass = moduleClass;
		this.moduleName = moduleName;
		this.moduleDescription = moduleDescription;
	}

	/**
	 * Get an {@link AbstractModule} instance that was represented by this
	 * generic module.
	 * 
	 * @return AbstractModule
	 */
	public AbstractModule getInstance() {
		return ModuleController.getModuleInstance(moduleClass);
	}

	/** Returns the name of the generic module. */
	@Override
	public String toString() {
		return moduleName;
	}
}

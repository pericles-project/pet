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

import static configuration.Log.EXCEPTION_LOGGER;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import model.GenericModule;
import modules.AbstractModule;

import org.reflections.Reflections;

/**
 * Manages the extraction modules and provides a set of generic modules to
 * instantiate new {@link AbstractModule}s.
 */
public class ModuleController {
	/**
	 * HashMap of all available {@link GenericModule}s. The hash String is the
	 * module name
	 */
	public static HashMap<String, GenericModule> genericModules = new HashMap<>();

	/**
	 * Constructor will initialize the set of available {@link GenericModule}s.
	 */
	public ModuleController() {

	}
	static {		Reflections reflections = new Reflections("modules");
	Set<Class<? extends AbstractModule>> moduleClasses;
	moduleClasses = reflections.getSubTypesOf(AbstractModule.class);
	Iterator<Class<? extends AbstractModule>> modulIterator;
	modulIterator = moduleClasses.iterator();
	while (modulIterator.hasNext()) {
		AbstractModule module;
		Class<? extends AbstractModule> next = modulIterator.next();
		if (!Modifier.isAbstract(next.getModifiers())) {
			module = getModuleInstance(next);
			GenericModule genericModule = new GenericModule(next,
					module.moduleName, module.getModuleDescription());
			genericModules.put(module.moduleName, genericModule);
		}
	}}

	/**
	 * Creates an {@link AbstractModule} instance from a class.
	 * 
	 * @param moduleClass
	 * @return created AbstractModule instance
	 */
	public static AbstractModule getModuleInstance(
			Class<? extends AbstractModule> moduleClass) {
		try {
			Constructor<?> c = moduleClass.getConstructor();
			return (AbstractModule) c.newInstance();
		} catch (Exception e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at getModuleInstance() for module: "
							+ moduleClass, e);
		}
		return null;
	}

	/**
	 * Returns a newly created {@link AbstractModule} instance. Looks up at the
	 * set of {@link GenericModule}s to find the right class to be instantiated,
	 * based on the module name.
	 * 
	 * @param moduleName
	 * @return instance of AbstractModule
	 */
	public static AbstractModule loadModule(String moduleName) {
		GenericModule genericModule = genericModules.get(moduleName);
		if (genericModule == null)
			return null;
		return genericModule.getInstance();
	}

	/**
	 * Returns a {@link GenericModule} with a specific name.
	 * 
	 * @param moduleName
	 *            the name of the GenericModule
	 * @return the GenericModule
	 */
	public static GenericModule getGenericModule(String moduleName) {
		return genericModules.get(moduleName);
	}

	/**
	 * Returns a collection of all available {@link GenericModule}s.
	 * 
	 * @return GenericModule collection
	 */
	public static Collection<GenericModule> getGenericModuleSet() {
		return genericModules.values();
	}
}

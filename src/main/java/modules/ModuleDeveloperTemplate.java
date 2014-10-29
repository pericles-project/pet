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

/**
 * This class serves as template for the development of a customized extraction
 * module to be used by the Pericles Extraction Tool. It is not used by the
 * application. Copy this template to another class and modify it, if you want
 * to develop an extraction module.
 */
public class ModuleDeveloperTemplate
/*
 * Each extraction module has to extend the abstract base class AbstractModule,
 * by extending one of its three sub-classes:
 */

/*
 * extends AbstractFileDependentModule
 * 
 * Use this base class, if you want to develop a module that extracts
 * file-dependent information. This is environment information that changes,
 * according to which file is investigated.
 */
/*
 * extends AbstractEnvironmentModule
 * 
 * Use this base class, if you want to develop a module that extracts
 * file-independent environment information. This type of information is
 * valid/similar for all files in the environment.
 */
/*
 * extends AbstractDaemonModule
 * 
 * Use this base class, if you want to develop a module that runs in the system
 * background to monitor the environment for the occurrence of an specific
 * event.
 */
{
	// //////////////// METHODS TO BE IMPLEMENTED: /////////////
	/*
	 * The following three methods have to be implemented for all kinds of
	 * modules:
	 */

	/*
	 * A meaningful name.
	 */
	// @Override
	// public void setModuleName() {
	// moduleName = "Your choosen module name";
	// }

	/*
	 * The version can be any meaningful version naming - developer choice
	 */
	// @Override
	// public void setVersion() {
	// version = "0.1";
	// }

	/*
	 * Returns a description string for this module. The description should
	 * explain what information is extracted or monitored, and how to configure
	 * the module correctly, in case the module needs a customized
	 * configuration.
	 */
	// @Override
	// public String getModuleDescription() {
	// return "meaningful description of the module";
	// }

	/*
	 * Only for file-dependent modules: This method defines the way how to
	 * extract the information from the computer system environment. The path
	 * argument is the path to the file where the information depends on. A
	 * FileFilter, which is configured in the ModuleConfiguration, is used
	 * automatically for file-dependent modules, to check if the module works on
	 * the passed file.
	 * 
	 * The method returns an ExtractionResult and has to set an Object of any
	 * type as result of this ExtractionResult with its setResults(Object
	 * resultObject) method. This can be for example a String, or an own defined
	 * class.
	 * 
	 * The model.OperatingSystem.getCurrentOS() class can be used to differ
	 * between operating systems and define customized commands for each
	 * operating system.
	 */
	// @Override
	// public ExtractionResult extractInformation(Path path) {
	// ExtractionResult result = new ExtractionResult(this);
	// Object customizedResultClass = ...
	// result.setResults(customizedResultClass);
	// return result;
	// }

	/*
	 * Only for file-independent modules: Mostly the same as the previously
	 * described method, but without the path parameter. This method doesn't use
	 * a FileFilter automatically, but it can refer to the config.fileFilter for
	 * example to filter for system-wide log file types.
	 */
	// @Override
	// public ExtractionResult extractInformation() {
	// ExtractionResult result = new ExtractionResult(this);
	// Object customizedResultClass = ...
	// result.setResults(customizedResultClass);
	// }

	/*
	 * Only for daemon modules: Daemon modules don't extract information, but
	 * they have to implement this observe() method for the environment
	 * monitoring. It is called periodically by the daemon threads run() method.
	 * 
	 * This method can use OperatingSystem and FileFilter as described above.
	 */
	// @Override
	// protected void observe() throws InterruptedException {
	// }

	// //////////////// MODULE CONSTRUCTOR AND CONFIGURATION /////////////

	/*
	 * At the constructor you can pre-define options of the ModuleConfiguration.
	 * For example you can define the supported operating systems and override
	 * the SYSTEM_INDEPENDENT default, or configure the fileFilter.
	 * 
	 * If you use a customized configuration (see below), you also have to call
	 * setConfig(customizedConfig) in the constructor, and you can move the
	 * configuration adjustments from the module constructor to the constructor
	 * of the customized ModuleConfiguration.
	 */
	// public MyModule() {
	// super();
	// config.addSupportedSystem(OsName.OS_X);
	// config.fileFilter.addInclusiveMediaType("text");
	// }

	// //////////////// CUSTOMIZED MODULE CONFIGURATION /////////////
	/*
	 * The following describes the use of a customized ModuleConfiguration,
	 * called MyModuleConfiguration. Each module has a configuration class of
	 * the type ModuleConfiguration, which is serialized to save the
	 * configuration for later tool starts.
	 */

	/*
	 * A customized configuration has to extend the class ModuleConfiguration.
	 * The following JSON annotation is needed:
	 */
	// @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
	// public static class MyModuleConfiguration extends ModuleConfiguration {
	/*
	 * You can define any types of files, which will be changeable by the user,
	 * for example:
	 */
	// public String directory;
	// boolean recursive true;

	/*
	 * Use the JSON annotation @JsonIgnore, to stop a variable from being
	 * serialized, and therewith saved at tool exit.
	 */
	// @JsonIgnore
	// boolean tmp;

	/*
	 * Override the following method, if you want to implement a customized
	 * configuration validation. It is called by the Extractor before an
	 * extraction and will cancel the extraction, if false.
	 */
	// @Ovrride
	// public boolean configurationIsValid() {
	// return validFlag;
	// }
	// } // END CLASS MyModuleConfiguration

	/*
	 * If you use a customized configuration, implement the default constructor
	 * of the module as follows:
	 */
	// public MyModule() {
	// super();
	// this.setConfig(new MyModuleConfiguration(moduleName, version, state));
	// }
}

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

import java.io.File;
import java.util.Map;

import model.OperatingSystem;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import configuration.ModuleConfiguration;

/**
 * @author fabio
 * 
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public class GeneralExecutableModuleConfig extends ModuleConfiguration {

	public String options;
	public String defaults;

	public GeneralExecutableModuleConfig() {
	}

	public GeneralExecutableModuleConfig(String moduleName, String version) {
		super(moduleName, version);
	}

	/**
	 * The absolute path for the current working directory for the command.
	 */
	public File workingDirectory;

	/**
	 * The generic command name to natively execute; or it's full path. You can
	 * specify OS-specific commands in the proper configuration parameters.
	 */
	public String commandNameOrPath;

	/**
	 * The OS X command name to natively execute; or it's full path.
	 */
	public String OSXcommandNameOrPath;

	/**
	 * The Linux command name to natively execute; or it's full path.
	 */
	public String LinuxcommandNameOrPath;

	/**
	 * The Windows command name to natively execute; or it's full path.
	 */
	public String WINcommandNameOrPath;
	public String BSDcommandNameOrPath;

	public String OSXoptions;
	public String Winoptions;
	public String Linuxoptions;

	// reduce command calls
	public boolean noHelp = false;

	/**
	 * TODO: (currently unimplemented) The regular expressions used to generate
	 * the outputs (name to identify result, expression)
	 */
	public Map<String, String> regEx;

	/**
	 * Timeout value for the process execution, after that timeout, the process
	 * will be killed. Default value is 10000 (10 seconds); set to -1 to give no
	 * limit to the execution.
	 */
	public int timeout = 10000;

	/**
	 * Value to wait between calls in hte iterative monitor
	 */
	public int waitBetweenCalls = 3000;

	/**
	 * Define here the commanline option to obtain the command help. Usually
	 * it's -h or --help. This will generate the default description for the
	 * module.
	 */
	public String helpOption = "-h";

	protected String currentOsOptions() {
		switch (OperatingSystem.getCurrentOS().genericName) {
		case OS_X:
			if (OSXoptions != null)
				return OSXoptions;
			break;
		case WINDOWS:
			if (Winoptions != null)
				return Winoptions;
			break;
		case LINUX:
			if (Linuxoptions != null)
				return Linuxoptions;
			break;
		default:
			break;
		}
		return options;
	}
}
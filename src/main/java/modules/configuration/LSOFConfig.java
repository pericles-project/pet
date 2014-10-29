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
package modules.configuration;

import model.OperatingSystem.OsName;
import modules.GeneralExecutableModuleConfig;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public class LSOFConfig extends GeneralExecutableModuleConfig {

	public String[] commandNames = null;
	public boolean monitorFiles = true;
	public String targetFolder = null;
	public int pid = 0;

	// / MAP PID TO PORCESS -- report relevant process info into the
	// LSOFData !
	// TODO: fix: when PID are reused, should notice and discard cache

	public LSOFConfig(String moduleName, String version) {
		super(moduleName, version);
		init();
	}

	public void init() {
		enabled = false;
		addSupportedSystem(OsName.BSD).addSupportedSystem(OsName.LINUX)
				.addSupportedSystem(OsName.OS_X)
				.addSupportedSystem(OsName.SOLARIS);
		commandNameOrPath = "lsof";
		recordEvents = true;
		waitBetweenCalls = 1000;
	}

	public LSOFConfig() {
		super("fiConfigModule", "1.0");
		init();
	}

	@Override
	protected String currentOsOptions() {
		String c = super.currentOsOptions();
		if (c == null) {
			c = "";
		}
		c += "-r " + (waitBetweenCalls / 1000);
		if (monitorFiles) {

			if (isNotEmpty(targetFolder)) {
				c += " +d " + targetFolder;
			}
		} else {
			c += " -i";
		}
		if (pid != 0) {
			c += " -p " + pid;
		} else if (commandNames != null) {
			for (String commandName : commandNames)
				if (isNotEmpty(commandName)) {
					c += " -c " + commandName;
				}
		}

		// System.out.println(c);
		return c;
	}

	private static boolean isNotEmpty(String s) {
		return s != null && s.trim().length() > 0;
	}
}
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

import model.ExtractionResult;
import configuration.ModuleConfiguration;

public class GeneralNativeCommandModuleEnv extends AbstractEnvironmentModule {
	protected final GeneralNativeCommandModule wrapped;

	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}

	@Override
	public boolean supportsThisOS() {
		return wrapped.supportsThisOS();
	}

	@Override
	public ExtractionResult extractInformation() {
		return wrapped.extractInformation(null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeneralNativeCommandModuleEnv) {
			GeneralNativeCommandModuleEnv mm = (GeneralNativeCommandModuleEnv) obj;
			return mm.wrapped.equals(obj);
		}
		return false;

	}

	@Override
	public String toString() {
		return wrapped.toString();
	}

	@Override
	public String getHash() {
		return wrapped.getHash();
	}

	public String getCurrentOsCommand() {
		return wrapped.getCurrentOsCommand();
	}

	@Override
	public String getModuleDescription() {
		return wrapped.getModuleDescription();
	}

	@Override
	public GeneralExecutableModuleConfig getConfig() {
		return wrapped.getConfig();
	}

	public GeneralNativeCommandModuleEnv() {
		wrapped = new GeneralNativeCommandModule();
		wrapped.setConfig(new GeneralExecutableModuleConfig(moduleName, version));
		wrapped.moduleName = moduleName;// =
										// "Create custom executable command (file independent)";
		wrapped.version = "0.1";
	}

	@Override
	public void setModuleName() {
		moduleName = "Create custom executable command (file independent)";
	}

	@Override
	public void setVersion() {
		version = "0.1";
	}

	@Override
	public void setConfig(ModuleConfiguration config) {
		if (wrapped != null) {
			wrapped.setConfig(config);
		}
	}

}

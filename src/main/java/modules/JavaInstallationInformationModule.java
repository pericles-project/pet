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

import java.util.Properties;

import model.ExtractionResult;
import model.KeyValueResult;

public class JavaInstallationInformationModule extends
		AbstractEnvironmentModule {

	@Override
	public void setModuleName() {
		this.moduleName = "Java installation information snapshot";
	}

	@Override
	public ExtractionResult extractInformation() {
		ExtractionResult moduleResults = new ExtractionResult(this);
		Properties systemProperties = System.getProperties();
		KeyValueResult result = new KeyValueResult("java installation result");
		result.add("java_class_path",
				systemProperties.getProperty("java.class.path"));
		result.add("java_home", systemProperties.getProperty("java.home"));
		result.add("java_vendor", systemProperties.getProperty("java.vendor"));
		result.add("java_vendorUrl",
				systemProperties.getProperty("java.vendor.url"));
		result.add("java_version", systemProperties.getProperty("java.version"));
		moduleResults.setResults(result);
		return moduleResults;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module extracts information about the current java installation:"
				+ "\n- java class path"
				+ "\n- java home"
				+ "\n- java vendor"
				+ "\n- java vendor url"
				+ "\n- java version"
				+ "\n\nThe module uses the Java native System.getProperties() method.";
		return description;
	}

	@Override
	public void setVersion() {
		this.version = "1.0";
	}

}

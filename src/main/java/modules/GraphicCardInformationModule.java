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
import model.KeyValueResult;
import model.OperatingSystem.OsName;
import configuration.Constants;

public class GraphicCardInformationModule extends GeneralNativeCommandModuleEnv {

	public GraphicCardInformationModule() {
		super();
		setModuleName();
		wrapped.moduleName = moduleName;
		GeneralExecutableModuleConfig config = new GeneralExecutableModuleConfig(
				moduleName, version);
		setConfig(config);

		config.defaults = config.options = "SPDisplaysDataType";
		config.commandNameOrPath = "";
		config.addSupportedSystem(OsName.OS_X);
		config.addSupportedSystem(OsName.WINDOWS);
		config.WINcommandNameOrPath = "dxdiag";
		config.OSXcommandNameOrPath = "system_profiler";
		config.OSXoptions = "SPDisplaysDataType";
		config.Winoptions = "/whql:off /x " + Constants.OUTPUT_DIRECTORY
				+ "dxdiag" + System.currentTimeMillis() + ".xml";
		config.options = "";
		config.helpOption = "-h";
		config.noHelp = true;

		// for msinfo "%CommonProgramFiles%\Microsoft
		// Shared\MSInfo\MSINFO32.EXE" /report .\%COMPUTERNAME%.txt /categories
		// +all
	}

	@Override
	public void setModuleName() {
		moduleName = "Graphic card information module";
	}

	@Override
	public ExtractionResult extractInformation() {
		ExtractionResult result = new ExtractionResult(this);
		try {
			ExtractionResult extractionResult = super.extractInformation();
			KeyValueResult keyValueResult = (KeyValueResult) extractionResult.results;
			result.setResults(keyValueResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	@Override
	public String getModuleDescription() {
		return "This module will return grapic card information using the native system_profiler command in OSX or dxdiag on Windows\n\n\n"
				+ super.getModuleDescription();
	}

	public static void main(String[] args) {
		GraphicCardInformationModule f = new GraphicCardInformationModule();
		System.out.println(f.getModuleDescription());
	}
}

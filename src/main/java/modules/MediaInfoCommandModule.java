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

import static configuration.Log.MODULE_LOGGER;

import java.nio.file.Path;
import java.util.logging.Level;

import model.ExtractionResult;
import model.KeyValueResult;

public class MediaInfoCommandModule extends GeneralNativeCommandModule {
	public static final String WINDOWS_BIN_FILE_EXE = "nativeTools/MediaInfo_CLI_0.7.65_Windows_i386/MediaInfo.exe";
	public static final String OSX_BIN_FILE_EXE = "nativeTools/mediainfo";

	public MediaInfoCommandModule() {
		GeneralExecutableModuleConfig config = new GeneralExecutableModuleConfig(
				moduleName, version);
		this.setConfig(config);
		config.defaults = config.options = "-f --Output=XML";
		config.commandNameOrPath = "mediainfo";
		config.WINcommandNameOrPath = WINDOWS_BIN_FILE_EXE;
		config.OSXcommandNameOrPath = OSX_BIN_FILE_EXE;
		config.helpOption = "--help";
	}

	@Override
	public void setModuleName() {
		moduleName = "MediaInfoCommand";
	}

	@Override
	public ExtractionResult extractInformation(Path path) {
		ExtractionResult result = new ExtractionResult(this);
		try {
			ExtractionResult extractionResult = super.extractInformation(path);
			KeyValueResult keyValueResult = (KeyValueResult) extractionResult.results;
			result.setResults(keyValueResult);
		} catch (Exception e) {
			MODULE_LOGGER.log(Level.SEVERE,
					"Exception in MediaInfoCommandModule", e);
		}
		return result;
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	@Override
	public String getModuleDescription() {
		return "This module will extract metadata from video file using the mediainfo application in all supported platforms.\n\n Here follows a description of the avaliable options: \n\n\n"
				+ super.getModuleDescription();
	}

	public static void main(String[] args) {
		MediaInfoCommandModule f = new MediaInfoCommandModule();
		System.out.println(f.getModuleDescription());
	}
}

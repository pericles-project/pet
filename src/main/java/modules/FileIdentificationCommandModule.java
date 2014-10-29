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

import java.nio.file.Files;
import java.nio.file.Path;

import model.ExtractionResult;
import model.KeyValueResult;
import model.OperatingSystem.OsName;
import utility.FileUtils;

public class FileIdentificationCommandModule extends GeneralNativeCommandModule {
	public static final String WINDOWS_BIN_FILE_EXE = "nativeTools/file_utility_windows/bin/file.exe";

	public FileIdentificationCommandModule() {
		GeneralExecutableModuleConfig config = new GeneralExecutableModuleConfig(
				moduleName, version);

		this.setConfig(config);
		config.commandNameOrPath = "file";
		config.defaults = config.options = "--mime-type -b";
		config.WINcommandNameOrPath = WINDOWS_BIN_FILE_EXE;
		config.addSupportedSystem(OsName.SYSTEM_INDEPENDENT);

	}

	@Override
	public void setModuleName() {
		moduleName = "File identification";
	}

	@Override
	public ExtractionResult extractInformation(Path path) {

		ExtractionResult r = new ExtractionResult(this);

		try {
			ExtractionResult extractionResult = super.extractInformation(path);
			KeyValueResult t = (KeyValueResult) extractionResult.results;
			String mimeTypeKey = t.results.remove("fullOutput");
			if (mimeTypeKey != null) {
				t.add("mime_type", mimeTypeKey.trim());
			}
			t.add("mime_type_java", Files.probeContentType(path));
			t.add("mime_type_tika",
					ApacheTikaExtractionModule.tika.detect(path.toFile()));
			if (t.results.get("mime_type") == null) {
				t.add("mime_type", t.results.get("mime_type_tika"));
			}
			r.setResults(t);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return r;
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	@Override
	public String getModuleDescription() {

		return super.getModuleDescription();

	}

	public static void main(String[] args) {
		FileIdentificationCommandModule f = new FileIdentificationCommandModule();
		FileUtils.getCurrentJarFolder(f.getClass());

		System.out.println(f.getModuleDescription());

		System.out.println(f.getModuleDescription());
	}
}

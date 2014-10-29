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

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

import model.ExtractionResult;
import model.KeyValueResult;

public class FileStoreModule extends AbstractFileDependentModule {
	@Override
	public ExtractionResult extractInformation(Path file) {
		ExtractionResult moduleResults = new ExtractionResult(this);
		try {
			KeyValueResult properties = new KeyValueResult("File store results");
			FileStore storeAttributes = Files.getFileStore(file);
			properties.add("total_space", "" + storeAttributes.getTotalSpace());
			properties.add("unallocated_space",
					"" + storeAttributes.getUnallocatedSpace());
			properties.add("is_readonly", "" + storeAttributes.isReadOnly());
			properties.add("file_store_name", storeAttributes.name());
			properties.add("file_store_type", storeAttributes.type());
			moduleResults.setResults(properties);
		} catch (IOException e) {
			MODULE_LOGGER.severe("IOException in FileStoreModule.");
		}
		return moduleResults;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module extracts the following information about a file:"
				+ "\n- total space used"
				+ "\n- unallocated space"
				+ "\n- is readonly? (true/false)"
				+ "\n- file name"
				+ "\n- file type"
				+ "\n\nThe module uses the Java native Files.getFileStore() method.";
		return description;
	}

	@Override
	public void setModuleName() {
		moduleName = "File store information (java.nio.file)";
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}
}

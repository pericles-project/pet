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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;

import model.ExtractionResult;
import model.KeyValueResult;
import model.OperatingSystem;

public class PosixModule extends AbstractFileDependentModule {
	@Override
	public ExtractionResult extractInformation(Path file) {
		ExtractionResult moduleResults = new ExtractionResult(this);
		try {
			KeyValueResult properties = new KeyValueResult("PosixResults");
			PosixFileAttributes posixAttributes = Files.readAttributes(file,
					PosixFileAttributes.class);
			properties.add("last_modified", posixAttributes.lastModifiedTime()
					.toString());
			properties.add("last_access", posixAttributes.lastAccessTime()
					.toString());
			properties.add("creation_time", posixAttributes.creationTime()
					.toString());
			String fileType = "other";
			if (posixAttributes.isRegularFile()) {
				fileType = "regularFile";
			} else if (posixAttributes.isDirectory()) {
				fileType = "testDirectory";
			} else if (posixAttributes.isSymbolicLink()) {
				fileType = "symbolicLink";
			}
			properties.add("file_type", fileType);
			properties.add("file_size", "" + posixAttributes.size());
			properties.add("file_key", posixAttributes.fileKey().toString());
			properties.add("file_group_owner", posixAttributes.group()
					.getName());
			properties.add("file_owner", posixAttributes.owner().getName());
			// attributes.permissions();
			moduleResults.setResults(properties);
		} catch (IOException e) {
			MODULE_LOGGER.severe("IOException in PosixModule.");
		}
		return moduleResults;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module extracts the following Posix file information:"
				+ "\n- last modified time"
				+ "\n- last access time"
				+ "\n- creation time"
				+ "\n- file type"
				+ "\n- type (regular file / directory / symbolic link)"
				+ "\n- file size"
				+ "\n- file key"
				+ "\n- file group owner"
				+ "\n- file owner"
				+ "\n\nThe module uses Javas native Files.readAttributes() method.";
		return description;
	}

	@Override
	public void setModuleName() {
		moduleName = "Posix file information monitoring";
	}

	public PosixModule() {
		getConfig().addSupportedSystem(OperatingSystem.OsName.BSD);
		getConfig().addSupportedSystem(OperatingSystem.OsName.LINUX);
		getConfig().addSupportedSystem(OperatingSystem.OsName.SOLARIS);
		getConfig().addSupportedSystem(OperatingSystem.OsName.OS_X);
	}

	@Override
	public void setVersion() {
	}
}
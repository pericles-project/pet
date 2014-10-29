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

import java.nio.file.Path;

import model.ExtractionResult;
import model.KeyValueResult;

import org.hyperic.sigar.FileInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarFileInfo extends AbstractFileDependentModule {
	@Override
	public ExtractionResult extractInformation(Path file) {
		ExtractionResult attributeList = new ExtractionResult(this);
		try {
			Sigar sigar = new Sigar();
			FileInfo fileInfo = sigar.getFileInfo("" + file);
			KeyValueResult properties = new KeyValueResult("SigarFileInfo");
			properties.add("file_path", "" + file);
			properties.add("file_mode", "" + fileInfo.getMode());
			properties.add("file_permisson", fileInfo.getPermissionsString());
			properties.add("file_name", fileInfo.getName());
			properties.add("file_type", fileInfo.getTypeString());
			properties.add("file_hash_code", "" + fileInfo.hashCode());
			properties.add("file_modified", "" + fileInfo.modified());
			attributeList.setResults(properties);
			sigar.close();
		} catch (SigarException e) {
			e.printStackTrace();
		}
		this.getModuleDescription();
		return attributeList;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module extracts the following information about a file:"
				+ "\n- path"
				+ "\n- mode"
				+ "\n- permissions"
				+ "\n- name"
				+ "\n- type"
				+ "\n- hash code"
				+ "\n- modified"
				+ "\n\nThe module uses the sigar library.";
		return description;
	}

	@Override
	public void setModuleName() {
		moduleName = "File store information (sigar)";
	}

	@Override
	public void setVersion() {
	}
}

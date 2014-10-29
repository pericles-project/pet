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
package model;

import static utility.FileUtils.fileExists;
import static utility.FileUtils.isDirectory;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This data structure represents a file with its belonging extracted
 * information.
 */
public class Part extends ExtractionResultCollection {
	/** Name of the file */
	public String fileName;
	@JsonIgnore
	private Path file;
	/** Path to the file */
	public String path;

	/**
	 * Empty constructor needed for serialization. Don't use this constructor
	 * for something else.
	 */
	@Deprecated
	public Part() {
	}

	private Part(String profileUUID) {
		super(profileUUID);
	}

	/**
	 * Private constructor and construction method to check, if the passed file
	 * exists.
	 * 
	 * @param file
	 *            path to the file represented by this Part
	 * @param profileuuId
	 *            UUID where the Part should belong to
	 * @return part if everything is OK, and null otherwise
	 */
	public static Part create(Path file, String profileuuId) {
		if (!fileExists(file) || isDirectory(file))
			return null;
		Part part = new Part(profileuuId);
		part.setFile(file.toAbsolutePath());
		part.fileName = part.getFile().getFileName().toString();
		return part;
	}

	/**
	 * Prints name of the parts file
	 */
	@Override
	public String toString() {
		return this.getFile().getFileName().toString();
	}

	/**
	 * get the parts string path
	 * 
	 * @return string path to the file represented by the part
	 */
	@JsonIgnore
	public String getPath() {
		return path;
	}

	/**
	 * get the parts Path
	 * 
	 * @return Path to the file represented by the part
	 */
	public Path getFile() {
		return file;
	}

	private void setFile(Path file) {
		this.file = file;
		path = file.toAbsolutePath().toString();
	}
}

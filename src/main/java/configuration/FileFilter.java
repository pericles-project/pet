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
package configuration;

import static configuration.Log.EXCEPTION_LOGGER;
import static configuration.Log.MODULE_LOGGER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.logging.Level;

import javax.activation.MimetypesFileTypeMap;

import modules.ApacheTikaExtractionModule;

public class FileFilter extends MimetypesFileTypeMap {
	/** pattern filtering for file extensions */
	private String fileExtensionFilter;
	// such as (?i)(.*?)\.(jpg|gif|doc|pdf)$

	private final HashSet<String> exclusiveMimeTypes = new HashSet<String>();
	private final HashSet<String> inclusiveMimeTypes = new HashSet<String>();
	private final HashSet<String> exclusiveMediaTypes = new HashSet<String>();
	private final HashSet<String> inclusiveMediaTypes = new HashSet<String>();

	public FileFilter() {
	}

	/**
	 * Checks whether the file type should be extracted, or filtered out. To be
	 * used at the <code>AbstractModule.extractInformation(Path path)</code>
	 * procedure.
	 * 
	 * @param path
	 * @return boolean if to be extracted
	 */
	public boolean fileTypeSupported(Path path) {
		if (inclusiveMimeTypes.size() > 0 || inclusiveMediaTypes.size() > 0
				|| exclusiveMimeTypes.size() > 0
				|| exclusiveMediaTypes.size() > 0) {
			try {
				String mime = Files.probeContentType(path);
				if (mime == null) {
					mime = ApacheTikaExtractionModule.tika
							.detect(path.toFile());
				}
				if (mime == null) {
					MODULE_LOGGER.info("Couldn't get mime-type for " + path
							+ "\nExtract without ");
					return true;
				}
				// Use of the file extension filter:
				if (!fileExtensionSupported(path.toString())) {
					return false;
				}
				String media = mime.split("/")[0];
				if (inclusiveMediaTypes.size() > 0
						|| inclusiveMimeTypes.size() > 0) {
					if (inclusiveMediaTypes.contains(media)
							|| inclusiveMimeTypes.contains(mime))
						return true;
					else
						return false;
				} else if (exclusiveMediaTypes.contains(media)
						|| exclusiveMimeTypes.contains(mime))
					return false;
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception in FileFilter.fileSupported() check.", e);
			}
		}
		return true;
	}

	/**
	 * Uses the file extension filter on the string path.
	 * 
	 * @param filePath
	 *            string path to the file
	 * @return true, if the pattern matches
	 */
	public boolean fileExtensionSupported(String filePath) {
		if (fileExtensionFilter == null
				|| fileExtensionFilter.trim().length() == 0)
			return true;
		if (filePath.matches(fileExtensionFilter))
			return true;
		return false;
	}

	/**
	 * Inclusive: Only files of this type will be considered! If an inclusive
	 * type is added, the exclusive types won't be regarded anymore.
	 * 
	 * For example: text/plain, image/gif, video/mpeg, audio/mid,
	 * application/msword
	 * 
	 * @param mimeType
	 */
	public void addInclusiveMimeType(String mimeType) {
		if (!mimeType.equals("")) {
			inclusiveMimeTypes.add(mimeType);
		}
	}

	/**
	 * Exclusive: Files of this type won't be considered! If any inclusive type
	 * is specified, the exclusive types won't be regarded anymore.
	 * 
	 * For example: text/plain, image/gif, video/mpeg, audio/mid,
	 * application/msword
	 * 
	 * @param mimeType
	 */
	public void addExclusiveMimeType(String mimeType) {
		if (!mimeType.equals("")) {
			exclusiveMimeTypes.add(mimeType);
		}
	}

	/**
	 * Inclusive: Only files of this type will be considered! If an inclusive
	 * type is added, the exclusive types won't be regarded anymore.
	 * 
	 * For example: text, image, video, audio, application
	 * 
	 * @param mediaType
	 */
	public void addInclusiveMediaType(String mediaType) {
		if (!mediaType.equals("")) {
			inclusiveMediaTypes.add(mediaType);
		}
	}

	/**
	 * Exclusive: Files of this type won't be considered! If any inclusive type
	 * is specified, the exclusive types won't be regarded anymore.
	 * 
	 * For example: text, image, video, audio, application
	 * 
	 * @param mediaType
	 */
	public void addExclusiveMediaType(String mediaType) {
		if (!mediaType.equals("")) {
			exclusiveMediaTypes.add(mediaType);
		}
	}

	/**
	 * Add a file extension filter: a regular expression that filters file
	 * extensions
	 * 
	 * @param fileExtensionFilter
	 */
	public void addFileExtensionFilter(String fileExtensionFilter) {
		this.fileExtensionFilter = fileExtensionFilter;
	}

	public HashSet<String> getInclusiveMimeType() {
		return this.inclusiveMimeTypes;
	}

	public HashSet<String> getInclusiveMediaType() {
		return this.inclusiveMediaTypes;
	}

	public HashSet<String> getExclusiveMediaType() {
		return this.exclusiveMediaTypes;
	}

	public HashSet<String> getExclusiveMimeType() {
		return this.exclusiveMimeTypes;
	}

	public String getFileExtensionFilter() {
		return fileExtensionFilter;
	}
}

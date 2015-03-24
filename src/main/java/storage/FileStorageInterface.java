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
package storage;

import static configuration.Log.EXCEPTION_LOGGER;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import model.Environment;
import model.ExtractionResultCollection;
import model.Part;
import utility.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import configuration.Constants;

/**
 * Class to start Elasticsearch queries. Can be replaced by another storage
 * backend.
 */
public class FileStorageInterface extends GeneralStorage {

	// maps file paths to UUIDs used to store the extractions.
	private Map<String, String> pathToId;
	// maps to last used environment file number
	private final Map<String, Integer> lastEnv = new HashMap<String, Integer>();
	// maps to last used file part (profile UUID+ file UUID) tp file number
	private final Map<String, Integer> lastFile = new HashMap<String, Integer>();

	
	public FileStorageInterface() {
		initPathToId();
	}

	@SuppressWarnings("unchecked")
	public void initPathToId() {
		try {
			// we must store and read the mapping of file paths to ids
			// (althought it could be reconstructed from the extraction data
			// themselves)
			pathToId = initMapper().readValue(
					new File(Constants.OUTPUT_DIRECTORY, "filetoidmap.json"),
					HashMap.class);
		} catch (IOException e) {
			pathToId = new HashMap<String, String>();
		}
	}
	

	@Override
	public void finalize() {
		try {
			// we must store and read the mapping of file paths to ids
			// (althought it could be reconstructed from the extraction data
			// themselves)
			initMapper().writeValue(
					new File(Constants.OUTPUT_DIRECTORY, "filetoidmap.json"),
					pathToId);
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE, "Exception at finalize()", e);
		}
	}

	@Override
	public void save(String collection, String profileUUID, String type,
			String path) {
		File out = new File(Constants.OUTPUT_DIRECTORY, profileUUID);
		FileUtils.createDirectory(out.toPath());
		if (path == null) {
			int le = 0;
			if (lastEnv.containsKey(profileUUID)) {
				le = lastEnv.get(profileUUID);
				le++;
				lastEnv.put(profileUUID, le);
			} else {
				String[] envs = out.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if (name.startsWith("environment"))
							return true;
						return false;
					}
				});
				if (envs.length > 0) {
					Arrays.sort(envs);
					le = Integer
							.parseInt(envs[envs.length - 1].substring(
									"environment".length(),
									"environment".length() + 8));
					le++;
				} else {
					le = 0;
				}
				lastEnv.put(profileUUID, le);
			}
			File env = new File(out, "environment" + String.format("%08d", le)
					+ ".json");
			try {
				org.apache.commons.io.FileUtils.writeStringToFile(env,
						collection);
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE, "Exception at save", e);
			}
		} else {
			String id = pathToId.get(path);
			if (id == null) {
				id = UUID.randomUUID().toString();
				pathToId.put(path, id);
			}
			File partOut = new File(out, id);
			FileUtils.createDirectory(partOut.toPath());

			int le;
			if (lastFile.containsKey(profileUUID + id)) {
				le = lastFile.get(profileUUID + id);
				le++;
				lastFile.put(profileUUID + id, le);
			} else {
				String[] envs = partOut.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if (name.startsWith("file"))
							return true;
						return false;
					}
				});
				if (envs.length > 0) {
					Arrays.sort(envs);
					String string = envs[envs.length - 1]
							.replaceAll("file", "").replaceAll(".json", "");
					le = Integer.parseInt(string);
					le++;
				} else {
					le = 0;
				}
				lastFile.put(profileUUID + id, le);
			}
			File env = new File(partOut, "file" + String.format("%08d", le)
					+ ".json");
			try {
				org.apache.commons.io.FileUtils.writeStringToFile(env,
						collection);
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE, "Exception at save", e);
			}
		}
	}

	@Override
	public void deleteAllMetadata() {
		FileUtils.deleteDirectory(Constants.OUTPUT_DIRECTORY);
		FileUtils.createDirectory(Constants.OUTPUT_DIRECTORY);
	}

	/**
	 * Logic to get the data to fill the InformationChangeTable.
	 * 
	 * @return data to fill the InformationChangeTable
	 */
	@Override
	public String[] getRawResults(ExtractionResultCollection coll) {
		LinkedList<String> res = new LinkedList<String>();

		File out = new File(Constants.OUTPUT_DIRECTORY, coll.profileUUID);
		FileUtils.createDirectory(out.toPath());
		if (coll instanceof Part) {
			if (pathToId.size()==0)
				initPathToId();
			Part part = (Part) coll;
			String path = part.getPath();
			String id = pathToId.get(path);
			if (id == null)
				return new String[0];
			File partOut = new File(out, id);
			FileUtils.createDirectory(partOut.toPath());
			String[] envs = partOut.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith("file"))
						return true;
					return false;
				}
			});
			Arrays.sort(envs);
			for (String e : envs) {
				try {
					res.add(org.apache.commons.io.FileUtils
							.readFileToString(new File(partOut, e)));
				} catch (IOException e1) {
					EXCEPTION_LOGGER.log(Level.SEVERE,
							"Exception at getRawResults", e1);
				}
			}
		} else if (coll instanceof Environment) {
			String[] envs = out.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith("environment"))
						return true;
					return false;
				}
			});
			Arrays.sort(envs);
			for (String e : envs) {
				try {
					res.add(org.apache.commons.io.FileUtils
							.readFileToString(new File(out, e)));
				} catch (IOException e1) {
					EXCEPTION_LOGGER.log(Level.SEVERE,
							"Exception at getRawResults", e1);
				}
			}
		} else
			return null;
		return res.toArray(new String[0]);
	}

	private static ObjectMapper initMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}
}

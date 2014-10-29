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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;

import model.ExtractionResult;
import model.Part;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import configuration.ModuleConfiguration;
import controller.ExtractionController;

/**
 * This class saves the configuration to a file by closing the tool; and loads
 * it back at a new start of the tool. The configuration save and load is
 * initiated by the controller. Especially the information with modules ares
 * selected and unselected will be saved.
 * 
 * @see ExtractionController
 */
public class ConfigSaver {
	private ConfigSaver() {
	};

	/**
	 * Saves a set of Part classes to file.
	 * 
	 * @param data
	 * @param fileName
	 */
	public static void savePartSet(HashSet<Part> data, String fileName) {
		HashSet<Path> paths = new HashSet<Path>();
		for (Part part : data) {
			paths.add(part.getFile());
		}
		savePathSet(paths, fileName);
	}

	/**
	 * Saves a set of Paths to file.
	 * 
	 * @param paths
	 * @param fileName
	 */
	public static void savePathSet(HashSet<Path> paths, String fileName) {
		try {
			FileWriter fWriter = new FileWriter(fileName);
			BufferedWriter writer = new BufferedWriter(fWriter);
			for (Path path : paths) {
				writer.write(path + "\n");
			}
			writer.close();
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE, "Exception at savePathSet()", e);
		}
	}

	/**
	 * Loads a set of Paths from file.
	 * 
	 * @param fileName
	 * @return Path set
	 */
	public static HashSet<Path> loadPathSet(String fileName) {
		HashSet<Path> data = new HashSet<Path>();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNext()) {
				Path path = Paths.get(scanner.nextLine());
				data.add(path);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE, "Exception at loadPathSet()", e);
		}
		return data;
	}

	/**
	 * Loads a ModuleConfiguration from File
	 * 
	 * @param source
	 * @return ModuleConfiguration
	 */
	public static ModuleConfiguration loadModuleConfiguration(File source) {
		try {
			return loadModuleConfiguration(new FileInputStream(source));
		} catch (FileNotFoundException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at loadComplexConfiguration()", e);
			return null;
		}
	}

	/**
	 * Load a ModuleConfiguration from JSON file.
	 * 
	 * @param source
	 * @return configured ModuleConfiguration class
	 */
	public static ModuleConfiguration loadModuleConfiguration(InputStream source) {
		try {
			ModuleConfiguration config = initMapper().readValue(source,
					ModuleConfiguration.class);
			return config;
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at loadComplexConfiguration()", e);
			return null;
		}
	}
	
	/**
	 * Load a ModuleConfiguration from JSON file.
	 * 
	 * @param source
	 * @return configured ModuleConfiguration class
	 */
	public static ModuleConfiguration loadModuleConfiguration(String source) {
		try {
			ModuleConfiguration config = initMapper().readValue(source,
					ModuleConfiguration.class);
			return config;
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at loadComplexConfiguration()", e);
			return null;
		}
	}

	/**
	 * Saves a ModuleConfiguration to file.
	 * 
	 * @param destination
	 * @param conf
	 */
	public static void saveModuleConfiguration(File destination,
			ModuleConfiguration conf) {
		try {
			saveModuleConfiguration(new FileOutputStream(destination), conf);
		} catch (FileNotFoundException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at saveComplexConfiguration()", e);
		}
	}

	/**
	 * Calculates a MD5 hash String for a ModuleConfiguration.
	 * 
	 * @param config
	 * @return MD5 hash String
	 */
	public static String hash(ModuleConfiguration config) {
		try {
			if (config != null) {
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream(
						1200);
				initMapper().writeValue(byteArray, config);
				HashFunction hashFunction = Hashing.md5();
				HashCode hashCode = hashFunction.newHasher()
						.putBytes(byteArray.toByteArray()).hash();
				return hashCode.toString();
			}
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE, "Exception at hash()", e);
		}
		return "";
	}

	/**
	 * Saves a ModuleConfiguration to an OutputStream
	 * 
	 * @param destiantion
	 * @param config
	 */
	public static void saveModuleConfiguration(OutputStream destiantion,
			ModuleConfiguration config) {
		try {
			if (config != null) {
				initMapper().writeValue(destiantion, config);
			}
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at saveComplexConfiguration()", e);
		}
	}
	public static String saveModuleConfiguration(ModuleConfiguration config) throws JsonProcessingException  {

		if (config != null) {
			return initMapper().writeValueAsString(config);

		}
		return null;
	}
	
	public static String getSerializedResult(ExtractionResult result) throws JsonProcessingException  {
		if (result != null) {
			return initMapper().writeValueAsString(result);
		}
		return null;
	}
	
	private static ObjectMapper initMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		}
		return mapper;
	}
	static ObjectMapper mapper ;
}

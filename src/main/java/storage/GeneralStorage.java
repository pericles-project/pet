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
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import javax.swing.tree.DefaultMutableTreeNode;

import model.ExtractionResult;
import model.ExtractionResultCollection;
import model.KeyValueResult;
import model.Part;
import model.Profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import configuration.Constants;
import configuration.Log;

public abstract class GeneralStorage {

	/**
	 * Default method to extract results from a collection, must be implemented
	 * by the specific storage interface
	 * 
	 * @param coll
	 * @return raw string results
	 */
	public abstract String[] getRawResults(ExtractionResultCollection coll);

	public abstract void save(String collection, String profileUUID,
			String type, String path) throws Exception;

	@Override
	public abstract void finalize();

	public abstract void deleteAllMetadata();

	/**
	 * This method is called by the Extractor, if one part was added to the
	 * profile and only this part was extracted.
	 * 
	 * 1. save and clear the result of profile.getEnvironment(); 2. save and
	 * clear the results of the part
	 * 
	 * @param coll
	 * @return extraction results belonging to a {@link model.Part} or an
	 *         {@link model.Environment}.
	 */
	public ExtractionResultCollection[] getResults(
			ExtractionResultCollection coll) {
		String[] raw = getRawResults(coll);
		LinkedList<ExtractionResultCollection> l = convertRawResults(raw);
		return l.toArray(new ExtractionResultCollection[0]);

	}

	public void storeEventData(byte[] lines) {

		File file = new File(Constants.EVENT_STORAGE_FILE);

		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			Files.write(file.toPath(), lines, StandardOpenOption.APPEND,
					StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		} catch (IOException e) {
			Log.EXCEPTION_LOGGER.log(Level.SEVERE, "Error writing file", e);
		}
	}

	public List<String> readEventData() {

		File file = new File(Constants.EVENT_STORAGE_FILE);
		try {
			if (!file.exists())
				return null;
			return Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			Log.EXCEPTION_LOGGER.log(Level.SEVERE, "Error reading file", e);
			return null;
		}

	}

	public String storeExternalData(byte[] data) {
		String id = UUID.randomUUID().toString();
		File out = new File(Constants.OUTPUT_DIRECTORY, id);
		try {
			Files.write(out.toPath(), data, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.EXCEPTION_LOGGER.log(Level.SEVERE, "Error writing file", e);
		}
		return id;
	}

	public byte[] readExternalData(String id) {
		File out = new File(Constants.OUTPUT_DIRECTORY, id);
		try {
			return Files.readAllBytes(out.toPath());
		} catch (IOException e) {
			Log.EXCEPTION_LOGGER.log(Level.SEVERE, "Error reading file", e);
		}
		return null;
	}

	private LinkedList<ExtractionResultCollection> convertRawResults(
			String[] raw) {
		LinkedList<ExtractionResultCollection> l = new LinkedList<ExtractionResultCollection>();
		for (String r : raw) {
			try {
				l.add(getExtractionResultCollection(r));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return l;
	}

	public GeneralStorage() {
	}

	public void save(Part part, Profile profile) throws Exception {
		ObjectMapper mapper = initMapper();
		String collectionAsString;
		if (part.extractionResults.size() > 0) {
			part.sortResults();
			collectionAsString = mapper.writeValueAsString(part);
			save(collectionAsString, profile.getUUID(), "file-dependent",
					part.getPath());
			part.extractionResults.clear();
		}
		if (profile.getEnvironment().extractionResults.size() > 0) {
			profile.getEnvironment().sortResults();
			collectionAsString = mapper.writeValueAsString(profile
					.getEnvironment());
			save(collectionAsString, profile.getUUID(), "environment", null);
			profile.getEnvironment().extractionResults.clear();
		}

	}

	/**
	 * This method is called by the Extractor, if all parts of the profile were
	 * extracted.
	 * 
	 * 1. save and clear the result of profile.getEnvironment(); 2. save and
	 * clear the results of each part of the profile
	 * 
	 * @param profile
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	public void save(Profile profile) throws Exception {
		ObjectMapper mapper = initMapper();
		String collectionAsString;
		for (Part part : profile.getParts()) {
			if (part.extractionResults.size() > 0) {
				collectionAsString = mapper.writeValueAsString(part);
				save(collectionAsString, profile.getUUID(), "file-dependent",
						part.getPath());
				part.extractionResults.clear();
			}
		}
		if (profile.getEnvironment().extractionResults.size() > 0) {
			collectionAsString = mapper.writeValueAsString(profile
					.getEnvironment());
			save(collectionAsString, profile.getUUID(), "environment", null);
			profile.getEnvironment().extractionResults.clear();
		}
	}

	/**
	 * Logic to create the InformationTree from ExtractionResultCollection.
	 * 
	 * @param coll
	 * @param root
	 */
	public void createResultTree(ExtractionResultCollection coll,
			DefaultMutableTreeNode root) {
		String[] response = getRawResults(coll);
		searchAndAddHits(response, root);
	}

	/**
	 * Logic to get the header for the InformationChangeTable
	 * 
	 * @param coll
	 * @param moduleName
	 * @return header
	 */
	public String[] getResultsHeader(ExtractionResultCollection coll,
			String moduleName) {
		String[] response = getRawResults(coll);
		return getArrayFromList(getTableHeaderStrings(moduleName, response));
	}

	/**
	 * Logic to get the names of all used modules for an Environment, to fill
	 * the GUI ComboBox.
	 * 
	 * @param collection
	 * @return name set of all used modules
	 */
	public HashSet<String> getAllUsedModules(
			ExtractionResultCollection collection) {
		HashSet<String> moduleNames = new HashSet<String>();
		for (String hit : getRawResults(collection)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode jsonRoot = mapper.readTree(hit);
				moduleNames.addAll(recursiveGetModules(jsonRoot));
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at creation of JSON tree", e);
			}
		}
		return moduleNames;

	}

	private static ObjectMapper initMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

	private static HashSet<String> recursiveGetModules(JsonNode jsonParent) {
		HashSet<String> moduleNames = new HashSet<String>();
		Iterator<Entry<String, JsonNode>> jsonFields = jsonParent.fields();
		while (jsonFields.hasNext()) {
			Entry<String, JsonNode> jsonField = jsonFields.next();
			if (jsonField.getKey().equals("moduleName")) {
				moduleNames.add(jsonField.getValue().asText());
			}
		}
		Iterator<JsonNode> jsonChildren = jsonParent.elements();
		while (jsonChildren.hasNext()) {
			moduleNames.addAll(recursiveGetModules(jsonChildren.next()));
		}
		return moduleNames;
	}

	private static ArrayList<String> getTableHeaderStrings(String moduleName,
			String[] response) {
		ArrayList<String> headerStrings = new ArrayList<String>();
		headerStrings.add("Extraction Date");
		for (String hit : response) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode jsonRoot = mapper.readTree(hit);
				headerStrings.addAll(getHeader(moduleName, jsonRoot));
				return headerStrings;
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at creation of JSON tree", e);
			}
		}
		return null;
	}

	private static String[] getArrayFromList(ArrayList<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	private static ArrayList<ArrayList<String>> getTableData(String moduleName,
			String[] response) {
		ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
		for (String hit : response) {
			ArrayList<String> column = new ArrayList<String>();
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode jsonRoot = mapper.readTree(hit);
				column = getColumn(moduleName, jsonRoot);
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at creation of JSON tree", e);
			}
			if (column.size() > 0) {
				tableData.add(column);
			}
		}
		return tableData;
	}

	private static ArrayList<String> getHeader(String moduleName,
			JsonNode jsonParent) {
		ArrayList<String> header = new ArrayList<String>();
		Iterator<Entry<String, JsonNode>> jsonFields = jsonParent.fields();
		while (jsonFields.hasNext()) {
			Entry<String, JsonNode> jsonField = jsonFields.next();
			if (jsonField.getKey().equals("moduleName")) {
				if (!jsonField.getValue().toString()
						.equals('"' + moduleName + '"')) {
					break;// Wrong module
				}
				while (jsonFields.hasNext()) {
					Iterator<JsonNode> resultChildren = jsonParent.elements();
					while (resultChildren.hasNext()) {
						header.addAll(recursiveAddResultKeys(resultChildren
								.next()));
					}
					return header;
				}
			}
		}
		// The module extraction wasn't found yet:
		Iterator<JsonNode> jsonChildren = jsonParent.elements();
		while (jsonChildren.hasNext()) {
			header.addAll(getHeader(moduleName, jsonChildren.next()));
		}
		return header;
	}

	private static ArrayList<String> getColumn(String moduleName,
			JsonNode jsonParent) {
		ArrayList<String> column = new ArrayList<String>();
		Iterator<Entry<String, JsonNode>> jsonFields = jsonParent.fields();
		while (jsonFields.hasNext()) {
			Entry<String, JsonNode> jsonField = jsonFields.next();
			if (jsonField.getKey().equals("moduleName")) {
				if (!jsonField.getValue().toString()
						.equals('"' + moduleName + '"')) {
					break;// Wrong module
				}
				while (jsonFields.hasNext()) {
					column.add(getTimestamp(jsonParent.fields()));
					// All children are the results for the module!
					Iterator<JsonNode> resultChildren = jsonParent.elements();
					while (resultChildren.hasNext()) {
						column.addAll(recursiveAddResults(resultChildren.next()));
					}
					return column;
				}
			}
		}
		// The module extraction wasn't found yet:
		Iterator<JsonNode> jsonChildren = jsonParent.elements();
		while (jsonChildren.hasNext()) {
			column.addAll(getColumn(moduleName, jsonChildren.next()));
		}
		return column;
	}

	private static String getTimestamp(
			Iterator<Entry<String, JsonNode>> iterator) {
		while (iterator.hasNext()) {
			Entry<String, JsonNode> entry = iterator.next();
			if (entry.getKey().equals("extractionDate")) {
				return entry.getValue().toString();
			}
		}
		return "no date";
	}

	/**
	 * Logic to get data for the JSON result display.
	 * 
	 * @param collection
	 * @param moduleName
	 * @return data
	 */
	public String getStringResults(ExtractionResultCollection collection,
			String moduleName) {
		StringBuffer data = new StringBuffer();
		String[] response = getRawResults(collection); // probably not best way?
		for (String hit : response) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode root = mapper.readTree(hit);
				addHitIfRightModule(root, moduleName, data);
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at creation of JSON tree", e);
			}
		}
		return data.toString();
	}

	/**
	 * Returns a JSON object as String, that represents the extraction result of
	 * a specific module, at a specific date and for a specific collection.
	 * 
	 * @param collection
	 * @param moduleName
	 * @param extractionDate
	 * @return results
	 */
	public String getStringResults(ExtractionResultCollection collection,
			String moduleName, String extractionDate) {
		try {
			// all results for collection:
			String[] response = getRawResults(collection);
			for (String hit : response) {
				ExtractionResultCollection deserializedCollection = getExtractionResultCollection(hit);
				for (ExtractionResult result : deserializedCollection.extractionResults) {
					if (extractionDate.equals(result.extractionDate.toString())
							&& result.moduleName.equals(moduleName)) {
						return ConfigSaver.getSerializedResult(result);
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "No extraction result found.";
	}

	private void addHitIfRightModule(JsonNode jsonParent, String moduleName,
			StringBuffer data) {
		Iterator<Entry<String, JsonNode>> jsonFields = jsonParent.fields();
		while (jsonFields.hasNext()) {
			Entry<String, JsonNode> jsonField = jsonFields.next();
			if (jsonField.getKey().equals("moduleName")) {
				if (!jsonField.getValue().toString()
						.equals('"' + moduleName + '"')) {
					break;// Wrong module
				}
				try {
					data.append(getTimestamp(jsonFields) + " : ");
					data.append(ConfigSaver.mapper
							.writeValueAsString(jsonParent));
					data.append(",\n");
				} catch (JsonProcessingException e) {
					EXCEPTION_LOGGER.log(Level.SEVERE,
							"Exception at addHitIfRightModule", e);
				}
				return;
			}
		}
		// The module extraction wasn't found yet:
		Iterator<JsonNode> jsonChildren = jsonParent.elements();
		while (jsonChildren.hasNext()) {
			addHitIfRightModule(jsonChildren.next(), moduleName, data);
		}
		return;
	}

	/**
	 * Logic to get the data to fill the InformationChangeTable.
	 * 
	 * @param coll
	 * @param moduleName
	 * @return data to fill the InformationChangeTable
	 */
	public ArrayList<ArrayList<String>> getResults(
			ExtractionResultCollection coll, String moduleName) {
		String[] response = getRawResults(coll);
		return getTableData(moduleName, response);
	}

	private static ArrayList<String> recursiveAddResultKeys(JsonNode resultNode) {
		ArrayList<String> resultKeys = new ArrayList<String>();
		Iterator<Entry<String, JsonNode>> jsonFields = resultNode.fields();
		while (jsonFields.hasNext()) {
			Entry<String, JsonNode> jsonField = jsonFields.next();
			String result = jsonField.getValue().toString();
			if (result != null && !result.equals("")
					&& !jsonField.getKey().equals("results")
					&& !jsonField.getKey().equals("")
					&& !jsonField.getKey().equals("model.KeyValueResult")) {
				resultKeys.add(jsonField.getKey());
			}
		}
		Iterator<JsonNode> jsonChildren = resultNode.elements();
		while (jsonChildren.hasNext()) {
			resultKeys.addAll(recursiveAddResultKeys(jsonChildren.next()));
		}
		return resultKeys;
	}

	private static ArrayList<String> recursiveAddResults(JsonNode resultNode) {
		ArrayList<String> resultStrings = new ArrayList<String>();
		Iterator<Entry<String, JsonNode>> jsonFields = resultNode.fields();
		while (jsonFields.hasNext()) {
			Entry<String, JsonNode> jsonField = jsonFields.next();
			String result = jsonField.getValue().toString();
			if (result != null && !result.equals("")
					&& !jsonField.getKey().equals("results")
					&& !jsonField.getKey().equals("")
					&& !jsonField.getKey().equals("model.KeyValueResult")) {
				resultStrings.add(result);
			}
		}
		Iterator<JsonNode> jsonChildren = resultNode.elements();
		while (jsonChildren.hasNext()) {
			resultStrings.addAll(recursiveAddResults(jsonChildren.next()));
		}
		return resultStrings;
	}

	private void searchAndAddHits(String[] response, DefaultMutableTreeNode root) {
		root.setUserObject("Results");
		for (String hit : response) {
			try {
				ExtractionResultCollection c = getExtractionResultCollection(hit);
				DefaultMutableTreeNode current = new DefaultMutableTreeNode();
				root.add(current);
				Collections.sort(c.extractionResults,
						new Comparator<ExtractionResult>() {
							@Override
							public int compare(ExtractionResult o1,
									ExtractionResult o2) {
								return o1.moduleName.compareTo(o2.moduleName);
							}
						});
				for (ExtractionResult r : c.extractionResults) {
					current.setUserObject("Extraction on " + r.extractionDate);
					DefaultMutableTreeNode result = new DefaultMutableTreeNode(
							r.moduleDisplayName);
					current.add(result);
					result.add(new DefaultMutableTreeNode("Extraction date: "
							+ r.extractionDate));
					result.add(new DefaultMutableTreeNode("Module type: "
							+ r.moduleName));
					if (r.results instanceof KeyValueResult) {
						KeyValueResult kvr = (KeyValueResult) r.results;
						for (Entry<String, String> e : kvr.results.entrySet()) {
							result.add(new DefaultMutableTreeNode(e.getKey()
									+ " :" + e.getValue()));
						}
					} else {
						if (r.results == null) {
							result.add(new DefaultMutableTreeNode("No results"));
							continue;
						}
						StringWriter w = new StringWriter();
						initMapper().writeValue(w, r.results);
						result.add(new DefaultMutableTreeNode(w.toString()));
					}
				}
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at creation of JSON tree", e);
			}
		}
	}

	/**
	 * 
	 * @param f
	 *            serialized ExtractionResultCollection
	 * @return deserialized ExtractionResultCollection
	 * @throws IOException
	 */
	public static ExtractionResultCollection getExtractionResultCollection(
			String f) throws IOException {
		return initMapper().readValue(f, ExtractionResultCollection.class);
	}
}
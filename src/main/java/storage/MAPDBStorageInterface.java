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

import java.io.File;
import java.util.NavigableSet;
import java.util.regex.Pattern;

import model.Environment;
import model.ExtractionResultCollection;
import model.Part;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import configuration.Constants;

/**
 * Class to start Elasticsearch queries. Can be replaced by another storage
 * backend.
 */
public class MAPDBStorageInterface extends GeneralStorage {

	public MAPDBStorageInterface() {
		getClient();
	}

	DB client;
	NavigableSet<Fun.Tuple2<String, String>> mapParts;
	NavigableSet<Fun.Tuple2<String, String>> mapEnvironment;

	private DB getClient() {
		if (client == null) {
			client = DBMaker
					.newFileDB(
							new File(Constants.PROJECT_HOME
									+ File.pathSeparator + "metadata.mapdb"))
					.closeOnJvmShutdown().make();
			mapParts = client.getTreeSet("parts");
			mapEnvironment = client.getTreeSet("environment");
		}
		return client;
	}

	@Override
	public void finalize() {
		if (!client.isClosed()) {
			client.commit();
			client.close();
		}
	}

	@Override
	public void save(String collection, String profileUUID, String type,
			String path) {
		if (path == null) {
			mapEnvironment.add(Fun.t2(profileUUID, collection));
		} else {
			mapParts.add(Fun.t2(path, collection));
		}
		client.commit();

	}

	@Override
	public void deleteAllMetadata() {
		mapEnvironment.clear();
		mapParts.clear();
	}

	/**
	 * Logic to get the data to fill the InformationChangeTable.
	 * 
	 * @return data to fill the InformationChangeTable
	 */
	@Override
	public String[] getRawResults(ExtractionResultCollection coll) {
		Iterable<String> response = null;
		if (coll instanceof Part) {
			Part part = (Part) coll;
			response = Fun.filter(mapParts, part.getPath());

			response = Iterables.filter(response,
					Predicates.contains(Pattern.compile(part.profileUUID)));

		} else if (coll instanceof Environment) {
			Environment environment = (Environment) coll;
			response = Fun.filter(mapEnvironment, environment.profileUUID);
		} else
			return null;
		return Iterables.toArray(response, String.class);
	}

}

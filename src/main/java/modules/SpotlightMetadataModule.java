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

import static configuration.Log.EXCEPTION_LOGGER;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.logging.Level;

import model.ExtractionResult;
import model.KeyValueResult;
import model.OperatingSystem.OsName;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

public class SpotlightMetadataModule extends GeneralNativeCommandModule {

	public SpotlightMetadataModule() {

		GeneralExecutableModuleConfig config = new GeneralExecutableModuleConfig(
				moduleName, version);
		this.setConfig(config);
		config.defaults = config.options = "-plist -";
		config.commandNameOrPath = "";
		config.addSupportedSystem(OsName.OS_X);
		config.WINcommandNameOrPath = "";
		config.OSXcommandNameOrPath = "mdls";
		config.helpOption = "";
	}

	@Override
	public void setModuleName() {
		moduleName = "OS X Spotlight Command module";
	}

	private void addItem(String name, NSDictionary oo, KeyValueResult kvr) {
		NSObject o = oo.get(name);
		if (o != null) {
			if (o instanceof NSArray) {
				NSArray a = (NSArray) o;
				int n = 0;
				for (NSObject oa : a.getArray()) {
					kvr.add(name + n++, oa.toString());
				}
			} else {
				kvr.add(name, o.toString());
			}
		}

	}

	public static String[] extract = new String[] { "kMDItemWhereFroms",
			"kMDItemAuthors", "kMDItemCreator", "kMDItemTitle",
			"kMDItemEncodingApplications", "kMDItemContentCreationDate",
			"kMDItemContentModificationDate", "kMDItemDateAdded",
			"kMDItemLastUsedDate", "kMDItemFSContentChangeDate",
			"kMDItemFSCreationDate", "kMDItemContentTypeTree", };

	@Override
	public ExtractionResult extractInformation(Path path) {

		ExtractionResult r = new ExtractionResult(this);

		ExtractionResult extractionResult = super.extractInformation(path);
		KeyValueResult keyValueResult = (KeyValueResult) extractionResult.results;
		try {
			NSObject o = PropertyListParser
					.parse(new ByteArrayInputStream(keyValueResult.results.get(
							"fullOutput").getBytes("UTF-8")));
			r.setResults(keyValueResult);
			if (o instanceof NSDictionary) {
				NSDictionary oo = (NSDictionary) o;
				for (String name : extract) {
					addItem(name, oo, keyValueResult);
				}
			}
		} catch (Exception e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at module extraction call for module: "
							+ moduleName, e);
		}

		return r;
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	@Override
	public String getModuleDescription() {
		return "This module will return Spotlight metadata using the native mdls command in OSX \n\n\n"
				+ super.getModuleDescription();
	}

	public static void main(String[] args) {
		SpotlightMetadataModule f = new SpotlightMetadataModule();
		System.out.println(f.getModuleDescription());
	}
}

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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.ExtractionResult;
import modules.configuration.RegexConfig;

public class RegexModule extends AbstractFileDependentModule {
	public RegexModule() {
		super();
		this.setConfig(new RegexConfig(moduleName, version));
	}

	public static class RegexMatch {
		public String regularExpression;
		public int start;
		public int end;
		public String subSequence;
	}

	@Override
	public ExtractionResult extractInformation(Path path) {
		if (!((RegexConfig) getConfig()).regexIsValid()) {
			this.getConfig().enabled = false;
			MODULE_LOGGER.severe("Module RegexModule no valid regex.");
			return null;
		}
		if (!getConfig().fileFilter.fileTypeSupported(path)) {
			MODULE_LOGGER.severe("Module RegexModule file type not supported.");
			return null;
		}
		String regex = ((RegexConfig) getConfig()).regularExpression;
		Matcher matcher = useRegex(path, regex);
		List<RegexMatch> matches = new ArrayList<RegexMatch>();
		if (matcher != null) {
			while (matcher.find()) {
				RegexMatch match = new RegexMatch();
				match.regularExpression = regex;
				match.start = matcher.start();
				match.end = matcher.end();
				match.subSequence = matcher.group();
				matches.add(match);
			}
		}
		ExtractionResult extractionResult = new ExtractionResult(this);
		extractionResult.setResults(matches);
		return extractionResult;
	}

	private Matcher useRegex(Path path, String regex) {
		String fileText = "No information matching the regularExpression.";
		try {
			byte[] encoded = Files.readAllBytes(path);
			fileText = Charset.defaultCharset()
					.decode(ByteBuffer.wrap(encoded)).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(fileText);
		return matcher;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module uses a regular expression on defined file types, to look for specific information.\n";
		description += "To use this module, it has to be configurated: The regularExpression has to be defined and the types of the files where the regularExpression should be applied to.\n";
		description += "Configure the file type filters.";
		description += "\n";
		description += "\nThe regularExpression has to be defined in Java regularExpression syntax. For more information, have a look at: \nhttp://docs.oracle.com/javase/7/docs/api/java/util/regularExpression/Pattern.html";
		return description;
	}

	@Override
	public void setModuleName() {
		this.moduleName = "Regex text search";
	}

	@Override
	public void setVersion() {
		this.version = "0.5";
	}
}

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
import static configuration.Log.MODULE_LOGGER;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.ExtractionResult;
import modules.configuration.LogGrepConfig;

public class SimpleLogGrep extends AbstractEnvironmentModule {

	public SimpleLogGrep() {
		super();
		this.setConfig(new LogGrepConfig(this.moduleName, this.version));
	}

	public static class GrepMatch {
		public String regularExpression;
		public String line;
		public String fileName;
		public int lineNumber;
	}

	@Override
	public ExtractionResult extractInformation() {
		if (!((LogGrepConfig) getConfig()).configIsValid()) {
			this.getConfig().enabled = false;
			MODULE_LOGGER.severe("Module SimpleLogGrep not valid configured.");
			return null;
		}

		ArrayList<GrepMatch> matches = new ArrayList<GrepMatch>();
		File directory = new File(
				((LogGrepConfig) getConfig()).logFileDirectory);
		String patternString = ((LogGrepConfig) getConfig()).regularExpression;
		Pattern pattern = Pattern.compile(patternString);

		for (File file : directory.listFiles()) {
			matches.addAll(findPattern(patternString, pattern, file));
		}
		ExtractionResult extractionResult = new ExtractionResult(this);
		extractionResult.setResults(matches);
		return extractionResult;
	}

	private ArrayList<GrepMatch> findPattern(String patternString,
			Pattern pattern, File file) {
		ArrayList<GrepMatch> matches = new ArrayList<GrepMatch>();
		if (file.isFile()
				&& ((LogGrepConfig) getConfig()).fileFilter
						.fileTypeSupported(file.toPath())) {
			try {
				Scanner scanner = new Scanner(file);
				int lineNumber = 0;
				while (scanner.hasNext()) {
					lineNumber++;
					String line = scanner.nextLine();
					Matcher matcher = pattern.matcher(line);
					if (matcher != null) {
						if (matcher.find()) {
							GrepMatch match = new GrepMatch();
							match.regularExpression = patternString;
							match.line = line;
							match.lineNumber = lineNumber;
							match.fileName = file.getAbsolutePath();
							matches.add(match);
						}
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at simple log grep()", e);
			}
		} else if (file.isDirectory()
				&& ((LogGrepConfig) getConfig()).searchDirectoriesRecursive) {
			for (File child : file.listFiles()) {
				matches.addAll(findPattern(patternString, pattern, child));
			}
		}
		return matches;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module parses each line of text files in a specified directory for a defined regularExpression expression."
				+ "\nEach line of the text file that has a match for the expression is included in the results."
				+ "\nEnable the searchDirectoriesRecursive option, to look at files in sub-directories."
				+ "\n\nTo use this module, it has to be configurated: The regularExpression has to be defined and the types of the files where the regularExpression should be applied to."
				+ "\nConfigure the file type filters."
				+ "\n"
				+ "\nThe regularExpression has to be defined in Java regularExpression syntax. For more information, have a look at: \nhttp://docs.oracle.com/javase/7/docs/api/java/util/regularExpression/Pattern.html";
		return description;
	}

	@Override
	public void setModuleName() {
		this.moduleName = "Log expression grep";
	}

	@Override
	public void setVersion() {
		this.version = "0.2";
	}
}

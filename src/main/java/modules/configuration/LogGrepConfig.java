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
package modules.configuration;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import configuration.ModuleConfiguration;

@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public class LogGrepConfig extends ModuleConfiguration {
	public String regularExpression = "";
	public String logFileDirectory = "";
	public boolean searchDirectoriesRecursive = false;

	public LogGrepConfig(String moduleName, String version) {
		super(moduleName, version);
		fileFilter.addInclusiveMediaType("text");
	}

	public LogGrepConfig() {
		super("Log expression grep", "0.2");
	}

	public boolean configIsValid() {
		if (new File(logFileDirectory).isDirectory() && patternIsValid())
			return true;
		return false;
	}

	private boolean patternIsValid() {
		if (regularExpression.equals("")) {
			// state.setState(State.CONFIGURATION_ERROR,
			// "Configuration error: Regexular expression must be defined.");
			return false;
		}
		try {
			Pattern.compile(regularExpression);
		} catch (PatternSyntaxException e) {
			// state.setState(State.CONFIGURATION_ERROR,
			// "Configuration error: Regular expression not valid.");
			return false;
		}
		return true;
	}

	@Override
	public boolean configurationIsValid() {
		// state.setState(State.OK, "Configuration fine.");
		return patternIsValid() && new File(logFileDirectory).isDirectory();
	}
}
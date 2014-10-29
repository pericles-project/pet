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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import configuration.ModuleConfiguration;

@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public class XmlQueryConfig extends ModuleConfiguration {
	public XmlQueryConfigData data = new XmlQueryConfigData(false, "", "",
			false);

	public XmlQueryConfig(String moduleName, String version) {
		super(moduleName, version);
		fileFilter.addInclusiveMimeType("text/xml");
		fileFilter.addInclusiveMimeType("application/xml");
	}

	public XmlQueryConfig() {
		super("XML xPath expression", "0.2");
	}

	/**
	 * Validates the syntax of a XPath expression
	 * 
	 * @return flag if valid
	 */
	private boolean queryIsValid() {
		if (data.XPathExpression.equals(""))
			return false;
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {
			xpath.compile(data.XPathExpression);
			return true;
		} catch (XPathExpressionException e) {
			return false;
		}
	}

	@Override
	public boolean configurationIsValid() {
		boolean isDirectory = new File(data.XMLFileDirectory).isDirectory();
		boolean queryValid = queryIsValid();
		if (isDirectory && queryValid) {
			// state.setState(State.OK, "Configuration fine.");
			return true;
		}
		if (!isDirectory) {
			// state.setState(State.CONFIGURATION_ERROR,
			// "Configuration error: Path is not a directory.");
		}
		if (!queryValid) {
			// state.setState(State.CONFIGURATION_ERROR,
			// "Configuration error: XPath expression invalid.");
		}
		return false;
	}
}
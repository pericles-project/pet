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
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import model.ExtractionResult;
import modules.configuration.XmlQueryConfig;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SimpleXPath extends AbstractEnvironmentModule {

	public SimpleXPath() {
		super();
		this.setConfig(new XmlQueryConfig(this.moduleName, this.version));
	}

	public static class QueryMatch {
		public String query;
		public String fileName;
		public String result;
	}

	@Override
	public ExtractionResult extractInformation() {
		if (!((XmlQueryConfig) getConfig()).configurationIsValid()) {
			this.getConfig().enabled = false;
			MODULE_LOGGER.severe("Module SimpleXPath not valid configured.");
			return null;
		}
		File directory = new File(
				((XmlQueryConfig) getConfig()).data.XMLFileDirectory);
		ArrayList<QueryMatch> matches = new ArrayList<QueryMatch>();
		String expression = ((XmlQueryConfig) getConfig()).data.XPathExpression;
		for (File file : directory.listFiles()) {
			matches.addAll(getResults(file, expression));
		}
		ExtractionResult extractionResult = new ExtractionResult(this);
		extractionResult.setResults(matches);
		return extractionResult;
	}

	private ArrayList<QueryMatch> getResults(File file, String expression) {
		ArrayList<QueryMatch> matches = new ArrayList<QueryMatch>();
		if (file.isFile()) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(file.getAbsolutePath());
				// validate xml document ???
				XPathFactory xPathfactory = XPathFactory.newInstance();
				XPath xpath = xPathfactory.newXPath();
				XPathExpression expr = xpath.compile(expression);
				String result = expr.evaluate(doc);
				QueryMatch match = new QueryMatch();
				match.fileName = file.getAbsolutePath();
				match.query = expression;
				match.result = result;
				matches.add(match);
			} catch (ParserConfigurationException | SAXException | IOException
					| XPathExpressionException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at simple xpath getResults()", e);
			}
		} else if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				matches.addAll(getResults(child, expression));
			}
		}
		return matches;
	}

	@Override
	public String getModuleDescription() {
		String description = "Module to query XPath expressions on a set of XML files at a directory."
				+ "\n1. Enter the path of the directory at XMLFileDirectory"
				+ "\n2. Enable 'searchDirectoriesRecursive' to regard XML files in sub-directories, too"
				+ "\n3. Define the XPathExpression.";
		return description;
	}

	@Override
	public void setModuleName() {
		this.moduleName = "XML xPath expression";
	}

	@Override
	public void setVersion() {
		this.version = "0.2";
	}
}

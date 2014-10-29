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
package model;

import java.util.Date;

import modules.AbstractModule;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class represents the result of a single module extraction run. It will
 * be serialized for storage.
 * 
 * A set of all {@link ExtractionResult}s from one extraction run of all modules
 * will be saved at the {@link Environment} class before storing to database.
 * 
 * ! Don't refactor names, otherwise the serialization will fail. !
 */
public class ExtractionResult {
	/** Name of the module used for the extraction */
	@JacksonXmlProperty(isAttribute = true)
	public String moduleName;
	
	/** Name of the module used for the extraction */
	@JacksonXmlProperty(isAttribute = true)
	public String moduleDisplayName;

	/** Version of the module */
	@JacksonXmlProperty(isAttribute = true)
	public String moduleVersion;

	/** Class of the module */
	@JacksonXmlProperty(isAttribute = true)
	public String moduleClass;

	/** Date of extraction */
	@JacksonXmlProperty(isAttribute = true)
	public Date extractionDate;

	/** Hash value of the {@link configuration.ModuleConfiguration} */
	@JacksonXmlProperty(isAttribute = true)
	public String configurationHash;

	/** Result object. This can be a customized Object of any type. */
	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT, property = "class")
	public Object results;

	/**
	 * Empty constructor is needed for serialization. Don't delete it, and don't
	 * use it if you have no good reason!
	 */
	@Deprecated
	public ExtractionResult() {
	}

	/**
	 * This is the constructor you should use. It will get some information from
	 * the {@link AbstractModule} class which will be saved together with the
	 * extraction result.
	 * 
	 * @param module
	 *            module used for the extraction of this information
	 */
	public ExtractionResult(AbstractModule module) {
		moduleName = module.moduleName;
		moduleDisplayName = module.getConfig().moduleDisplayName;
		moduleVersion = module.version;
		moduleClass = module.getClass().getName();
		extractionDate = new Date();
		configurationHash = module.getHash();
	}

	/**
	 * Set the result object. This can be any class.
	 * 
	 * @param results
	 *            result object - Don't refactor!
	 */
	public void setResults(Object results) {
		this.results = results;
	}
}

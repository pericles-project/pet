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

import static configuration.Constants.VERSION;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Information collections are used to collect sets of extracted information as
 * {@link ExtractionResult}s, for example from different module extractions, to
 * be saved together.
 * 
 * An ExtractionResultCollection can be a {@link Part} or an {@link Environment}
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
@JsonPropertyOrder(alphabetic = true)
public class ExtractionResultCollection {
	/** Version of the Pericles Extraction Tool used for the extraction */
	@JacksonXmlProperty(isAttribute = true)
	public String petVersion;

	/** UUID of the {@link Profile} where this collection belongs to */
	@JacksonXmlProperty(isAttribute = true)
	public String profileUUID;

	/** List of extracted information */
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<ExtractionResult> extractionResults;

	/**
	 * Constructor used for serialization. Don't use it for other purposes.
	 */
	@Deprecated
	public ExtractionResultCollection() {
	}

	/**
	 * Main constructor.
	 * 
	 * @param profileuuId
	 *            UUID of the {@link Profile} where this information was
	 *            extracted
	 */
	public ExtractionResultCollection(String profileuuId) {
		petVersion = VERSION;
		profileUUID = profileuuId;
		extractionResults = new LinkedList<>();
	}

	/**
	 * Add an {@link ExtractionResult} to this collection.
	 * 
	 * @param result
	 *            the extracted information to be added
	 */
	public void addExtractionResult(ExtractionResult result) {
		if (result != null) {
			extractionResults.add(result);
		}
	}

	/** Deletes all extracted information */
	public void deleteExtractedInformation() {
		extractionResults.clear();
	}

	/**
	 * Sorts the list of extracted information by module names.
	 */
	public void sortResults() {
		Collections.sort(extractionResults, new Comparator<ExtractionResult>() {
			@Override
			public int compare(ExtractionResult o1, ExtractionResult o2) {
				
				int c = o1.moduleDisplayName.compareTo(o2.moduleDisplayName);
				if (c != 0)
					return c;
				return o1.extractionDate.compareTo(o2.extractionDate);
			}
		});
	}
}

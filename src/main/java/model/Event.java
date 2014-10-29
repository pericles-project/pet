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

import modules.AbstractDaemonModule;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/**
 * Represents an event that occurred in the system environment and was monitored
 * by one of the application daemons, or an event created as result of other
 * occurring events.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
@JsonPropertyOrder(alphabetic = true)
public class Event {

	/** Date of the event occurrence */
	public long timestamp;

	/** Data associated with the event, the type of data depends on the creating module.  */
	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
	public Object data;

	/** flag to mark an event for recording in the event list. NOT STORED */
	@JsonIgnore
	public boolean record;

	/** The module that reported the event. NOT STORED */
	@JsonIgnore
	public AbstractDaemonModule reporter;

	/** The name of the reporting module */
	public String reporterName;

	/** The type of event. This is specific to the reproting module (so for example, appear and disappear events for LSOF module, file type events for teh directoty watch module. */
	public String type;

	/** The filename that allows the controller to add new files to the specific profile. This is used when the "eventAddToProfile" property is  specified in the generating module configuration.  */
	public String fileName;

	/**
	 * Empty constructor needed for serialization. Don't use this constructor
	 * for other purposes.
	 */
	@Deprecated
	public Event() {
	}

	/**
	 * Create a new event, initialising the fields. 
	 * 
	 * @param data
	 * @param record
	 * @param reporter
	 * @param fileName
	 */
	public Event(Object data, boolean record, AbstractDaemonModule reporter,
			String fileName) {
		super();
		this.data = data;
		this.record = record;
		this.reporter = reporter;
		reporterName = reporter.moduleName;
		timestamp = System.currentTimeMillis();
		this.fileName = fileName;
	}
	public Event(Object data, boolean record, String reporterName,
			String fileName) {
		super();
		this.data = data;
		this.record = record;
		this.reporter = null;
		reporterName = reporterName;
		timestamp = System.currentTimeMillis();
		this.fileName = fileName;
	}

	/** Returns String representation of this class */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}

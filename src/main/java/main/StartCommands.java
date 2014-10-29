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
package main;

import static configuration.Log.EXCEPTION_LOGGER;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.reflections.ReflectionUtils;

import storage.GeneralStorage;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import controller.StorageController;

/**
 * This class provides the user interface, that is available via command line.
 * It usesJcommander annotations. First it defines the options that can be
 * chosen by the user; secondly it parses the user arguments; and thirdly it
 * provides the specified properties for the application.
 * 
 * @author Anna
 * @author Fabio
 * 
 */
public class StartCommands {
	/**
	 * Defines the command line options for the tool start.
	 */
	public static class CliParameters {
		@Parameter(names = { "-h", "--help" }, description = "print this message", help = true)
		public boolean help;
		@Parameter(names = { "-p", "--projecthelp" }, description = "print project help information")
		public boolean phelp;
		@Parameter(names = { "", "--version" }, description = "print project help information")
		public boolean version;
		@Parameter(names = { "-o", "--once" }, description = "extract just once, not continuously")
		public boolean once;
		@Parameter(names = { "-n", "--headless" }, description = "start without graphics")
		public boolean headless;
		@Parameter(names = { "-s", "--storage" }, description = "Storage system for the extraction results. Default:elasticsearch ")
		public String storage = "elasticsearch";
		@Parameter(names = { "-d", "--destination" }, description = "Default folder location for the PET data;  default: PET.jar folder")
		public String destination = null;
	}

	public CliParameters options;
	public JCommander jc;

	/**
	 * Constructor gets the unmodified user args from the tool start command
	 * line.
	 * 
	 * @param args
	 */
	public StartCommands(String args[]) {
		options = new CliParameters();
		jc = new JCommander(options);
		jc.setProgramName("[P]ericles [E]xtraction [T]ool");
		try {
			jc.parse(args);
		} catch (ParameterException e) {
			jc.usage();
			System.out.print("\nStorage interfaces: \n");
			for (Class<? extends GeneralStorage> c : StorageController.storageSystems) {
				System.out.print(" " + c.getSimpleName() + "\n\n"); // +"\t\t[");
			}
			System.out.println("Wrong arguments, can not start. Try --help");
			EXCEPTION_LOGGER
					.log(Level.SEVERE, "Wrong command line argument", e);
			System.exit(-1);
		}
	}

	/**
	 * Parses the command line configurations and merges them with the file
	 * specified commands
	 * 
	 * @param fileProperties
	 *            configurations specified by the configuration file
	 * @return merged command line configurations and configurations specified
	 *         by the configuration file in a {@link Properties} class.
	 */
	public Properties getProperties(Properties fileProperties) {
		Properties properties = new Properties(fileProperties);
		Set<Field> fields = ReflectionUtils.getAllFields(options.getClass(),
				ReflectionUtils.withModifier(Modifier.PUBLIC));
		for (Field field : fields) {
			try {
				// if the option is false, then this would overwrite a true
				// option given by the file, so filter it out
				if (field.getName() != null && field.get(options) != null
						&& !field.get(options).toString().equals("false")) {//
					properties.setProperty(field.getName(), field.get(options)
							.toString());
				}
			} catch (Exception e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception while getting properties", e);
			}
		}
		return properties;
	}
}
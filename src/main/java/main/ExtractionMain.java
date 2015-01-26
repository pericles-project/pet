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

import static configuration.Constants.EXTRACTION_PREFERENCES_FILE;
import static configuration.Constants.SI_LOCK_FILE;
import static configuration.Constants.VERSION;
import static configuration.Log.EXCEPTION_LOGGER;
import gui.StartupWindow;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;

import storage.GeneralStorage;
import utility.FileUtils;
import utility.PropertiesSaverAndLoader;
import configuration.Constants;
import configuration.Log;
import controller.ExtractionController;
import controller.ExtractionControllerBuilder;
import controller.StorageController;

/**
 * This main class brings together the user properties specified via command
 * line {@link StartCommands} and via the properties file
 * {@link PropertiesSaverAndLoader}. Then it uses these properties to configure
 * the {@link ExtractionControllerBuilder} for building the applications main
 * controller: {@link ExtractionController}.
 */
public class ExtractionMain {
	private static StartCommands startInterface;
	private static boolean firstStart;

	private static boolean lockInstance(final String lockFile) {
		try {
			final File file = new File(lockFile);
			file.getParentFile().mkdirs();
			final RandomAccessFile randomAccessFile = new RandomAccessFile(
					file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (Exception e) {
							EXCEPTION_LOGGER.log(Level.SEVERE,
									"Unable to remove lock file: " + lockFile,
									e);
						}
					}
				});
				return true;
			}
		} catch (Exception e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Unable to create and/or lock file: " + lockFile, e);
		}
		return false;
	}

	/**
	 * Applications main method!
	 * 
	 * @param args
	 *            User arguments given at tool start
	 */
	public static void main(String args[]) {
		
		startInterface = new StartCommands(args);
		String workingDirectory =startInterface.options.destination;
		if (workingDirectory == null || workingDirectory.trim().length() == 0) {
			workingDirectory = FileUtils.getCurrentJarFolder(Constants.class)
					+ "";
		}
		workingDirectory = workingDirectory +  File.separator + "PET_data" + File.separator;
		//System.out.println(workingDirectory);
		firstStart = !Files.exists(Paths.get(workingDirectory));
		//System.out.println(firstStart);
		new Constants(startInterface.options.destination);
		if (!lockInstance(SI_LOCK_FILE)) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Enforcing single instance. Application already running!");
			System.err
					.println("Enforcing single instance. Application already running!");
			return;
		}
		Log.initLogging(Constants.LOG_DIRECTORY);
		Properties userPropertiesFile = PropertiesSaverAndLoader
				.loadProperties(EXTRACTION_PREFERENCES_FILE);
		Properties userInput = startInterface.getProperties(userPropertiesFile);
		final ExtractionController controller = buildExtractor(userInput);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				controller.saveConfigs();
				StorageController.storage.finalize();
			}
		});
	}

	private static ExtractionController buildExtractor(Properties userInput) {
		ExtractionControllerBuilder builder = new ExtractionControllerBuilder();
		if (optionIsTrue("once", userInput)) {
			builder.once();
		}
		if (optionIsTrue("headless", userInput)) {
			builder.headless();
		}
		if (optionIsTrue("help", userInput)) {
			printHelpAndExit();
		}
		if (optionIsTrue("phelp", userInput)) {
			printProjectHelpAndExit();
		}
		if (optionIsTrue("version", userInput)) {
			printVersionAndExit();
		}
		builder.firstStart = firstStart;
		builder.storageSystem = userInput.getProperty("storage");
		return builder.create();
	}

	private static void printHelpAndExit() {
		startInterface.jc.usage();
		System.out.print("\nStorage interfaces: \n");
		for (Class<? extends GeneralStorage> c : StorageController.storageSystems) {
			System.out.print(" " + c.getSimpleName() + ", "); // +"\t\t[");
		}
		System.out.println();
		System.exit(0);
	}

	private static void printProjectHelpAndExit() {
		System.out
				.println("The Pericles Extraction Tool extracts information from the environment of digital objects about these objects and about the environment itself. The information is stored in XML files and can be completed and managed by the tool. A selection of information that will be extracted can be specified and the set of possible extractable meta data can be extended by the integration of external applications into the tool.");
		System.exit(0);
	}

	private static void printVersionAndExit() {
		System.out
				.println("Pericles Extraction Tool [Version " + VERSION + "]");
		System.exit(0);
	}

	private static boolean optionIsTrue(String option, Properties properties) {
		return properties.getProperty(option) != null
				&& properties.getProperty(option).equals("true");
	}
}

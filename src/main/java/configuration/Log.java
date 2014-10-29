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
package configuration;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * If you like to define a more specific logger, decide to inherit from an
 * existing one by naming it "existingLoggerName.newLoggerName".
 * 
 * Example: Logger for extraction flow tracing: getLogger("flow.exception")
 */
public class Log {
	/**
	 * Logger for exceptions.
	 */
	public final static Logger EXCEPTION_LOGGER = Logger.getLogger("exception");
	/**
	 * Logger to be used by the modules.
	 */
	public final static Logger MODULE_LOGGER = Logger.getLogger("module");
	/**
	 * Logger for flow tracing.
	 */
	public final static Logger FLOW_LOGGER = Logger.getLogger("flow");
	/**
	 * Logger for debug messages.
	 */
	public final static Logger DEBUG_LOGGER = Logger.getLogger("debug");

	private Log() {
	}

	public static void initLogging(String LOG_DIRECTORY) {
		disableDefaultHandler();
		createHandlers(LOG_DIRECTORY);
		setLevels();
	}

	private static void disableDefaultHandler() {
		Logger globalLogger = Logger.getLogger("");
		Handler[] handlers = globalLogger.getHandlers();
		for (Handler handler : handlers) {
			globalLogger.removeHandler(handler);
		}
	}

	private static void createHandlers(String LOG_DIRECTORY) {
		try {
			/*
			 * If you want to enable logging output to be shown at the console
			 * for a logger, add this console handler to the logger:
			 */
			@SuppressWarnings("unused")
			Handler consoleHandler = new ConsoleHandler();

			Handler exceptionHandler = new FileHandler(LOG_DIRECTORY
					+ "exceptions.log", 100000, 2, false);
			exceptionHandler.setFormatter(new SimpleFormatter());
			EXCEPTION_LOGGER.addHandler(exceptionHandler);

			Handler moduleHandler = new FileHandler(LOG_DIRECTORY
					+ "modules.log", 100000, 2, false);
			moduleHandler.setFormatter(new SimpleFormatter());
			MODULE_LOGGER.addHandler(moduleHandler);

			Handler flowHandler = new FileHandler(LOG_DIRECTORY + "flow.log",
					100000, 2, false);
			flowHandler.setFormatter(new SimpleFormatter());
			FLOW_LOGGER.addHandler(flowHandler);

			Handler debugHandler = new FileHandler(LOG_DIRECTORY + "debug.log",
					100000, 2, false);
			debugHandler.setFormatter(new SimpleFormatter());
			DEBUG_LOGGER.addHandler(debugHandler);

//			Handler elasticsearchHandler = new FileHandler(LOG_DIRECTORY
//					+ "elasticsearch.log", 100000, 2, false);
//			elasticsearchHandler.setFormatter(new SimpleFormatter());
		} catch (SecurityException | IOException e) {
		}
	}

	private static void setLevels() {
		EXCEPTION_LOGGER.setLevel(Level.WARNING);
		MODULE_LOGGER.setLevel(Level.WARNING);
		FLOW_LOGGER.setLevel(Level.FINE);
		DEBUG_LOGGER.setLevel(Level.FINE);
	}
}

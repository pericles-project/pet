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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import model.ExtractionResult;
import model.KeyValueResult;
import model.OperatingSystem;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

import configuration.Log;

public class GeneralNativeCommandModule extends AbstractFileDependentModule {
	public static class NativeExecutionException extends Exception {
		private static final long serialVersionUID = 1L;
		public String executionResult;
		public String path;
		public int exitCode;

		@Override
		public String getMessage() {
			return super.getMessage() != null ? super.getMessage() : "" + '\n'
					+ executionResult;
		}
	}

	String description = "This module will execute a native operating system command.\n"
			+ "The command will be executed in the specified working directory.\n"
			+ "The file paths will be added as argument to the command.\n"
			+ "This is a file dependent module.\n";
	String origDesc = description;

	public GeneralNativeCommandModule() {
		super();
		setConfig(new GeneralExecutableModuleConfig(moduleName, version));
	}

	private String extractedLocation;

	@Override
	public void setModuleName() {
		moduleName = "Create custom executable command (file dependent)";
	}

	@Override
	public ExtractionResult extractInformation(Path path) {
		if (getCurrentOsCommand() == null) {
			this.getConfig().enabled = false;
			return null;
		}
		ExtractionResult extractionResult = new ExtractionResult(this);
		try {
			KeyValueResult keyValueResult;
			keyValueResult = extractNative(getCurrentOsCommand(), getConfig()
					.currentOsOptions(), path);
			extractionResult.setResults(keyValueResult);
		} catch (NativeExecutionException e) {
			if (e.getCause() instanceof IOException) {
				this.getConfig().enabled = false;
				extractedLocation = null;
			} else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extractionResult;
	}

	protected boolean canRunNativeCommand() {
		if (!getConfig().noHelp) {
			try {
				extractNative(getCurrentOsCommand(), getConfig().helpOption,
						null);
			} catch (NativeExecutionException e) {
				if (e.getCause() instanceof IOException)
					return false;
			} catch (Exception e) {
			}
		}
		return true;
	}

	protected KeyValueResult extractNative(String command, String options,
			Path path) throws NativeExecutionException {
		if (command == null || command.equals("")) {
			System.err
					.println("command null at GeneralNativeCommandModule.extractNative()");
			return null;
		}
		CommandLine commandLine = new CommandLine(command);

		if (options != null && !options.equals("")) {
			String[] args = options.split(" ");
			commandLine.addArguments(args);
		}

		if (path != null) {
			commandLine.addArgument(path.toAbsolutePath().toString(), false);
		}
		DefaultExecutor executor = new DefaultExecutor();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);
		GeneralExecutableModuleConfig generalExecutableModuleConfig = getConfig();
		executor.setWatchdog(new ExecuteWatchdog(
				generalExecutableModuleConfig.timeout));
		if (getConfig().workingDirectory != null
				&& getConfig().workingDirectory.exists()) {
			executor.setWorkingDirectory(getConfig().workingDirectory);
		}
		try {
			// System.out.println(commandLine);
			executor.execute(commandLine);
		} catch (ExecuteException xs) {
			NativeExecutionException n = new NativeExecutionException();
			n.initCause(xs);
			if (path != null) {
				n.path = path.toAbsolutePath().toString();
			}
			n.executionResult = outputStream.toString();
			n.exitCode = xs.getExitValue();
			throw n;
		} catch (IOException xs) {
			// System.out.println(commandLine);
			NativeExecutionException n = new NativeExecutionException();
			n.initCause(xs);
			if (path != null) {
				n.path = path.toAbsolutePath().toString();
			}
			n.executionResult = outputStream.toString();
			throw n;
		}
		KeyValueResult t = new KeyValueResult("GeneralNativeCommandResults");
		t.add("fullOutput", outputStream.toString().trim());
		return t;
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	public String getCurrentOsCommand() {
		if (extractedLocation != null)
			return extractedLocation;
		GeneralExecutableModuleConfig executableConfig = getConfig();
		String command = executableConfig.commandNameOrPath;
		switch (OperatingSystem.getCurrentOS().genericName) {
		case OS_X:
			if (executableConfig.OSXcommandNameOrPath != null
					&& !executableConfig.OSXcommandNameOrPath.equals("")) {
				command = executableConfig.OSXcommandNameOrPath;
			}
			break;
		case WINDOWS:
			if (executableConfig.WINcommandNameOrPath != null
					&& !executableConfig.WINcommandNameOrPath.equals("")) {
				command = executableConfig.WINcommandNameOrPath;
			}
			break;
		case LINUX:
			if (executableConfig.LinuxcommandNameOrPath != null
					&& !executableConfig.LinuxcommandNameOrPath.equals("")) {
				command = executableConfig.LinuxcommandNameOrPath;
			}
			break;
		case BSD:
			if (executableConfig.BSDcommandNameOrPath != null
					&& !executableConfig.BSDcommandNameOrPath.equals("")) {
				command = executableConfig.BSDcommandNameOrPath;
			}
		default:
			break;
		}
		if (command == null || command.equals(""))
			return null;
		try {
			if (!getConfig().noHelp) {
				extractNative(command, getConfig().helpOption, null);
			}
		} catch (NativeExecutionException e) {
			if (e.getCause().getClass().equals(IOException.class)) {
				File f = utility.FileUtils.getCurrentJarFolder(this.getClass());
				File f2 = new File(f, command);
				command = f2.getAbsolutePath();
				try {
					if (!getConfig().noHelp) {
						extractNative(command, getConfig().helpOption, null);
						// System.out.println("AA"+r);
					}
				} catch (NativeExecutionException e1) {
					// .printStackTrace();
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		extractedLocation = command;
		// System.out.println("XXX"+r);
		return extractedLocation;

	}

	@Override
	// public String getModuleDescription() {
	// String description =
	// "This module will execute a native operating system command.\n"
	// + "The command will be executed in the specified working directory.\n"
	// + "The file paths will be added as argument to the command.\n"
	// + "This is a file dependent module.\n";
	// return description;
	// }
	// TODO: this method as implemented here would return in many cases the
	// general command help for that specific command. For this reason I would
	// consider optionally adding it back. One could define an option to express
	// thsi is avlaible or not.
	public String getModuleDescription() {
		if (origDesc.equals(description)) {
			try {
				if (getCurrentOsCommand() != null)
					if (!getConfig().noHelp) {
						description = extractNative(getCurrentOsCommand(),
								getConfig().helpOption, null).results
								.get("fullOutput")
								+ "\nDefault option: " + getConfig().defaults;
					}
			} catch (NativeExecutionException x) {
				if (x.exitCode > 0) {
					description = x.executionResult.trim();
				}

				else if (x.getCause() instanceof IOException) {
					this.getConfig().enabled = false;
					extractedLocation = null;

				} else {
					x.printStackTrace();
				}

			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return description;

	}

	@Override
	public GeneralExecutableModuleConfig getConfig() {
		return (GeneralExecutableModuleConfig) super.getConfig();
	}

	public static void main(String[] args) {
		GeneralNativeCommandModule x = new GeneralNativeCommandModule();
		try {
			x.getConfig().commandNameOrPath = "file";
			System.out.println(x.getCurrentOsCommand());
			x.getConfig().WINcommandNameOrPath = "nativeTools/file_utility_windows/bin/file.exe";
			System.out.println(x.extractNative(x.getCurrentOsCommand(),
					"--help", null).results.get("fullOutput"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// extractNative(((GeneralExecutableModuleConfig)config).helpOption)
	}

	public class LogOutNative extends LogOutputStream {

		@Override
		protected void processLine(String line, int level) {
			Log.DEBUG_LOGGER.warning(line);

		}

	}
}

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

import model.OperatingSystem;
import modules.GeneralNativeCommandModule.NativeExecutionException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public abstract class NativeProcessIterativeDaemonModule extends
		AbstractDaemonModule {

	private String extractedLocation;

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				observe();
				Thread.sleep(getConfig().waitBetweenCalls);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public NativeProcessIterativeDaemonModule() {
		super();
		setConfig(new GeneralExecutableModuleConfig(moduleName, version));
		getConfig().enabled = false;
		// getConfig().commandNameOrPath = "lsof";
		// getConfig().addSupportedSystem(OsName.BSD).addSupportedSystem(OsName.LINUX).addSupportedSystem(OsName.OS_X).addSupportedSystem(OsName.SOLARIS);
	}

	@Override
	protected void observe() throws InterruptedException {
		try {
			processOut(extractNative(getCurrentOsCommand(), getConfig()
					.currentOsOptions(), null));
		} catch (NativeExecutionException e) {
			e.printStackTrace();
		}
	}

	public void processOut(String extractNative) {
		System.out.println(extractNative);

	}

	@Override
	public void setModuleName() {
		this.moduleName = "Native process iterative module";

	}

	@Override
	public GeneralExecutableModuleConfig getConfig() {
		return (GeneralExecutableModuleConfig) super.getConfig();
	}

	protected boolean canRunNativeCommand() {
		try {
			extractNative(getCurrentOsCommand(), getConfig().helpOption, null);
		} catch (NativeExecutionException e) {
			if (e.getCause() instanceof IOException)
				return false;
		} catch (Exception e) {
		}
		return true;
	}

	protected String extractNative(String command, String options, Path path)
			throws NativeExecutionException {
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

			// System.out.println("Now execute " + commandLine);
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
			NativeExecutionException n = new NativeExecutionException();
			n.initCause(xs);
			if (path != null) {
				n.path = path.toAbsolutePath().toString();
			}
			n.executionResult = outputStream.toString();
			throw n;
		}
		return outputStream.toString().trim();
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
		default:
			break;
		}
		if (command == null || command.equals(""))
			return null;
		try {
			extractNative(command, getConfig().helpOption, null);
		} catch (NativeExecutionException e) {
			if (e.getCause().getClass().equals(IOException.class)) {
				File f = utility.FileUtils.getCurrentJarFolder(this.getClass());
				File f2 = new File(f, command);
				command = f2.getAbsolutePath();
				try {
					extractNative(command, getConfig().helpOption, null);
					// System.out.println("AA"+r);
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

	// public static void main(String[] args) {
	// NativeProcessIterativeDaemonModule x = new
	// NativeProcessIterativeDaemonModule();
	// try {
	// x.getConfig().commandNameOrPath = "ls";
	// x.getConfig().addSupportedSystem(OsName.BSD).addSupportedSystem(OsName.LINUX).addSupportedSystem(OsName.OS_X).addSupportedSystem(OsName.SOLARIS);
	// //System.out.println(x.getCurrentOsCommand());
	// x.isDaemon = false;
	// x.setSelected(true);
	//
	// //x.start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // extractNative(((GeneralExecutableModuleConfig)config).helpOption)
	// }
	String description = "This module will execute a native operating system command.\n"
			+ "The command will be executed in the specified working directory.\n"
			+ "The file paths will be added as argument to the command.\n"
			+ "This is a file dependent module.\n";
	String origDesc = description;

	@Override
	public String getModuleDescription() {
		if (origDesc.equals(description)) {
			try {
				if (getCurrentOsCommand() != null) {
					description = extractNative(getCurrentOsCommand(),
							getConfig().helpOption, null)
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

}

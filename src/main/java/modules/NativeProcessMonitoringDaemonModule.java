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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;

import modules.GeneralNativeCommandModule.NativeExecutionException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;

public abstract class NativeProcessMonitoringDaemonModule extends
		NativeProcessIterativeDaemonModule implements ExecuteResultHandler {

	public class MyExecuteStreamHandler implements ExecuteStreamHandler {
		OutputStream sis;
		InputStream ses;
		InputStream sos;

		public MyExecuteStreamHandler() {
		}

		@Override
		public void stop() {

			if (runner != null) {
				try {
					runner.join();
					runner = null;
				} catch (InterruptedException e) {
					// ignore
				}
			}
			// System.out.println("Stop IO");

		}

		@Override
		public void start() throws IOException {
			// System.out.println("Start IO with SOS" + sos);
			superStart();

		}

		@Override
		public void setProcessInputStream(OutputStream os) throws IOException {
			this.sis = os;
			// System.out.println("got sis" + os);

		}

		@Override
		public void setProcessErrorStream(InputStream is) throws IOException {
			this.ses = is;
			// System.out.println("got ses" + is);
		}

		@Override
		public void setProcessOutputStream(InputStream is) throws IOException {
			this.sos = is;
			superCreate();
			// System.out.println("got sos" + is);
		}
	};

	DefaultExecutor executor;
	MyExecuteStreamHandler esh;

	@Override
	public void start() {
		if (isSelected() && supportsThisOS()) {
			try {
				startNativeMonitor(getCurrentOsCommand(), getConfig()
						.currentOsOptions(), null);
			} catch (NativeExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public void superStart() {
		super.start();
	}

	public void superCreate() {
		super.create();
	}

	@Override
	public void stop() {
		super.stop();
		// System.out.println("stop");
		if (executor != null) {
			executor.getWatchdog().destroyProcess();
		}

	}

	// /**
	// * Copies data from the input stream to the output stream. Terminates as
	// * soon as the input stream is closed or an error occurs.
	// */
	// public void run() {
	//
	// final byte[] buf = new byte[1024];
	//
	// int length;
	// try {
	// while ((length = esh.sos.read(buf)) > 0) {
	// os.write(buf, 0, length);
	// }
	// } catch (Exception e) {
	// // nothing to do - happens quite often with watchdog
	// } finally {
	// if (closeWhenExhausted) {
	// try {
	// os.close();
	// } catch (IOException e) {
	// String msg = "Got exception while closing exhausted output stream";
	// DebugUtils.handleException(msg ,e);
	// }
	// }
	// synchronized (this) {
	// finished = true;
	// notifyAll();
	// }
	// }
	// }
	//
	@Override
	public void run() {
		// System.out.println("RUN + " + esh.sos);
		//

		BufferedReader sosReader = new BufferedReader(new InputStreamReader(
				esh.sos));
		try {
			sosReader.readLine();
			String line;
			while ((line = sosReader.readLine()) != null
					&& !Thread.currentThread().isInterrupted()) {
				processLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void processLine(String line) {
		System.out.println(line);

	}

	@Override
	protected void observe() throws InterruptedException {
	}

	protected void startNativeMonitor(String command, String options, Path path)
			throws NativeExecutionException {
		if (command == null || command.equals("")) {
			System.err
					.println("command null at GeneralNativeCommandModule.extractNative()");
			return;
		}
		CommandLine commandLine = new CommandLine(command);

		if (options != null && !options.equals("")) {
			String[] args = options.split(" ");
			commandLine.addArguments(args);
		}

		if (path != null) {
			commandLine.addArgument(path.toAbsolutePath().toString(), false);
		}
		executor = new DefaultExecutor();
		esh = new MyExecuteStreamHandler();
		executor.setStreamHandler(esh);

		// GeneralExecutableModuleConfig generalExecutableModuleConfig =
		// getConfig();
		executor.setWatchdog(new ExecuteWatchdog(
				ExecuteWatchdog.INFINITE_TIMEOUT));
		if (getConfig().workingDirectory != null
				&& getConfig().workingDirectory.exists()) {
			executor.setWorkingDirectory(getConfig().workingDirectory);
		}

		try {
			// System.out.println("Now execute: " + commandLine);
			executor.execute(commandLine, this);
		} catch (ExecuteException xs) {
			NativeExecutionException n = new NativeExecutionException();
			n.initCause(xs);
			if (path != null) {
				n.path = path.toAbsolutePath().toString();
			}
			n.exitCode = xs.getExitValue();
			throw n;
		} catch (IOException xs) {
			NativeExecutionException n = new NativeExecutionException();
			n.initCause(xs);
			if (path != null) {
				n.path = path.toAbsolutePath().toString();
			}
			throw n;
		}
		return;
	}

	@Override
	public void setModuleName() {
		this.moduleName = "Native process monitor module";

	}

	@Override
	public void onProcessComplete(int exitValue) {
		if (runner != null) {
			runner.interrupt();
		}
	}

	@Override
	public void onProcessFailed(ExecuteException e) {
		if (runner != null) {
			runner.interrupt();
		}

	}

	// public static void main(String[] args) {
	// NativeProcessMonitoringDaemonModule x = new
	// NativeProcessMonitoringDaemonModule();
	//
	// try {
	// x.getConfig().commandNameOrPath = "ls";
	// x.getConfig().addSupportedSystem(OsName.BSD).addSupportedSystem(OsName.LINUX).addSupportedSystem(OsName.OS_X).addSupportedSystem(OsName.SOLARIS);
	// x.isDaemon = false;
	// x.setSelected(true);
	// //
	// // x.start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // extractNative(((GeneralExecutableModuleConfig)config).helpOption)
	// }

}

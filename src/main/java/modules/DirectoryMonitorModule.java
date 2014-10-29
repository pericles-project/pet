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
import static utility.FileUtils.isDirectory;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import model.Event;
import model.OperatingSystem.OsName;
import modules.configuration.DirectoryMonitorModuleConfig;
import storage.FileStorageInterface;
import configuration.Log;
import controller.EventController;
import controller.ExtractionController;
import controller.StorageController;

public class DirectoryMonitorModule extends AbstractDaemonModule {

	protected WatchService directoryWatcher;

	private final Map<WatchKey, Path> keys;

	public DirectoryMonitorModule() {
		super();
		setConfig(new DirectoryMonitorModuleConfig());
		getConfig().addSupportedSystem(OsName.SYSTEM_INDEPENDENT);
		getConfig().enabled = false;
		this.keys = new HashMap<WatchKey, Path>();
	}

	private void registerAll(final Path start) {
		try {
			Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					reg(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void reg(Path directory) {

		if (isDirectory(directory)) {
			try {
				WatchKey key;
				if (!((DirectoryMonitorModuleConfig)getConfig()).monitorDelete)
					key = directory.register(directoryWatcher,
							StandardWatchEventKinds.ENTRY_CREATE);// ,
				else 
					key = directory.register(directoryWatcher,
							StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
				keys.put(key, directory);
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at registration of watch event", e);
			}
		}
		// System.out.println("Registered " + directory);

	}

	public void register(Path directory, boolean recursive) {
		if (recursive) {
			registerAll(directory);
		} else {
			reg(directory);
		}
	}

	@Override
	protected void observe() throws InterruptedException {
		WatchKey key = null;
		try {
			key = directoryWatcher.take();
		} catch (ClosedWatchServiceException x) {
			return;
		}
		Path dir = keys.get(key);
		if (dir == null)
			return;

		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();
			if (kind == StandardWatchEventKinds.OVERFLOW) {
				continue;
			}
			Path rel = (Path) event.context();
			Path changed = dir.resolve(rel);
			if ((kind == StandardWatchEventKinds.ENTRY_CREATE)) {

				if (Files.isDirectory(changed, LinkOption.NOFOLLOW_LINKS)) {
					registerAll(changed);
				}

			}
			if (!config.fileFilter.fileExtensionSupported(changed.getFileName()
					.toString())) {
				continue;
			}

			Event e = new Event(changed.toString(), true, this,
					changed.toString());
			e.type = kind.name();
			submitEvent(e);

		}
		boolean valid = key.reset();
		if (!valid) {
			keys.remove(key);

		}
	}

	@Override
	public void start() {

		if (isSelected() && supportsThisOS()) {
			createWatcher();
			try {

				String dp = ((DirectoryMonitorModuleConfig) this.getConfig()).monitoredFolderPath;
				boolean rec = ((DirectoryMonitorModuleConfig) this.getConfig()).isRecursive;
				register(FileSystems.getDefault().getPath(dp), rec);
			} catch (Exception x) {
				Log.EXCEPTION_LOGGER.log(Level.SEVERE,
						"Error registering folder monitor ", x);
			}
		}
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		try {
			if (directoryWatcher != null) {
				directoryWatcher.close();
			}
			directoryWatcher = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void createWatcher() {
		try {
			directoryWatcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception while getting watch service", e);
		}
	}

	@Override
	public void setModuleName() {
		this.moduleName = "Directory Monitor Module";
	}

	public static void main(String[] args) {
		// System.out.println(new Date(1396566160095l));
		ExtractionController.eventq = new EventController(null);
		final Path path = FileSystems.getDefault().getPath(
				System.getProperty("user.home"));
		StorageController.storage = new FileStorageInterface();
		DirectoryMonitorModule m = new DirectoryMonitorModule();
		((DirectoryMonitorModuleConfig) m.getConfig()).monitoredFolderPath = path
				.toString();
		m.getConfig().addSupportedSystem(OsName.SYSTEM_INDEPENDENT);
		m.isDaemon = false;
		m.setSelected(true);
	}

	@Override
	public String getModuleDescription() {
		return "monitores directories";
	}
}

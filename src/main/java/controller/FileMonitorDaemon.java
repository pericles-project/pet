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
package controller;

import static configuration.Log.EXCEPTION_LOGGER;
import static utility.FileUtils.isDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.logging.Level;

import model.Event;
import model.Part;
import model.Profile;

/**
 * The FileMonitorDaemon is a daemon that observes file events. If a file of an
 * digital object under observation is altered, the meta data that could have
 * changed can be extracted again. If a file is deleted, it has to be removed
 * from the data structure.
 * 
 * The ExtractionController looks for altered or deleted files in the
 * modifiedParts and removedPartsTable of this class. Just a synchronized access
 * is allowed.
 * 
 * @see ExtractionController
 */
public class FileMonitorDaemon extends Thread {
	protected WatchService fileWatcher;
	private final HashSet<WatchKey> watchKeys = new HashSet<WatchKey>();
	/**
	 * This HashSet contains the monitored files, so the events can be filtered
	 * and just be handled if thrown by one of these files.
	 */
	protected final HashSet<Path> monitoredFiles = new HashSet<Path>();
	protected final ExtractionController controller;

	/**
	 * @param controller
	 */
	public FileMonitorDaemon(ExtractionController controller) {
		this.controller = controller;
		setName("Pericles File Watcher");
		setDaemon(true);
		createWatcher();
		start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				observe();
			} catch (InterruptedException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at run() of abstract monitor", e);
			}
		}
	}

	protected void createWatcher() {
		try {
			fileWatcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception at creation of monitor", e);
		}
	}

	protected void observe() throws InterruptedException {
		if (fileWatcher == null) {
			createWatcher();
		}
		WatchKey key = fileWatcher.take();
		for (WatchEvent<?> event : key.pollEvents()) {
			Path modifiedFile = findModifiedFile(key, event);
			if (modifiedFile != null) {
				handleFileModifiedEvent(event, modifiedFile);
				WatchEvent.Kind<?> kind = event.kind();
				Event e = new Event(modifiedFile.toString(), false, getName(),
						modifiedFile.toString());
				e.type = kind.name();
				if (ExtractionController.eventq != null) {
					ExtractionController.eventq.submitEvent(e);
				} else {
					System.out.println("Null event controller!");
				}
			}

		}
		if (!key.reset())
			return;
	}

	private Path findModifiedFile(WatchKey key, WatchEvent<?> event) {
		if (event.context() instanceof Path) {
			Path watchedParentDirectory = (Path) key.watchable();
			Path file = watchedParentDirectory.resolve((Path) event.context());
			if (monitoredFiles.contains(file))
				return file;
		}
		return null;
	}

	private void handleFileModifiedEvent(WatchEvent<?> event, Path file) {
		if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			controller.updateFileDeletion(file);
		} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
			controller.updateFileModification(file);
		}
	}

	public void update() {
		unregister();
		register();
	}

	private void unregister() {
		monitoredFiles.clear();
		for (WatchKey key : watchKeys) {
			key.cancel();
		}
	}

	private void register() {
		for (Profile profile : controller.profileController.getProfiles()) {
			for (Part part : profile.getParts()) {
				registerFile(part.getFile());
			}
		}
	}

	/**
	 * Only the parent directories can be registered.
	 * 
	 * @param path
	 */
	public void registerFile(Path path) {
		if (isDirectory(path)) {
			registerFileParent(path);
		} else {
			registerFileParent(path.getParent());
		}
		this.monitoredFiles.add(path);
	}

	/**
	 * Register directories to watch single files of the testDirectory!
	 * 
	 * @param directory
	 */
	private void registerFileParent(Path directory) {
		if (isDirectory(directory)) {
			try {
				WatchKey key = directory.register(fileWatcher,
						StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);
				watchKeys.add(key);
			} catch (IOException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at registration of files to monitor", e);
			}
		}
	}
}

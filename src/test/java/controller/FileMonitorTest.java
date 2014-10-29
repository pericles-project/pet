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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utility.FileUtils.createDirectory;
import static utility.FileUtils.createFile;
import static utility.FileUtils.deleteFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.Part;
import model.Profile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import configuration.Constants;
import controller.ExtractionController;
import controller.FileMonitorDaemon;

public class FileMonitorTest {

	Path file1;
	Path file2;
	Path testDir;
	Part part1;
	Part part2;
	ExtractionController controller;
	FileMonitorDaemon watcher;

	@Before
	public void setUp() {
		new Constants(null);
		String directoryName = "fileMonitorTestDiretory" + File.separator;
		String fileName1 = "fileMonitorTest1";
		String fileName2 = "fileMonitorTest2";
		testDir = Paths.get(directoryName);
		file1 = Paths.get(fileName1);
		file2 = Paths.get(fileName2);
		createDirectory(testDir);
		createFile(file1);
		createFile(file2);
		part1 = Part.create(file1, fileName1);
		part2 = Part.create(file2, fileName2);
		controller = new ExtractionController();
		watcher = controller.fileMonitorDaemon;
	}

	@After
	public void tearDown() {
		deleteFile(file1);
		deleteFile(file2);
		deleteFile(testDir);
	}

	@Test
	public void createWatcherTest() {
		watcher.fileWatcher = null;
		assertNull(watcher.fileWatcher);
		watcher.createWatcher();
		assertNotNull(watcher.fileWatcher);
		FileMonitorDaemon monitor2 = new FileMonitorDaemon(controller);
		assertTrue(monitor2.isAlive());
	}

	@Test
	public void registerTest() {
		watcher.registerFile(part1.getFile());
		assertTrue(watcher.monitoredFiles.contains(part1.getFile()));
	}

	@Test
	public void registerProfilePartsTest() {
		String profileName = "fileMonitorTestProfile";
		Profile testProfile = controller.profileController
				.createProfile(profileName);
		testProfile.addPart(part1);
		testProfile.addPart(part2);
		assertTrue(watcher.monitoredFiles.contains(part1.getFile()));
		assertTrue(watcher.monitoredFiles.contains(part2.getFile()));
	}

	/*
	 * TODO: FIX
	
	@Test
	public void deletedFileTest() {
		String profileName = "fileMonitorTestProfile";
		Profile testProfile = controller.profileController
				.createProfile(profileName);
		testProfile.addPart(part1);
		testProfile.addPart(part2);
		assertTrue(testProfile.getParts().contains(part1));
		deleteFile(part1.getFile());
		assertFalse(testProfile.getParts().contains(part1));
	}*/

	@Test
	public void updateTest() {
		String profileName = "fileMonitorTestProfile";
		Profile testProfile = controller.profileController
				.createProfile(profileName);
		testProfile.addPart(part1);
		testProfile.addPart(part2);
		assertTrue(watcher.monitoredFiles.contains(part1.getFile()));
		testProfile.removePart(part1);
		assertFalse(testProfile.getParts().contains(part1));
		assertFalse(watcher.monitoredFiles.contains(part1.getFile()));
	}
}

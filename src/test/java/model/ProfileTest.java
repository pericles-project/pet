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

import static configuration.Constants.EXTRACTION_PROFILES_DIRECTORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utility.FileUtils.createDirectory;
import static utility.FileUtils.createFile;
import static utility.FileUtils.deleteDirectory;
import static utility.FileUtils.deleteFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import modules.AbstractModule;
import modules.ChecksumFileModule;
import modules.FileStoreModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import configuration.Constants;
import controller.ExtractionController;

public class ProfileTest {

	String profileID = "profileTest";
	Profile profile;
	Path testFile1;
	Path testFile2;
	Part testPart1;
	Part testPart2;
	AbstractModule testModule1;
	AbstractModule testModule2;

	@Before
	public void setUp() {
		new Constants(null);
		ExtractionController controller = new ExtractionController();
		profile = new Profile(controller.profileController, profileID,
				"testUUID");
		testFile1 = Paths.get("profileTestFile1");
		testFile2 = Paths.get("profileTestFile2");
		createFile(testFile1);
		createFile(testFile2);
		testPart1 = Part.create(testFile1, profileID);
		testPart2 = Part.create(testFile2, profileID);
		testModule1 = new FileStoreModule();
		testModule2 = new ChecksumFileModule();
	}

	@After
	public void tearDown() {
		deleteFile(testFile1);
		deleteFile(testFile2);
	}

	@Test
	public void setNameTest() {
		String newName = "newTestName";
		profile.setName(newName);
		assertEquals(newName, profile.getName());
	}

	@Test
	public void saveLoadTest() {
		profile.removeAllModules();
		profile.removeAllParts();
		assertEquals(0, profile.getParts().size());
		assertEquals(0, profile.getModules().size());
		profile.addPart(testPart1);
		profile.addPart(testPart2);
		profile.addModule(testModule1);
		assertEquals(2, profile.getParts().size());
		assertEquals(1, profile.getModules().size());
		profile.save();
		profile.removeAllParts();
		profile.removeAllModules();
		assertEquals(0, profile.getParts().size());
		assertEquals(0, profile.getModules().size());
		profile.loadModules();
		profile.loadParts();
		assertEquals(2, profile.getParts().size());
		assertEquals(1, profile.getModules().size());
		deleteDirectory(EXTRACTION_PROFILES_DIRECTORY + "testUUID");
	}

	@Test
	public void addRemoveModuleTest() {
		int profileSize = profile.getModules().size();
		for (AbstractModule module : profile.getModules()) {
			System.out.println("module: 	" + module.moduleName);
		}
		profile.addModule(null);
		assertEquals(profileSize, profile.getModules().size());
		profile.addModule(testModule1);
		assertEquals(profileSize + 1, profile.getModules().size());
		profile.removeModule(testModule1);
		assertEquals(profileSize, profile.getModules().size());
	}

	@Test
	public void addRemovePartTest() {
		assertEquals(0, profile.getParts().size());
		profile.addPart(null);
		assertEquals(0, profile.getParts().size());
		profile.addPart(testPart1);
		assertEquals(1, profile.getParts().size());
		profile.removePart(testPart1);
		assertEquals(0, profile.getParts().size());
	}

	@Test
	public void addRemoveAllModulesTest() {
		List<AbstractModule> modules = new ArrayList<AbstractModule>();
		modules.add(testModule1);
		modules.add(testModule2);
		int moduleSize = profile.getModules().size();
		profile.addAllModules(modules);
		assertEquals(moduleSize + 2, profile.getModules().size());
		profile.removeAllModules();
		assertEquals(0, profile.getModules().size());
	}

	@Test
	public void addRemoveAllPartsTest() {
		List<Part> parts = new ArrayList<Part>();
		parts.add(testPart1);
		parts.add(testPart2);
		assertEquals(0, profile.getParts().size());
		profile.addPart(testPart1);
		assertEquals(1, profile.getParts().size());
		profile.removeAllParts();
		assertEquals(0, profile.getParts().size());
	}

	@Test
	public void getPartsFromDirectoryAndPathsTest() {
		assertEquals(0, profile.getParts().size());
		profile.getPartsFromDirectoryFiles(null);
		assertEquals(0, profile.getParts().size());
		File notExistingDir = new File("doesntExist");
		profile.getPartsFromDirectoryFiles(notExistingDir);
		assertEquals(0, profile.getParts().size());
		String dir = "profileTestDirectory" + File.separator;
		deleteDirectory(dir);
		createDirectory(dir); // empty dir:
		assertEquals(0, new File(dir).listFiles().length);
		HashSet<Part> parts = profile.getPartsFromDirectoryFiles(new File(dir));
		assertEquals(0, parts.size());
		String file1 = dir + "testFile1";
		String file2 = dir + "testFile2";
		String subDir = dir + "profileTestSubDir" + File.separator;
		createFile(file1);
		createFile(file2);
		createDirectory(subDir); // 2 files, 1 sub dir:
		assertEquals(3, new File(dir).listFiles().length);
		parts = profile.getPartsFromDirectoryFiles(new File(dir));
		assertEquals(2, parts.size()); // 2 files = 2 parts
		// Now: get parts from paths set:
		profile.removeAllParts();
		assertEquals(0, profile.getParts().size());
		HashSet<Path> paths = new HashSet<Path>();
		paths.add(Paths.get(file1));
		paths.add(Paths.get(file2));
		paths.add(Paths.get(subDir));
		paths.add(Paths.get("notExistingPath"));
		profile.addAllPartsFromPaths(paths, true);
		assertEquals(2, profile.getParts().size());
		// Try to add parts, that are already added:
		profile.addAllPartsFromPaths(paths, true);
		profile.addPart(Part.create(Paths.get(file1), profile.getUUID()));
		assertEquals(2, profile.getParts().size());
		deleteFile(file1);
		deleteFile(file2);
		deleteDirectory(subDir);
		deleteDirectory(dir);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void deleteMetadataTest() {
		profile.addPart(testPart1);
		profile.addPart(testPart2);
		testPart1.addExtractionResult(new ExtractionResult());
		testPart1.addExtractionResult(new ExtractionResult());
		testPart1.addExtractionResult(new ExtractionResult());
		testPart2.addExtractionResult(new ExtractionResult());
		profile.getEnvironment().addExtractionResult(new ExtractionResult());
		profile.getEnvironment().addExtractionResult(new ExtractionResult());
		assertTrue(profile.getEnvironment().extractionResults.size() > 0);
		for (Part part : profile.getParts()) {
			assertTrue(part.extractionResults.size() > 0);
		}
		profile.deleteAllMetadata();
		assertFalse(profile.getEnvironment().extractionResults.size() > 0);
		for (Part part : profile.getParts()) {
			assertFalse(part.extractionResults.size() > 0);
		}
	}
}

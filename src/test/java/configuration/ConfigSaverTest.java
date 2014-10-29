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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utility.FileUtils.createFile;
import static utility.FileUtils.deleteFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import model.Part;
import modules.AbstractModule;
import modules.FileIdentificationCommandModule;
import modules.GeneralExecutableModuleConfig;
import modules.GeneralNativeCommandModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import storage.ConfigSaver;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ConfigSaverTest {

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void hashTest() {
		assertEquals("", ConfigSaver.hash(null));
	}

	// uses savePathSet(), too
	@Test
	public void saveAndLoadPartSetTest() {
		HashSet<Part> parts = new HashSet<Part>();
		String fileName1 = "partSaveTest1";
		String fileName2 = "partSaveTest2";
		Path path1 = Paths.get(fileName1);
		Path path2 = Paths.get(fileName2);
		createFile(fileName1);
		createFile(fileName2);
		Part part1 = Part.create(path1, fileName1);
		Part part2 = Part.create(path2, fileName2);
		assertNotNull(part1);
		assertNotNull(part2);
		parts.add(part1);
		parts.add(part2);
		String outputFileName = "saveAndLoadPartSetTest";
		ConfigSaver.savePartSet(parts, outputFileName);
		HashSet<Path> loadedPaths = ConfigSaver.loadPathSet(outputFileName);
		assertEquals(loadedPaths.size(), 2);
		assertTrue(loadedPaths.contains(path1.toAbsolutePath()));
		assertTrue(loadedPaths.contains(path2.toAbsolutePath()));
	}

//	@Test
//	public void saveAndLoadConfiguration() throws JsonGenerationException,
//			JsonMappingException, IOException {
//		ByteArrayOutputStream b = new ByteArrayOutputStream(1000);
//
//		FileIdentificationCommandModule module1 = new FileIdentificationCommandModule();
//		module1.setDeep(true);
//		((FIConfig) module1.getConfig()).options = "-h";
//		ConfigSaver.saveModuleConfiguration(b, module1.getConfig());
//
//		ByteArrayInputStream bai = new ByteArrayInputStream(b.toByteArray());
//		FileIdentificationCommandModule module2 = new FileIdentificationCommandModule();
//		module2.setConfig(ConfigSaver.loadModuleConfiguration(bai));
//		assertEquals(module1.isDeep(), module2.isDeep());
//		assertEquals(((FIConfig) module1.getConfig()).options, "-h");
//	}
//
//	@Test
//	public void saveAndLoadComplexConfigTest() {
//		String fileName = "saveAndLoadComplexConfigTest";
//		File file = new File(fileName);
//		FileIdentificationCommandModule savedModule = new FileIdentificationCommandModule();
//		savedModule.setDeep(false);
//		ConfigSaver.saveModuleConfiguration(file, savedModule.getConfig());
//		AbstractModule loadedModule = new FileIdentificationCommandModule();
//		loadedModule.setConfig(ConfigSaver.loadModuleConfiguration(file));
//		assertEquals(savedModule.isSelected(), loadedModule.isSelected());
//		assertEquals(savedModule.moduleName, loadedModule.moduleName);
//		assertEquals(savedModule.version, loadedModule.version);
//		assertEquals(savedModule.getHash(), loadedModule.getHash());
//		assertEquals(savedModule.getConfig().getSupportedSystems(),
//				loadedModule.getConfig().getSupportedSystems());
//
//		GeneralNativeCommandModule module2 = new GeneralNativeCommandModule();
//		module2.getConfig().commandNameOrPath = "file";
//		// x.getConfig().supportedSystems = new String[] {
//		// OsName.SYSTEM_INDEPENDENT.toString(), OsName.OS_X.toString() };
//		module2.getConfig().WINcommandNameOrPath = "nativeTools/file_utility_windows/bin/file.exe";
//		ConfigSaver.saveModuleConfiguration(file, module2.getConfig());
//		AbstractModule loadedModule2 = new GeneralNativeCommandModule();
//		loadedModule2.setConfig(ConfigSaver.loadModuleConfiguration(file));
//		assertTrue(loadedModule2.getConfig() instanceof GeneralExecutableModuleConfig);
//		assertEquals(
//				module2.getConfig().WINcommandNameOrPath,
//				((GeneralExecutableModuleConfig) loadedModule2.getConfig()).WINcommandNameOrPath);
//
//		deleteFile(fileName);
//	}
}

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
package utility;

import java.io.IOException;
import java.nio.file.*;


import org.junit.Test;

import utility.FileUtils;
import static org.junit.Assert.*;

public class FileUtilsTest {

	@Test
	public void createFileTest() {
		Path file = Paths.get("FileUtilsTestFile");
		assertFalse(Files.isRegularFile(file));
		FileUtils.createFile(file);
		assertTrue(Files.isRegularFile(file));
		deleteFile(file);
		assertFalse(Files.isReadable(file));
	}

	@Test
	public void createDirectoryTest() {
		String directoryString = "FileUtilsTestDirectory";
		Path directory = Paths.get(directoryString);
		assertFalse(Files.isDirectory(directory));
		FileUtils.createDirectory(directory);
		assertTrue(Files.isDirectory(directory));
		deleteFile(directory);
		assertFalse(Files.isDirectory(directory));
	}

	private void deleteFile(Path file) {
		try {
			Files.delete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteFileTest() {
		Path file = Paths.get("FileUtilsTest");
		FileUtils.createFile(file);
		assertTrue(Files.exists(file));
		FileUtils.deleteFile(file);
		assertFalse(Files.exists(file));
	}

	@Test
	public void fileExistsTest() {
		String stringPath = "FileUtilsExistsTest";
		Path file = Paths.get(stringPath);
		FileUtils.createDirectory(file);
		assertTrue(Files.exists(file));
		assertTrue(FileUtils.fileExists(file));
		FileUtils.deleteFile(file);
		assertFalse(FileUtils.fileExists(file));
	}

	@Test
	public void isDirectoryTest() {
		String directoryPath = "FileUtilsIsDirectoryTest";
		Path directory = Paths.get(directoryPath);
		FileUtils.createFile(directory);
		assertFalse(FileUtils.isDirectory(directoryPath));
		FileUtils.deleteFile(directory);
		FileUtils.createDirectory(directory);
		assertTrue(FileUtils.isDirectory(directoryPath));
		FileUtils.deleteDirectory(directoryPath);
		assertFalse(FileUtils.isDirectory(directoryPath));
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(FileUtils.isDirectory(directoryPath));
		FileUtils.deleteDirectory(directoryPath);
	}
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utility.FileUtils.createFile;
import static utility.FileUtils.deleteFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import model.ExtractionResult;
import modules.RegexModule.RegexMatch;
import modules.configuration.RegexConfig;

import org.junit.Test;

public class RegexModuleTest {

	@SuppressWarnings("unchecked")
	@Test
	public void regexModuleTest() throws Exception {
		RegexModule module = new RegexModule();
		String text = "ABBCDFGGHIJJJKKK";
		String testFileName = "regexTestFile";
		createFile(testFileName);
		Path path = Paths.get(testFileName);
		FileWriter fw = new FileWriter(path.toFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(text);
		bw.close();
		String regex = "B";
		assertTrue(module.getConfig() instanceof RegexConfig);
		assertFalse(((RegexConfig) module.getConfig()).regexIsValid());
		((RegexConfig) module.getConfig()).regularExpression = regex;
		// ((RegexConfig) module.getConfig()).noExtension = true;
		assertTrue(((RegexConfig) module.getConfig()).regexIsValid());
		assertTrue(module.isSelected());
		ExtractionResult result = module.extractInformation(path);
		Object matches = result.results;
		assertTrue(matches instanceof List<?>);
		RegexMatch match = null;
		if (matches instanceof List<?>) {
			assertEquals(2, ((List<?>) matches).size());
			match = ((List<RegexMatch>) matches).get(0);
			// Check indices of B's:
			assertEquals(1, ((RegexMatch) ((List<?>) matches).get(0)).start);
			assertEquals(2, ((RegexMatch) ((List<?>) matches).get(1)).start);
		}
		assertNotNull(match);
		deleteFile(testFileName);
	}
}

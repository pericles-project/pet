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

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.ExtractionResult;
import model.KeyValueResult;
import modules.configuration.ChecksumFileModuleConfig;

import org.junit.Test;

public class ModuleTests {

	@Test
	public void fileIdModuleTest() throws URISyntaxException {
		FileIdentificationCommandModule f = new FileIdentificationCommandModule();
		URL u = this.getClass().getResource("/test1.pdf");
		Path t1 = Paths.get(u.toURI());
		ExtractionResult r = f.extractInformation(t1);
		KeyValueResult res = (KeyValueResult) r.results;
		assertEquals(res.results.get("mime_type"), "application/pdf");
	}

	@Test
	public void generalNativeModuleTest() throws URISyntaxException {
		GeneralNativeCommandModule f = new GeneralNativeCommandModule();
		URL u = this.getClass().getResource("/test1.pdf");
		Path t1 = Paths.get(u.toURI());
		GeneralExecutableModuleConfig c = f.getConfig();
		c.commandNameOrPath = "file";
		c.defaults = c.options = "--mime-type -b";
		c.helpOption = "--help";
		c.WINcommandNameOrPath = "nativeTools/file_utility_windows/bin/file.exe";
		// System.out.println(f.getModuleDescription());
		ExtractionResult r = f.extractInformation(t1);
		KeyValueResult res = (KeyValueResult) r.results;
		assertEquals(res.results.get("fullOutput"), "application/pdf");
	}

	@Test
	public void checksumModuleTest() throws Exception {
		ChecksumFileModule f = new ChecksumFileModule();
		URL u = this.getClass().getResource("/test1.pdf");
		Path t1 = Paths.get(u.toURI());
		ExtractionResult r = f.extractInformation(t1);
		KeyValueResult res = (KeyValueResult) r.results;
		// System.out.println(res.map.get("checksum"));
		assertEquals(res.results.get("checksum"),
				"46f202b356d0d5502f742af8687755c2");
		assertEquals(res.results.get("checksum_type"),
				((ChecksumFileModuleConfig) f.getConfig()).algorithm);
		((ChecksumFileModuleConfig) f.getConfig()).algorithm = "SHA1";
		r = f.extractInformation(t1);
		res = (KeyValueResult) r.results;
		// System.out.println(res.map.get("checksum"));
		assertEquals(res.results.get("checksum"),
				"b40b5bfee82094fbf1d8f635dc05deaf765d8cb4");
		assertEquals(res.results.get("checksum_type"), "SHA1");
	}
}

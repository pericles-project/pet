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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class FileFilterTest {

	FileFilter filter;
	URL pdfUrl;
	URL txtUrl;
	Path pdf;
	Path txt;
	String pdfMediaType = "application";
	String pdfMimeType = "application/pdf";
	String txtMediaType = "text";
	String txtMimeType = "text/plain";

	@Before
	public void setUp() {
		this.filter = new FileFilter();
		pdfUrl = this.getClass().getResource("/test1.pdf");
		txtUrl = this.getClass().getResource("/test_txt.txt");
		pdf = Paths.get(pdfUrl.getPath());
		txt = Paths.get(txtUrl.getPath());
	}

	@Test
	public void plainFilterTest() {
		assertTrue(filter.fileTypeSupported(pdf));
		assertTrue(filter.fileTypeSupported(txt));
	}

	@Test
	public void inclusiveMimeTypeTest() {
		filter.addInclusiveMimeType(pdfMimeType);
		assertTrue(filter.fileTypeSupported(pdf));
		assertFalse(filter.fileTypeSupported(txt));
		// Inclusive > exclusive:
		filter.addExclusiveMediaType(pdfMediaType);
		assertTrue(filter.fileTypeSupported(pdf));
	}

	@Test
	public void exclusiveMimeTypeTest() {
		filter.addExclusiveMediaType(pdfMediaType);
		assertTrue(filter.fileTypeSupported(txt));
		assertFalse(filter.fileTypeSupported(pdf));
	}

	@Test
	public void inclusiveMediaTypeTest() {
		filter.addInclusiveMediaType(pdfMediaType);
		assertTrue(filter.fileTypeSupported(pdf));
		assertFalse(filter.fileTypeSupported(txt));
	}

	@Test
	public void exclusiveMediaTypeTest() {
		filter.addExclusiveMediaType(pdfMediaType);
		assertTrue(filter.fileTypeSupported(txt));
		assertFalse(filter.fileTypeSupported(pdf));
		filter.addInclusiveMimeType(pdfMimeType);
		assertTrue(filter.fileTypeSupported(pdf));
	}
}

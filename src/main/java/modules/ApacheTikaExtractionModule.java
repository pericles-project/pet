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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.logging.Level;

import model.ExtractionResult;
import model.KeyValueResult;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import configuration.Log;

public class ApacheTikaExtractionModule extends AbstractFileDependentModule {

	public static Tika tika = new Tika();

	@Override
	public void setModuleName() {
		moduleName = "Apache Tika extractor";
	}

	@Override
	public ExtractionResult extractInformation(Path path) {
		// Logger.getLogger("module.tik").setLevel(Level.SEVERE);

		ExtractionResult extractionResult = new ExtractionResult(this);

		File file = path.toFile();
		if (!file.exists() || !file.canRead() || file.isDirectory())
			return extractionResult;

		try {
			Metadata m = new Metadata();
			KeyValueResult r = new KeyValueResult();
			r.add("parsed_contents",
					tika.parseToString(Files.newInputStream(path), m));
			for (String k : m.names()) {
				String[] v = m.getValues(k);
				int n = 0;
				for (String vv : v) {
					r.add(n > 0 ? (k + n) : k, vv);
				}
			}
			extractionResult.setResults(r);
		} catch (IOException e) {
			Log.EXCEPTION_LOGGER.log(Level.SEVERE, "IO error", e);
		} catch (InvalidParameterException x) {
			// System.out.println("not a pdf " + path);
		} catch (TikaException e) {
			Log.EXCEPTION_LOGGER.log(Level.SEVERE, "Tika error", e);
		}
		return extractionResult;
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	@Override
	public String getModuleDescription() {
		return "Apache Tika extractor";
	}
}

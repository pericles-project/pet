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

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.ExtractionResult;
import model.KeyValueResult;
import modules.configuration.ChecksumFileModuleConfig;

public class ChecksumFileModule extends AbstractFileDependentModule {
	public ChecksumFileModule() {
		super();
		this.setConfig(new ChecksumFileModuleConfig(moduleName, version));
	}

	@Override
	public void setModuleName() {
		moduleName = "Calculate file checksum";
	}

	@Override
	public ExtractionResult extractInformation(Path path) {
		ExtractionResult extractionResult = new ExtractionResult(this);
		try {
			KeyValueResult result = new KeyValueResult("ChecksumResults");
			result.add("checksum_type",
					((ChecksumFileModuleConfig) getConfig()).algorithm);
			result.add(
					"checksum",
					getMD5Checksum(path,
							((ChecksumFileModuleConfig) getConfig()).algorithm));

			extractionResult.setResults(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extractionResult;
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	public static byte[] createChecksum(Path filename, String type)
			throws Exception {
		InputStream fis = new FileInputStream(filename.toFile());
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance(type);
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	public static String getMD5Checksum(Path filename, String type)
			throws Exception {
		byte[] b = createChecksum(filename, type);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	@Override
	public String getModuleDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("This module will compute checksums based on the default JDK method\n");
		sb.append("Your current Java implementation spports these values (to be passed as option):\n");
		String[] names = getCryptoImpls("MessageDigest");
		for (String name : names) {
			sb.append(name).append('\n');
		}
		sb.append("Default option: ").append("MD5");
		return sb.toString();
	}

	// This method returns the available implementations for a service type
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String[] getCryptoImpls(String serviceType) {
		Set result = new HashSet();
		// All all providers
		java.security.Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			// Get services provided by each provider
			Set keys = providers[i].keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				key = key.split(" ")[0];
				if (key.startsWith(serviceType + ".")) {
					result.add(key.substring(serviceType.length() + 1));
				} else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
					// This is an alias
					result.add(key.substring(serviceType.length() + 11));
				}
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	public static void main(String[] args) {
		ChecksumFileModule c = new ChecksumFileModule();
		System.out.println(c.getModuleDescription());
		FileIdentificationCommandModule f = new FileIdentificationCommandModule();
		System.out.println(f.getModuleDescription());
	}
}

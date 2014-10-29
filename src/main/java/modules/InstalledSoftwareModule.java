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

import static configuration.Log.MODULE_LOGGER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;

import model.ExtractionResult;
import model.KeyValueResult;
import model.OperatingSystem;
import model.OperatingSystem.OsName;

public class InstalledSoftwareModule extends GeneralNativeCommandModuleEnv {

	public InstalledSoftwareModule() {
		super();
		setModuleName();
		wrapped.moduleName = moduleName;
		GeneralExecutableModuleConfig config = new GeneralExecutableModuleConfig(
				moduleName, version);
		setConfig(config);
		config.addSupportedSystem(OsName.LINUX);
		config.addSupportedSystem(OsName.OS_X);
		config.addSupportedSystem(OsName.WINDOWS);

		OperatingSystem currentOS = OperatingSystem.getCurrentOS();
		switch (currentOS.genericName) {
		case LINUX:
			configureLinux(config, currentOS);
			break;
		case BSD:
			configureBSD(config);
			break;
		case WINDOWS:
			configureWindows(config);
			break;
		case OS_X:
			configureOSX(config);
			break;
		case SOLARIS:
		case SYSTEM_INDEPENDENT:
		case UNKNOWN:
		default:
			break;
		}
		config.noHelp = true;
	}

	private void configureWindows(GeneralExecutableModuleConfig config) {
		// NOT TESTED!!!
		config.WINcommandNameOrPath = "wmic";
		config.Winoptions = "product";
	}

	private void configureBSD(GeneralExecutableModuleConfig config) {
		// NOT TESTED!!!
		config.BSDcommandNameOrPath = "pkg_info";
	}
	private void configureOSX(GeneralExecutableModuleConfig config) {
		config.OSXcommandNameOrPath = "mdfind";
		config.OSXoptions = "kMDItemContentType=com.apple.application-bundle";
	}
	
	

	private void configureLinux(GeneralExecutableModuleConfig config,
			OperatingSystem currentOS) {
		if (currentOS.version.contains("ARCH")) {
			config.LinuxcommandNameOrPath = "pacman";
			config.Linuxoptions = "-Qe";
		} else if (currentOS.version.contains("ebian")) {
			// NOT TESTED!!!
			config.LinuxcommandNameOrPath = "dpkg";
			config.Linuxoptions = "--list";
		} else if (currentOS.version.contains("redhat")) {
			// NOT TESTED!!!
			config.LinuxcommandNameOrPath = "rpm";
			config.Linuxoptions = "-qa";
		}
	}

	@Override
	public void setModuleName() {
		this.moduleName = "Installed software snapshot";
	}

	@Override
	public void setVersion() {
		this.version = "0.1";
	}

	@Override
	public ExtractionResult extractInformation() {
		ExtractionResult result = new ExtractionResult(this);
		if (OperatingSystem.getCurrentOS().genericName == OsName.OS_X) {
			ExtractionResult extractionResult = super.extractInformation();
			KeyValueResult keyValueResult = (KeyValueResult) extractionResult.results;
			result.setResults(keyValueResult);
			NSObject o;
			try {
				HashSet<String> s = new HashSet<String>();
				o = PropertyListParser.parse("/Library/Receipts/InstallHistory.plist");
				result.setResults(keyValueResult);
				if (o instanceof NSArray) {
					NSArray oo = (NSArray) o;
					keyValueResult.results.put("raw history",oo.toXMLPropertyList());
					int n = 0;
					for (NSObject h : oo.getArray()) {
						if (h instanceof NSDictionary) {
							NSDictionary dic = (NSDictionary) h;
							boolean t = s.add(dic.get("displayName").toString());
							if (t) {
								n++;
								keyValueResult.results.put("Software"+(n++),dic.get("displayName").toString());
							}
						}
					}
				}
			
			} catch (IOException
					| PropertyListFormatException | ParseException
					| ParserConfigurationException | SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			//				for (String name : extract) {
			//					addItem(name, oo, keyValueResult);
			//				}

		} else {
			try {
				ExtractionResult extractionResult = super.extractInformation();
				KeyValueResult keyValueResult = (KeyValueResult) extractionResult.results;
				result.setResults(keyValueResult);
			} catch (Exception e) {
				MODULE_LOGGER
				.severe("Exception in module InstalledSoftwareModule at extraction.");
			}
		}
		return result;
	}

	@Override
	public String getModuleDescription() {
		return "This module extracts a snapshot of all installed software on the system."
				+ "\nNote that the module queries the standard operating system software"
				+ " installation component (e.g. package manager) for the list, and won't "
				+ "recognise software that "
				+ "was installed without the knowlede of these component.\n"
				+ "If your operating system, or distribution, is not supported yet, you can"
				+ " add the missing command at the modules.InstalledSoftwareModule class.";
	}
}

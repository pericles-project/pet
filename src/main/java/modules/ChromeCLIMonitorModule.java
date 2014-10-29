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

import java.util.HashSet;

import model.Event;
import model.OperatingSystem.OsName;
import modules.LSOFMonitoringDaemon.LSOFout;

import com.google.common.collect.Sets;

public class ChromeCLIMonitorModule extends NativeProcessIterativeDaemonModule {

	public ChromeCLIMonitorModule() {
		super();
		getConfig().enabled = false;
		getConfig().OSXcommandNameOrPath = "nativeTools/chrome-cli";
		getConfig().addSupportedSystem(OsName.OS_X);
		getConfig().defaults = getConfig().options = "list links";
		getConfig().helpOption = "-h";
		getConfig().waitBetweenCalls = 5000;
		getConfig().recordEvents = true;
	}

	@Override
	public void setModuleName() {
		moduleName = "Google chrome opened tabs monitoring";
	}

	HashSet<String> prev;

	@Override
	public void processOut(String extractNative) {
		HashSet<String> res = new HashSet<String>();
		String lines[] = extractNative.split("\\r?\\n");
		for (int n = 0; n < lines.length; n++) {
			if (lines[n].startsWith("["))
				res.add(lines[n].substring(lines[n].indexOf(' ')+1));
		}
		if (prev != null) {
			for (String o : Sets.difference(prev, res)) {
				disappear(o);
			}
			for (String o : Sets.difference(res, prev)) {
				appear(o);
			}
		} else {
			for (String o : res) {
				appear(o);
			}
		}
		prev = res;
	}

	private void appear(String o) {
		
			Event e = new Event(o, false, this, o);
			e.type = "appear";
			submitEvent(e);
		
	}

	private void disappear(String o) {
		Event e = new Event(o, false, this, o);
		e.type = "disappear";
		submitEvent(e);
	
	}
}

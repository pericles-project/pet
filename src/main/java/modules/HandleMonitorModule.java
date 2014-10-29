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

public class HandleMonitorModule extends NativeProcessIterativeDaemonModule {

	public HandleMonitorModule() {
		super();
		getConfig().enabled = false;
		getConfig().WINcommandNameOrPath = "nativeTools/handle.exe";
		getConfig().addSupportedSystem(OsName.WINDOWS);
		getConfig().defaults = getConfig().options = "-u";
		getConfig().helpOption = "-h";
		getConfig().waitBetweenCalls = 1000;
		getConfig().recordEvents = true;
	}

	@Override
	public void setModuleName() {
		moduleName = "Windows Handle monitoring daemon";
	}

	static String divider = "----------------";
	HashSet<LSOFout> prev;

	@Override
	public void processOut(String extractNative) {
		HashSet<LSOFout> res = new HashSet<LSOFout>();
		String lines[] = extractNative.split("\\r?\\n");
		for (int n = 0; n < lines.length; n++) {
			// start of a new process
			if (lines[n].startsWith(divider) && ++n < lines.length) {
				String[] header = lines[n].split("\\s+", 5);
				String name = header[0];
				long pid = Long.parseLong(header[2]);
				n++;
				while (n < lines.length && !lines[n].startsWith(divider)) {
					String[] current = lines[n].split("\\s+", 5);
					LSOFout o = new LSOFout();
					o.command = name;
					o.pid = pid;
					o.node = current[1];
					o.type = current[2];
					if (current.length > 4) {
						o.FD = current[3];
						o.name = current[4];
						if (!name.equals("handle.exe")) {
							res.add(o);
						}
					}
					n++;
				}
			}
		}
		if (prev != null) {
			for (LSOFout o : Sets.difference(prev, res)) {
				disappear(o);
			}
			for (LSOFout o : Sets.difference(res, prev)) {
				appear(o);
			}
		} else {
			for (LSOFout o : res) {
				appear(o);
			}
		}
		prev = res;
	}

	private void appear(LSOFout o) {
		if (config.fileFilter.fileExtensionSupported(o.name)) {
			Event e = new Event(o, false, this, o.name);
			e.type = "appear";
			submitEvent(e);
		}
	}

	private void disappear(LSOFout o) {
		if (config.fileFilter.fileExtensionSupported(o.name)) {
			Event e = new Event(o, false, this, o.name);
			e.type = "disappear";
			submitEvent(e);
		}
	}
}

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import model.Event;
import modules.ProcessExtractionModules.ProcessInformation;
import modules.configuration.LSOFConfig;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

public class LSOFMonitoringDaemon extends NativeProcessMonitoringDaemonModule {

	@Override
	public String getModuleDescription() {
		return "Module for monitoring filesystem and network connections based on the lsof command. ";

	};

	public static class LSOFout {
		public String command;
		public long pid;
		public String user;
		public String FD;
		public String type;
		public String device;
		public String size;
		public String node;
		public String name;
		public String fullProcessName;
		public String fullProcesspath;
		public List<String> processArguments;

		@JsonIgnore
		public String fullLine;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			// if (fullLine!=null)
			// return fullLine.hashCode();
			result = prime * result + ((FD == null) ? 0 : FD.hashCode());
			result = prime * result
					+ ((command == null) ? 0 : command.hashCode());
			result = prime * result
					+ ((device == null) ? 0 : device.hashCode());
			// result = prime * result
			// + ((fullLine == null) ? 0 : fullLine.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((node == null) ? 0 : node.hashCode());
			result = prime * result + (int) (pid ^ (pid >>> 32));
			result = prime * result + ((size == null) ? 0 : size.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		public LSOFout addProcessInformation(ProcessInformation p) {
			fullProcessName = p.processName;
			fullProcesspath = p.processFullPath;
			processArguments = p.arg;
			return this;
		}

		public static LSOFout parseLine(String line) {
			String[] split = line.split(SPACECHARS, 9);
			if (split.length < 9 || split[1].equals("PID"))
				// System.out.println(line);
				return null;
			LSOFout o = new LSOFout();
			o.fullLine = line;
			try {
				o.command = split[0];
				o.pid = Integer.parseInt(split[1]);
				o.user = split[2];
				o.FD = split[3];
				o.type = split[4];
				o.device = split[5];
				o.size = split[6];
				o.node = split[7];
				o.name = split[8];
			} catch (Exception x) {
				x.printStackTrace();
				// System.out.println(line);
			}
			return o;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof LSOFout))
				return false;
			LSOFout other = (LSOFout) obj;
			// if (fullLine == null) {
			// if (other.fullLine != null) {
			// return false;
			// }
			// } else if (fullLine.equals(other.fullLine)) {
			// return true;
			// }
			if (FD == null) {
				if (other.FD != null)
					return false;
			} else if (!FD.equals(other.FD))
				return false;
			if (command == null) {
				if (other.command != null)
					return false;
			} else if (!command.equals(other.command))
				return false;
			if (device == null) {
				if (other.device != null)
					return false;
			} else if (!device.equals(other.device))
				return false;

			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (node == null) {
				if (other.node != null)
					return false;
			} else if (!node.equals(other.node))
				return false;
			if (pid != other.pid)
				return false;
			if (size == null) {
				if (other.size != null)
					return false;
			} else if (!size.equals(other.size))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

	}

	public static final String SPACECHARS = "\\s+";

	HashSet<LSOFout> prev = new HashSet<LSOFout>();
	HashSet<LSOFout> current = new HashSet<LSOFout>();
	public static HashMap<Long, ProcessInformation> pidCache = new HashMap<Long, ProcessExtractionModules.ProcessInformation>();

	public LSOFMonitoringDaemon() {
		super();
		setConfig(new LSOFConfig(moduleName, version));
	}

	@Override
	protected void processLine(String line) {
		// System.out.println(line);
		if (line.trim().equals("=======")) {
			for (LSOFout o : Sets.difference(prev, current)) {

				if (o != null) {

					disappear(o);
				}
			}
			for (LSOFout o : Sets.difference(current, prev)) {

				if (o != null) {

					appear(o);

				}
			}

			HashSet<LSOFout> t = prev;
			prev = current;
			current = t;
			current.clear();
		} else {
			LSOFout o = LSOFout.parseLine(line);
			current.add(o);
		}
	}

	private void appear(LSOFout o) {
		if (config.fileFilter.fileExtensionSupported(o.name)) {
			ProcessInformation p = pidCache.get(o.pid);
			if (p == null) {
				p = ProcessExtractionModules.ProcessParameterExtractor
						.extractProcessInformation(o.pid);
			}
			if (p != null) {
				o.addProcessInformation(p);
			}
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

	@Override
	public void setModuleName() {
		moduleName = "LSOF use monitor";
	}

	public static void main(String[] args) {
		// LSOFMonitoringDaemon x = new LSOFMonitoringDaemon();
		// StorageController.storage = new FileStorageInterface();
		// ExtractionController.eventq = new EventController(null);
		// ExtractionController.eventq.addProcessor(new
		// StorageEventProcessor());
		//
		// try {
		// x.isDaemon = false;
		// x.getConfig().recordEvents = true;
		// ((LSOFConfig) x.getConfig()).commandNames = new String[] { "java" };
		// ((LSOFConfig) x.getConfig()).monitorFiles = true;
		// x.setSelected(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

}

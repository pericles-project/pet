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

import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;

import model.ExtractionResult;
import model.KeyValueResult;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NfsFileSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

public class SigarEnvironmentModules {
	public static SigarProxy proxy = SigarProxyCache.newInstance(new Sigar());

	public static class SigarCpuModule extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				Cpu cpu = proxy.getCpu();
				moduleResults.setResults(cpu.toMap());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract information"
					+ "about the system CPU."
					+ "\n\n- Total system cpu idle time"
					+ "\n- Total system cpu time servicing interrupts"
					+ "\n- Total system cpu nice time"
					+ "\n- Total system cpu time servicing softirqs"
					+ "\n- Get the Total system cpu time servicing softirqs"
					+ "\n- Total system cpu involuntary wait time"
					+ "\n- Get the Total system cpu kernel time"
					+ "\n- Total system cpu time"
					+ "\n- Total system cpu user time"
					+ "\n- Total system cpu io wait time";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "CPU usage monitoring";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarCpuInfo extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getCpuInfoList());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract information"
					+ "about the system CPU."
					+ "\n\n- cache size"
					+ "\n- number of cores per socket"
					+ "\n- speed in MHz"
					+ "\n- model"
					+ "\n- number of total cores (logical)"
					+ "\n- number of total sockets (physical)"
					+ "\n- vendor id";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "CPU specification snapshot";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarFileSystem extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				FileSystem[] s;
				s = proxy.getFileSystemList();
				LinkedList<FileSystem> l = new LinkedList<FileSystem>();
				Collections.addAll(l, s);
				for (FileSystem c : l)
					if (c instanceof NfsFileSystem) {
						l.remove(c);
					}
				moduleResults.setResults(l);
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract information"
					+ "about the file system."
					+ "\n\n- Device name"
					+ "\n- Directory name"
					+ "\n- File system flags"
					+ "\n- File system mount options"
					+ "\n- File system os specific type name"
					+ "\n- File system type"
					+ "\n- File system generic type name";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "File system information snapshot";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarMemoryInfo extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getMem());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract information"
					+ "about the system memory."
					+ "\n\n- Actual total free system memory"
					+ "\n- Actual total used system memory"
					+ "\n- Total free system memory"
					+ "\n- Percent total free system memory"
					+ "\n- System Random Access Memory (in MB)"
					+ "\n- Total system memory"
					+ "\n- Total used system memory"
					+ "\n- Percent total used system memory";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "Memory monitoring";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarNetworkConfig extends AbstractEnvironmentModule {
		class NetworkInformation {
			public NetInfo netInfo;
			public NetInterfaceConfig netConfig;
		}

		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				NetworkInformation infos = new NetworkInformation();
				infos.netInfo = proxy.getNetInfo();
				infos.netConfig = proxy.getNetInterfaceConfig();
				moduleResults.setResults(infos);
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract network configuration information."
					+ "\n\n- address"
					+ "\n- broadcast"
					+ "\n- description"
					+ "\n- flags"
					+ "\n- hwaddr"
					+ "\n- metric"
					+ "\n- mtu"
					+ "\n- name"
					+ "\n- netmask"
					+ "\n- type"
					+ "\n- default_gateway"
					+ "\n- domain_name"
					+ "\n- host_name" + "\n- primary_dns" + "\n- secondary_dns";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "Network information";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarNetworkInterfaces extends
			AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getNetInterfaceList());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract a list of network interfaces.";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "List of network interfaces";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarProcStat extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getProcStat());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract"
					+ "system process statistics."
					+ "\n\n- Total number of processes in idle state"
					+ "\n- Total number of processes in run state"
					+ "\n- Total number of processes in sleep state"
					+ "\n- Total number of processes in stop state"
					+ "\n- Total number of threads"
					+ "\n- Total number of processes"
					+ "\n- Total number of processes in zombie state";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "Process statistics monitoring";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarSystemResources extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getResourceLimit());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract information"
					+ "about the system resources."
					+ "\n\n- core_cur"
					+ "\n- core_max"
					+ "\n- cpu_cur"
					+ "\n- cpu_max"
					+ "\n- data_cur"
					+ "\n- data_max"
					+ "\n- file_size_cur"
					+ "\n- file_size_max"
					+ "\n- memory_cur"
					+ "\n- memory_max"
					+ "\n- open_files_cur"
					+ "\n- open_files_max"
					+ "\n- pipe_size_cur"
					+ "\n- pipe_size_max"
					+ "\n- processes_cur"
					+ "\n- processes_max"
					+ "\n- stack_cur"
					+ "\n- stack_max"
					+ "\n- virtual_memory_cur" + "\n- virtual_memory_max";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "System resources snapshot";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarSwap extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getSwap());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract information"
					+ "about the system Swap."
					+ "\n\n- Total free system swap"
					+ "\n- Pages in"
					+ "\n- Pages out"
					+ "\n- Total system swap" + "\n- Total used system swap";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "System swap monitoring";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarTcp extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getTcp());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract tcp information."
					+ "\n\n- active_opens"
					+ "\n- attempt_fails"
					+ "\n- curr_estab"
					+ "\n- estab_resets"
					+ "\n- in_errs"
					+ "\n- in_segs"
					+ "\n- out_rsts"
					+ "\n- out_segs"
					+ "\n- passive_opens" + "\n- retrans_segs";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "TCP statistics monitoring";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarUptime extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getUptime());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract "
					+ "the system uptime."
					+ "\n\n- Time since machine started in seconds";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "Uptime";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarWho extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				moduleResults.setResults(proxy.getWhoList());
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract information"
					+ " given by the who command."
					+ "\n\n- user"
					+ "\n- host"
					+ "\n- device" + "\n- time";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "Who";
		}

		@Override
		public void setVersion() {
		}
	}

	public static class SigarFQDN extends AbstractEnvironmentModule {
		@Override
		public ExtractionResult extractInformation() {
			ExtractionResult moduleResults = new ExtractionResult(this);
			try {
				KeyValueResult properties = new KeyValueResult("SigarFQDN");
				properties.results.put("SigarFQDN", proxy.getFQDN());
				moduleResults.setResults(properties);
			} catch (SigarException e) {
				MODULE_LOGGER.log(Level.SEVERE, "Sigar exception", e);
			}
			return moduleResults;
		}

		@Override
		public String getModuleDescription() {
			String description = "This module uses the sigar library to extract the fully qualified domain name.";
			return description;
		}

		@Override
		public void setModuleName() {
			moduleName = "FQDN";
		}

		@Override
		public void setVersion() {
		}
	}
}

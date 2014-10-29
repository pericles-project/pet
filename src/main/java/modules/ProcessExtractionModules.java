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

import static modules.SigarEnvironmentModules.proxy;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.ExtractionResult;
import modules.configuration.ProcessExtractionModulesConfiguration;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hyperic.sigar.ProcCredName;
import org.hyperic.sigar.ProcExe;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ProcessExtractionModules {
	public static class ProcessInformation {
		public String processName;
		@JacksonXmlProperty(isAttribute = true, localName = "ProcessPid", namespace = "")
		public long pid;
		public String user;
		public String processFullPath;
		public String cwd;
		@JacksonXmlElementWrapper(localName = "arguments")
		public List<String> arg;
		@JacksonXmlElementWrapper(localName = "environment")
		public List<String> environmentInformation;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

		public ProcessInformation() {
			arg = new LinkedList<String>();
			environmentInformation = new LinkedList<String>();
		}
	}

	public static class ProcessParameterExtractor extends
			AbstractEnvironmentModule {

		public ProcessParameterExtractor() {
			super();
			setConfig(new ProcessExtractionModulesConfiguration(
					this.moduleName, this.version));
		}

		@Override
		/**
		 * See PTQL language
		 * https://support.hyperic.com/display/SIGAR/PTQL
		 * 
		 */
		public ExtractionResult extractInformation() {
			long[] pids;
			long petPid = proxy.getPid();
			List<ProcessInformation> processInformationList = new LinkedList<ProcessInformation>();
			ExtractionResult pr = new ExtractionResult(this);
			try {
				if (((ProcessExtractionModulesConfiguration) config).ptql != null
						&& ((ProcessExtractionModulesConfiguration) config).ptql
								.trim().length() > 0) {
					String ptql = ((ProcessExtractionModulesConfiguration) config).ptql;
					pids = queryPids(ptql);

				} else {
					pids = proxy.getProcList();
				}
				for (int i = 0; i < pids.length; i++) {
					if (i == petPid) {
						continue;
					}

					long pid = pids[i];

					processInformationList.add(extractProcessInformation(pid));
				}
				pr.setResults(processInformationList);
			} catch (SigarException e) {
				e.printStackTrace();
			}
			return pr;
		}

		/**
		 * See PTQL language https://support.hyperic.com/display/SIGAR/PTQL
		 * 
		 * @param ptql
		 * @return pids
		 * @throws SigarException
		 */
		public long[] queryPids(String ptql) throws SigarException {
			long[] pids;
			pids = ProcessFinder.find(proxy, ptql);
			return pids;
		}

		@SuppressWarnings("rawtypes")
		public static ProcessInformation extractProcessInformation(long pid) {

			ProcessInformation processInformation = new ProcessInformation();
			try {

				processInformation.pid = pid;
				String[] argv = proxy.getProcArgs(pid);
				ProcCredName cred = proxy.getProcCredName(pid);
				ProcState state = proxy.getProcState(pid);
				processInformation.user = cred.getUser() == null ? "" : cred
						.getUser();
				processInformation.arg.addAll(Arrays.asList(argv));
				ProcExe procExe = proxy.getProcExe(pid);
				processInformation.processFullPath = procExe.getName() == null ? ""
						: procExe.getName();
				processInformation.cwd = procExe.getCwd() == null ? ""
						: procExe.getCwd();
				processInformation.processName = state.getName() == null ? ""
						: state.getName();

				Map env = proxy.getProcEnv(pid);
				for (Iterator it = env.entrySet().iterator(); it.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					String information = entry.getKey() + "="
							+ entry.getValue();
					processInformation.environmentInformation.add(information);
				}

			} catch (SigarException x) {

			}
			return processInformation;
		}

		@Override
		public void setModuleName() {
			moduleName = "Process parameter";
		}

		@Override
		public void setVersion() {
			version = "0.6";
		}

		@Override
		public String getModuleDescription() {
			return "This module will extract process information. Options can specify a PTQL query to extract information only from specific processes . For example, set options to 'State.Name.ct=octave' to extract information about the octave process. For details see : https://support.hyperic.com/display/SIGAR/PTQL";
		}
	}

	static String getStartTime(long time) {
		if (time == 0)
			return "00:00";
		long timeNow = System.currentTimeMillis();
		String fmt = "MMMd";

		if ((timeNow - time) < ((60 * 60 * 24) * 1000)) {
			fmt = "HH:mm";
		}
		return new SimpleDateFormat(fmt).format(new Date(time));
	}
}

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
package model;

/**
 * {@link modules.AbstractModule}s use this class to define a list of operating
 * systems where they are compatible with, and to validate if they can be
 * executed on the current system. Allows the modules to define customized
 * commands for different operating systems.
 */
public class OperatingSystem {
	/**
	 * Operating system types for a general distinction. If a more specialized
	 * distinction is needed, the modules can use the variables fullName and
	 * version.
	 */
	public enum OsName {
		UNKNOWN, WINDOWS, LINUX, OS_X, BSD, SOLARIS, SYSTEM_INDEPENDENT
	}

	/** @see OsName */
	public OsName genericName;
	/**
	 * Full name of the operating system. E.g. useful to distinct between
	 * different Linux distributions.
	 */
	public String fullName;
	/** version of the operating system */
	public String version;

	private static OperatingSystem system = null;

	private OperatingSystem() {
	}

	/**
	 * Current used operating system to be used at the modules.
	 * 
	 * @return the current operating system
	 */
	public static final OperatingSystem getCurrentOS() {
		if (system == null) {
			system = new OperatingSystem();
		}
		system.fullName = System.getProperty("os.name");
		system.version = System.getProperty("os.version");
		String osName = system.fullName.toLowerCase();
		OsName os = OsName.UNKNOWN;
		if (osName.indexOf("win") >= 0) {
			os = OsName.WINDOWS;
		} else if (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0
				|| osName.indexOf("aix") >= 0) {
			os = OsName.LINUX;
		} else if (osName.indexOf("mac") >= 0) {
			os = OsName.OS_X;
		} else if (osName.indexOf("bsd") >= 0) {
			os = OsName.BSD;
		} else if (osName.indexOf("sunos") >= 0
				|| osName.indexOf("solaris") >= 0) {
			os = OsName.SOLARIS;
		}
		system.genericName = os;
		return system;
	}
}

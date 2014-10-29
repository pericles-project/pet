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

import java.util.Comparator;

public class ModuleComparator implements Comparator<AbstractModule> {
	@SuppressWarnings("rawtypes")
	@Override
	public int compare(AbstractModule o1, AbstractModule o2) {
		Class c = o1.getClass();
		Class t = c;
		while (!t.equals(AbstractModule.class) || t.equals(Object.class)) {
			c = t;
			t = t.getSuperclass();
		}
		Class c2 = o2.getClass();
		t = c2;
		while (!t.equals(AbstractModule.class) || t.equals(Object.class)) {
			c2 = t;
			t = t.getSuperclass();
		}
		String s1 = c.getSimpleName() + o1.getConfig().moduleDisplayName;
		String s2 = c2.getSimpleName() + o2.getConfig().moduleDisplayName;
//		String s1 = c.getSimpleName() + o1.moduleName;
//		String s2 = c2.getSimpleName() + o2.moduleName;
		// System.out.println(s1 + " " + s2);
		return s1.compareTo(s2);
	}
}
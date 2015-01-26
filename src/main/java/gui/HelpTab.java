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
package gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * GUI tab to display usage information for the user.
 */
public class HelpTab extends JScrollPane {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the panel with the help text.
	 */
	public HelpTab() {
		final JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setEditable(false);
		this.setViewportView(jEditorPane);
		;
		HTMLEditorKit kit = new HTMLEditorKit();
		jEditorPane.setEditorKit(kit);
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet
		.addRule("body {color:#000; font-family:times; margin: 4px; }");
		styleSheet
		.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

		// create some simple html as a string
		String htmlString = "<html>\n" + "<body>\n <h1>PET help</h1>";
		jEditorPane.addHyperlinkListener(new HyperlinkListener() {

			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
					try {
						if (e.getURL().toString().equals("http://petGuide")){
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									new StartupWindow(new Point(100,100),new Dimension(410,450));
								}
							});	

						} else 
							Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		// create a document, set it on the jeditorpane, then add the html
		Document doc = kit.createDefaultDocument();
		jEditorPane.setDocument(doc);
		htmlString += "<B>Please visit the PET website for more help, and the PET website's <a href='https://github.com/pericles-project/pet/wiki/Quick-start-guide'>Quickstart guide</a></B><br>"
				+ "<B>Follow <a href='http://petGuide'>this link</a> to re-open the short guide that was shown at the first start of PET</B><br>"
				+ ""
				+ "<h2>Modules</h2>Modules are used to extract information from the environment. "
				+ "They define a set of formatted information to be extracted and the way of how "
				+ "to extract it.\n"
				+ "Modules always belong to a profile. You can find a list of modules belonging to"
				+ " a specific profile, if you open the profile tab, select a module, and select "
				+ "the modules tab of the selected profile. Here you can browse the module "
				+ "descriptions and configurations, and add additional modules to the profile.\n<br/>"
				+ "There are <b>file dependent modules</b>, that extract information related to a specific"
				+ " file. They will be executed for all files listed in the profile. The second "
				+ "kind of modules are <b>file independent modules</b> that extract environment "
				+ "information which is the same for all files, like system resources.\n"
				+ "A flag displays if the information extracted by a module can change. Only modules"
				+ " with changeable information will be re-extracted in further extractions.\n"
				+ "<b>Daemon modules</b> monitor the system environment for the occurrence of specific events.\n<br/>"
				+ "Some modules can be configured to serve for special needs, see <b>Module Configuration</b>\n"
				+ "It is possible to develop further modules that satisfy special needs. Have a look at the "
				+ "shipped module developer documentation.<hr \\>";
		htmlString += "<h2>Configuration of modules</h2>All modules can be enabled or disabled. Only enabled "
				+ "modules will be used for extraction, while disabled modules are inactive. Disabled "
				+ "modules are shown in red letters at the list.\n"
				+ "Some modules need further configuration. They will disable themselves, if "
				+ "necessary configuration is missing. For other modules further configuration "
				+ "is optional. The module description should explain how to configure a module.<hr \\>";
		htmlString += "<h2>Profiles</h2>Profiles include a set of configured modules and a set of <b>monitored files</b>. They "
				+ "can be used to separate files and modules by purpose. It is possible to "
				+ "disable whole profiles to exclude them from any extraction process. Give your "
				+ "profiles meaningful names to improve the recall value.\n"
				+ "You can start a <b>Single Extraction</b> for a profile exclusively by using the option "
				+ "Extract only profile from the pofile lists pop up menu.\n"
				+ "Note, that it is probably not wise to send a whole profile to another PET user, "
				+ "because some files couldn't exist on the other environment. Have a look at profile templates"
				+ " for this purpose.<hr \\>";
		htmlString += "<h2>Templates</h2>Profile templates serve as template to create new profiles. They "
				+ "predefine a set of configured modules to serve a special need.\n"
				+ "Existing Profiles can be exported as templates from the pop up menu of the list "
				+ "of profiles in the profile tab.\n"
				+ "Templates are stored in the /PET/config/profile_templates/ directory and can "
				+ "be send to other PET users to share profile configurations.<hr \\>";
		htmlString += "<h2>Extraction modes</h2>The Pericles Extraction Tool can extract environment information"
				+ " in two extraction modes:\n"
				+ "In the <b>Continuous Extraction</b>-mode information will be extracted continuously "
				+ "while the tool runs in the background and monitores the environment for the occurence "
				+ "of events. If, for example, a file belonging to a profile was modified, PET will detect"
				+ " the modification and initiate a re-extraction of the modules that extract information "
				+ "that could have changed. The coninuous mode allows to capture the change of environment"
				+ " information over time.\n"
				+ "The <b>Single Extraction</b>-mode will extract all modules once. It allows to take a snapshot"
				+ " of the current environment information, which is useful to compare different states of the"
				+ " environment or before passing digital objects to another environment.<hr \\>";
		htmlString += "<h2>Environment information</h2>"
				+ "Environment information are file-independent information belonging to a profile and extracted by the "
				+ "file-independent modules of that profile." + "<hr \\>";
		htmlString += "<h2>Events</h2>"
				+ "At the events-tab all events recorded by daemon modules are listed."
				+ "<hr \\>";
		htmlString += "</body></html>";

		jEditorPane.setText(htmlString);
	}
}

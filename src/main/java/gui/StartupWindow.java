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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * GUI tab to display usage information for the user.
 */
public class StartupWindow extends JFrame {
	private static final long serialVersionUID = 1L;
public static void main(String[] args) {
	StartupWindow w = new StartupWindow(new Point(100,100),new Dimension(410,450) );

}
	/**
	 * Creates the panel with the help text.
	 */
	public StartupWindow(Point p1, Dimension d1) {
		super();
	//	this.setType(Type.POPUP);
		final JFrame w1 =this;
		JEditorPane jEditorPane = new JEditorPane();
//		jEditorPane.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				w1.setVisible(false);
//				w1.dispose();
//			
//
//			}
//		});
		jEditorPane.setEditable(false);
		JScrollPane p = new JScrollPane();
		p.setViewportView(jEditorPane);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(p,BorderLayout.CENTER);
		JButton b = new JButton("Close");
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				w1.setVisible(false);
				w1.dispose();		
			}
		});
		p2.add(b,BorderLayout.SOUTH);
		
		this.setContentPane(p2);
		HTMLEditorKit kit = new HTMLEditorKit();
		jEditorPane.setEditorKit(kit);
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet
				.addRule("body {color:#000; font-family:times; margin: 4px; }");
		styleSheet
				.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

		// create some simple html as a string
		String htmlString = "<html>\n" + "<body>\n <h1>PET tool quick start guide</h1>";

		// create a document, set it on the jeditorpane, then add the html
		Document doc = kit.createDefaultDocument();
		jEditorPane.setDocument(doc);
		htmlString += ""
				+ "<B>NOTE: The PET tool is created with an empty profile, so in order to make any extraction, you must add some modules by selecting the 'module' tab, and clicking on the 'Add new modules' button at the bottom left.</B>"
				+ "<h2>Quickstart</h2>"
				+ "<ul>"
				+ "<li>Select the 'Module' tab, and click on 'Add new modules' (bottom left)</li>"
				+ "<li>Select the 'Apache Tika extractor' module, and click 'Add selected modules'</li>"
				+ "<li>Using the same procedure, add the 'Installed software snapshot' module</li>"
				+ "<li>Move to the 'Monitored Files' tab, add some files to monitor</li>"
				+ "<li>Click on 'Snapshot' (bottom left of the window)</li>"
				+ "<li>View information extracted from the monitored files by selecting them from the list</li>"
				+ "<li>View information from the system environemnt in the 'Environment information' tab</li>"
				+ "<li>For more help, click on the 'help' tab</li>"
					
				+ "</ul>";
		htmlString += "</body></html>";

		jEditorPane.setText(htmlString);
		this.setLocation(p1);
		this.setSize(d1);
		this.validate();
		this.setVisible(true);
		this.toFront();
		setAlwaysOnTop( true );
	}
}

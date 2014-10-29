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

import static configuration.Log.EXCEPTION_LOGGER;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EventsTab extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTextArea eventFlow = new JTextArea();
	private final JButton timeline = new JButton("Open timeline in browser");

	public EventsTab() {
		super(new BorderLayout(10, 10));
		JScrollPane sp = new JScrollPane(eventFlow);
		add(sp, BorderLayout.CENTER);
		add(timeline, BorderLayout.NORTH);
		timeline.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() == timeline) {
					String timelineUrl = "http://localhost:7774/";
					TimelineHTTPServer.init();
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI(timelineUrl));
						} catch (IOException | URISyntaxException e) {
							EXCEPTION_LOGGER
									.log(Level.SEVERE,
											"Exception while trying to browse event timeline",
											e);
						}
					} else {
						try {
							String[] browsers = { "epiphany", "firefox",
									"mozilla", "konqueror", "netscape",
									"opera", "links", "lynx" };
							StringBuffer commandLineCall = new StringBuffer();
							for (int i = 0; i < browsers.length; i++)
								commandLineCall.append((i == 0 ? "" : " || ")
										+ browsers[i] + " \"" + timelineUrl
										+ "\" ");
							Runtime.getRuntime().exec(
									new String[] { "sh", "-c",
											commandLineCall.toString() });
						} catch (Exception e) {
							EXCEPTION_LOGGER
									.log(Level.SEVERE,
											"Exception while trying to browse event timeline",
											e);
						}
					}
				}

			}
		});
		timeline.setEnabled(true);

	}

	public void appendEvent(String text) {
		eventFlow.append(text);
		eventFlow.setCaretPosition(eventFlow.getDocument().getLength());
	}

}

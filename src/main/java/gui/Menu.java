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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import model.Profile;

/**
 * This class represents the menu of the GUI.
 * 
 * @see GUI
 */
public class Menu extends JMenu implements ActionListener {
	private static final long serialVersionUID = 3762673717195082606L;
	private final GUI gui;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu profileMenu;
	private JMenu preferencesMenu;
	private JMenuItem startMonitoring;
	private JMenuItem stopMonitoring;
	private JMenuItem snapshot;
	private JMenuItem close;
	private JMenuItem exit;
	private JMenuItem newP;
	private JMenuItem newPfromT;
	private JMenuItem extract;
	private JMenuItem clearParts;
	private JMenuItem clearModules;
	private JMenuItem rename;
	private JMenuItem export;
	private JMenuItem delete;
	private JMenuItem clear;
	private JMenu helpMenu;
	private JMenuItem help;
	private JMenuItem helps;
	private JMenuItem helpw;
	private JMenuItem helpww;
	private JMenuItem eventst;
	private JMenuItem eventst2;

	public Menu(GUI guInew) {
		this.gui = guInew;
		createMenu();
	}

	public void createMenu() {
		menuBar = new JMenuBar();
		fileMenu = createSubMenu("Extraction");
		profileMenu = createSubMenu("Profiles");
		preferencesMenu = createSubMenu("Preferences");
		helpMenu = createSubMenu("Help");

		//		startMonitoring = createMenuItem("Start continuous extraction",
		//				fileMenu);
		//		stopMonitoring = createMenuItem("Stop continuous extraction", fileMenu);
		fileMenu.add(gui.startStop2);
		snapshot = createMenuItem("Snapshot extraction", fileMenu);

		eventst = createMenuItem("Show current events", fileMenu);

		eventst2 = createMenuItem("Open event timeline in browser", fileMenu);

		fileMenu.add(new JSeparator());

		exit = createMenuItem("Exit application", fileMenu);

		newP = createMenuItem("New profile", profileMenu);
		newPfromT = createMenuItem("New profile from template", profileMenu);
		profileMenu.add(new JSeparator());
		extract = createMenuItem("Extract only current profile", profileMenu);
		clearParts = createMenuItem("Remove all parts from current profile",
				profileMenu);
		clearModules = createMenuItem(
				"Remove all modules from current profile", profileMenu);
		rename = createMenuItem("Rename current profile", profileMenu);
		export = createMenuItem("Export current profile as template",
				profileMenu);
		delete = createMenuItem("Delete current profile", profileMenu);

		clear = createMenuItem("Clear all extracted information",
				preferencesMenu);
		help = createMenuItem("Help", helpMenu);
		helpww = createMenuItem("Project webpage", helpMenu);
		helps = createMenuItem("First start guide", helpMenu);
		helpw= createMenuItem("Quickstart webpage", helpMenu);

		gui.mainFrame.setJMenuBar(menuBar);
	}

	private JMenu createSubMenu(String name) {
		JMenu menu = new JMenu(name);
		menuBar.add(menu);
		return menu;
	}

	private JMenuItem createMenuItem(String name, JMenu menu) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(this);
		menu.add(item);
		return item;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == close) {
			gui.close();
		} else if (event.getSource() == exit) {
			gui.controller.exit();
		} else if (event.getSource() == clear) {
			deleteMetadataDialog();
		} else if (event.getSource() == newP) {
			gui.profileTab.createProfile();
		} else if (event.getSource() == newPfromT) {
			gui.profileTab.loadTemplate();
		} else if (event.getSource() == extract) {
			final Profile selectedProfile = gui.profileTab.getSelectedProfile();
			HashSet<Profile> profileSet = new HashSet<Profile>();
			profileSet.add(selectedProfile);
			gui.controller.extractor.extract(profileSet, true, false);
		} else if (event.getSource() == clearParts) {
			final Profile selectedProfile = gui.profileTab.getSelectedProfile();
			selectedProfile.removeAllParts();
			gui.profileTab.updateProfile(selectedProfile);
		} else if (event.getSource() == clearModules) {
			final Profile selectedProfile = gui.profileTab.getSelectedProfile();
			selectedProfile.removeAllModules();
			gui.profileTab.updateProfile(selectedProfile);
		} else if (event.getSource() == rename) {
			final Profile selectedProfile = gui.profileTab.getSelectedProfile();
			final JDialog dialog = new JDialog();
			dialog.setTitle("Rename Profile");
			dialog.setSize(new Dimension(400, 200));
			dialog.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 2;
			dialog.add(
					new JLabel("Old profile name: " + selectedProfile.getUUID()),
					constraints);
			constraints.gridy++;
			dialog.add(new JLabel("Enter new Name: "), constraints);
			constraints.gridy++;
			final TextField textField = new TextField(40);
			dialog.add(textField, constraints);
			constraints.gridy++;
			constraints.gridwidth = 1;
			JButton submitButton = new JButton("Change name");
			submitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedProfile.setName(textField.getText());
					dialog.dispose();
				}
			});
			dialog.add(submitButton, constraints);
			constraints.gridx++;
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			dialog.add(cancelButton, constraints);
			dialog.setVisible(true);
		} else if (event.getSource() == export) {
			final Profile selectedProfile = gui.profileTab.getSelectedProfile();
			selectedProfile.exportAsTemplate();
		} else if (event.getSource() == delete) {
			gui.profileTab.removeProfile();
		} else if (event.getSource() == startMonitoring) {
			gui.startExtraction(true);
		} else if (event.getSource() == stopMonitoring) {
			gui.stopExtraction(true);
		} else if (event.getSource() == snapshot) {
			gui.controller.extractor
			.extract(gui.controller.profileController.getProfiles(),
					true, false);
		} else if (event.getSource() == help) {
			new HelpFrame();

		} else if (event.getSource() == helps) {
			new StartupWindow(new Point(100,100),new Dimension(410,450));

		} else if (event.getSource() == helpw) {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/pericles-project/pet/wiki/Quick-start-guide"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (event.getSource() == helpww) {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/pericles-project/pet/"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (event.getSource() == eventst) {
			gui.eventTab = new EventsFrame();


		} else if (event.getSource() == eventst2) {
			EventsFrame.showTimeline();
		}


	}

	private void deleteMetadataDialog() {
		Object[] options = { "Yes, delete extracted metadata", "Cancel" };
		int n = JOptionPane.showOptionDialog(null,
				"Do you really want to delete all extracted metadata?",
				"Clear metadata", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		if (n == 0) {
			gui.controller.deleteAllMetadata();
		}
	}
}

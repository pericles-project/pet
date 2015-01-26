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

import static configuration.Log.FLOW_LOGGER;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Part;
import model.Profile;
import model.ProfileTemplate;
import modules.AbstractModule;

public class ProfileTab extends JPanel {
	private static final long serialVersionUID = 1L;

	final GUI gui;
	private final JPanel profilePanel = new JPanel();
	private final JPanel profileInformationPanel = new JPanel();
	JPanel profileContent = new JPanel(new BorderLayout());
	private final ProfileModuleTab moduleTab;
	private final ProfilePartsTab partTab;
	private final ProfileEnvironmentInformationTab informationTab;
	final JComboBox<Profile> profileList = new JComboBox<Profile>();

	public ProfileTab(GUI gui) {
		this.gui = gui;
		moduleTab = new ProfileModuleTab(this);
		partTab = new ProfilePartsTab(this);
		informationTab = new ProfileEnvironmentInformationTab();
		JTabbedPane contentPanel = new JTabbedPane();
		setLayout(new BorderLayout(10, 10));
		contentPanel.addTab("Monitored files", partTab);
		contentPanel.addTab("Modules", moduleTab);
		contentPanel.addTab("Environment information", informationTab);
//		contentPanel.addTab("Events", gui.eventTab);
//		contentPanel.addTab("Help", gui.helpTab);
		this.add(profilePanel, BorderLayout.NORTH);
		profileContent.add(contentPanel, BorderLayout.CENTER);
		TitledBorder b = BorderFactory.createTitledBorder("Default Profile");
		if (b != null && b.getTitleFont() != null) {
			b.setTitleFont(b.getTitleFont().deriveFont(18.0f));
		}
		profileContent.setBorder(b);
		this.add(profileContent, BorderLayout.CENTER);
		createProfilePanel();
		profileList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == profileList) {
					@SuppressWarnings("unchecked")
					JComboBox<Profile> list = (JComboBox<Profile>) e
							.getSource();
					Profile profile = (Profile) list.getSelectedItem();
					e.getSource();
					if (profile != null) {
						updateProfile(profile);
					}
				}
			}
		});
	}

	private void createProfilePanel() {
		profilePanel.removeAll();
		profilePanel.setLayout(new FlowLayout());
		DefaultComboBoxModel<Profile> model = new DefaultComboBoxModel<Profile>();
		profileList.setModel(model);
		for (Profile profile : gui.controller.profileController.getProfiles()) {
			model.addElement(profile);
		}
		JLabel l = new JLabel("Select profile");
		l.setToolTipText("With extraction profiles a set of configured extraction "
				+ "modules can be defined, to be used on a set of Digital "
				+ "Objects.");
		profilePanel.add(l);
		profilePanel.add(profileList);

		JButton createButton = new JButton("New profile", GUI.PROADD);
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createProfile();
			}
		});
		JButton removeButton = new JButton("Delete profile", GUI.PRODEL);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeProfile();
			}
		});
		JButton templateButton = new JButton(
				"<html>New profile<br>(from template)</html>", GUI.PROADDT);
		templateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadTemplate();
			}
		});
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(createButton);
		buttons.add(templateButton);
		buttons.add(removeButton);
		profilePanel.add(buttons);
		profilePanel.add(profileInformationPanel);
		Profile selectedProfile = gui.controller.profileController
				.getDefaultProfile();
		profileList.setSelectedItem(selectedProfile);
		updateProfile(selectedProfile);
	}

	private void updateProfileInformationPanel() {
		profileInformationPanel.removeAll();
		final Profile selectedProfile = (Profile) profileList.getSelectedItem();
		if (selectedProfile == null)
			return;

		final JCheckBox profileSelectedButton = new JCheckBox("Profile enabled");
		profileSelectedButton
				.setToolTipText("Unselect this button to disable the extraction");
		profileInformationPanel.add(profileSelectedButton);
		profileSelectedButton.setSelected(selectedProfile.isEnabled());
		profileSelectedButton.setText("Profile enabled");
		profileSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedProfile.setEnabled(profileSelectedButton.isSelected());
			}
		});
	}

	protected void createProfile() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Create new profile");
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		dialog.add(new JLabel(
				"<html><h3>Enter name for the new profile:</h3></html>"),
				constraints);
		constraints.gridy++;
		final JTextField textField = new JTextField(40);
		dialog.add(textField, constraints);
		constraints.gridy++;
		constraints.gridwidth = 1;
		JButton addProfileButton = new JButton("Add profile");
		addProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!textField.getText().equals("")) {
					Profile newProfile = gui.controller.profileController
							.createProfile(textField.getText());
					DefaultComboBoxModel<Profile> model = (DefaultComboBoxModel<Profile>) profileList
							.getModel();
					model.addElement(newProfile);
				}
				dialog.dispose();
			}
		});
		dialog.add(addProfileButton, constraints);
		constraints.gridx++;
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		dialog.add(cancelButton, constraints);
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Will remove the profile that is moduleSelected in the profileList.
	 */
	protected void removeProfile() {
		Profile selectedProfile = (Profile) profileList.getModel()
				.getSelectedItem();
		if (selectedProfile != gui.controller.profileController
				.getDefaultProfile()) {
			DefaultComboBoxModel<Profile> model = (DefaultComboBoxModel<Profile>) profileList
					.getModel();
			model.removeElement(selectedProfile);
			gui.controller.profileController.remove(selectedProfile);
			profileList.setSelectedIndex(0);
		} else {
			System.err.println("Default profile cannot be deleted.");
		}
	}

	/**
	 * Update the gui view, if something has changed in the moduleSelected
	 * profile.
	 * 
	 * @param profile
	 */
	public void updateProfile(final Profile profile) {
		if ((Profile) profileList.getModel().getSelectedItem() == profile) {
			FLOW_LOGGER.info("GUI update profile: " + profile.getUUID());
			partTab.updateSelectedPart(profile);
			AbstractModule selectedModule = moduleTab.moduleList
					.getSelectedValue();
			moduleTab.createAllModules(profile);
			if (selectedModule != null) {
				moduleTab.moduleList.setSelectedValue(selectedModule, true);
			}
			informationTab.showInformation(profile);
			updateProfileInformationPanel();
			TitledBorder b = BorderFactory.createTitledBorder(profile
					.toString());
			if (b != null && b.getTitleFont() != null) {
				b.setTitleFont(b.getTitleFont().deriveFont(18.0f));
			}
			profileContent.setBorder(b);
		}
	}

	/**
	 * Will update the whole profile display.
	 */
	public void update() {
		FLOW_LOGGER.info("GUI complete update");
		Profile selectedProfile = (Profile) profileList.getModel()
				.getSelectedItem();
		Part selectedPart = partTab.partList.getSelectedValue();
		AbstractModule selectedModule = moduleTab.moduleList.getSelectedValue();
		createProfilePanel();
		profileList.setSelectedItem(selectedProfile);
		if (selectedPart != null) {
			partTab.partList.setSelectedValue(selectedPart, true);
		}
		if (selectedModule != null) {
			moduleTab.moduleList.setSelectedValue(selectedModule, true);
		}
	}

	protected void loadTemplate() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Load profile template");
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;

		dialog.add(new JLabel("Select templates to be added:"), constraints);
		constraints.gridy++;

		final JList<ProfileTemplate> templateList = new JList<ProfileTemplate>();
		templateList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(templateList);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		HashSet<ProfileTemplate> templates = gui.controller.profileController
				.getTemplates();
		DefaultComboBoxModel<ProfileTemplate> model = new DefaultComboBoxModel<ProfileTemplate>();
		templateList.setModel(model);
		for (ProfileTemplate template : templates) {
			model.addElement(template);
		}
		dialog.add(scrollPane, constraints);

		constraints.gridy++;
		constraints.gridwidth = 1;
		JButton addProfileButton = new JButton("Add profile");
		addProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ProfileTemplate selectedTemplate : templateList
						.getSelectedValuesList()) {
					Profile newProfile = gui.controller.profileController
							.createProfileFromTemplate(selectedTemplate);
					DefaultComboBoxModel<Profile> model = (DefaultComboBoxModel<Profile>) profileList
							.getModel();
					model.addElement(newProfile);
				}
				dialog.dispose();
			}
		});
		dialog.add(addProfileButton, constraints);
		constraints.gridx++;
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		dialog.add(cancelButton, constraints);
		dialog.pack();
		dialog.setVisible(true);
	}

	public Profile getSelectedProfile() {
		return (Profile) profileList.getModel().getSelectedItem();
	}

	class SelectionListener implements ListSelectionListener {
		@SuppressWarnings("unchecked")
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == profileList) {
				JList<Profile> list = (JList<Profile>) e.getSource();
				Profile profile = list.getSelectedValue();
				if (profile != null) {
					updateProfile(profile);
				}
			}
		}
	}
}

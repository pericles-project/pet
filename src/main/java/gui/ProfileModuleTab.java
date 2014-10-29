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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.GenericModule;
import model.Profile;
import modules.AbstractDaemonModule;
import modules.AbstractEnvironmentModule;
import modules.AbstractFileDependentModule;
import modules.AbstractModule;
import modules.ModuleComparator;
import controller.ModuleController;

public class ProfileModuleTab extends JPanel {
	private static final long serialVersionUID = 1L;
	private final ProfileTab profileTab;

	private final JSplitPane contentSplitter;
	private final JPanel configurationArea = new JPanel();
	private final JPanel allModules = new JPanel();
	final JList<AbstractModule> moduleList = new JList<AbstractModule>();
	private final JButton addModuleButton = new JButton("Add new modules",
			ProfilePartsTab.ADD);
	private final JButton removeModuleButton = new JButton(
			"Remove selected module", ProfilePartsTab.DELETE);

	public static final Icon DISK_ICON_IMAGE = new ImageIcon(
			getImage("/images/disk.png"));
	public static final Icon SYSTEM_ICON_IMAGE = new ImageIcon(
			getImage("/images/monitor.png"));
	public static final Icon DAEMON_ICON_IMAGE = new ImageIcon(
			getImage("/images/webcam.png"));

	public static Image getImage(String where) {
		URL u = ProfileModuleTab.class.getResource(where);
		Image image = null;
		if (u == null)
			return null;
		image = Toolkit.getDefaultToolkit().getImage(u);
		return image;
	}

	public ProfileModuleTab(ProfileTab profileTab) {

		this.profileTab = profileTab;
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.5;
		constraints.weighty = 0.5;
		JScrollPane scrollPanel = new JScrollPane(configurationArea);
		contentSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				allModules, scrollPanel);
		contentSplitter.setResizeWeight(.3d);
		contentSplitter.setDividerLocation(.3d);
		add(contentSplitter, constraints);
		EventListener listener = new EventListener();
		addModuleButton.addActionListener(listener);
		removeModuleButton.addActionListener(listener);
	}

	public void createAllModules(Profile profile) {
		allModules.removeAll();
		allModules.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		allModules.add(new JLabel("<html><h2>Extraction Modules</h2></html>"),
				constraints);
		constraints.gridy++;
		moduleList.addListSelectionListener(new SelectionListener());
		moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultListModel<AbstractModule> model = new DefaultListModel<AbstractModule>();
		moduleList.setModel(model);
		AbstractModule[] a = profile.getModules()
				.toArray(new AbstractModule[0]);
		Arrays.sort(a, new ModuleComparator());
		for (AbstractModule module : a) {
			model.addElement(module);
		}
		ListCellRenderer<AbstractModule> r = new ListCellRenderer<AbstractModule>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends AbstractModule> list,
					AbstractModule module, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = new JLabel(module.getConfig().moduleDisplayName);
				if (!module.getConfig().enabled) {
					label.setForeground(Color.red);
				}
				if (module instanceof AbstractDaemonModule) {
					label.setIcon(DAEMON_ICON_IMAGE);
				} else if (module instanceof AbstractFileDependentModule) {
					label.setIcon(DISK_ICON_IMAGE);
				} else if (module instanceof AbstractEnvironmentModule) {
					label.setIcon(SYSTEM_ICON_IMAGE);
				}
				label.setOpaque(true);
				label.setBackground(isSelected ? Color.LIGHT_GRAY : Color.white);
				return label;
			}
		};
		moduleList.setCellRenderer(r);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = 2;
		constraints.weightx = 0.5;
		constraints.weighty = 0.8;
		JScrollPane modulePane = new JScrollPane(moduleList);
		modulePane.setMinimumSize(new Dimension(400, 200));
		allModules.add(modulePane, constraints);
		constraints.fill = GridBagConstraints.NONE;
		constraints.weighty = 0.2;
		constraints.gridwidth = 1;
		constraints.gridy++;
		allModules.add(addModuleButton, constraints);
		constraints.gridx++;
		allModules.add(removeModuleButton, constraints);
		revalidate();
		repaint();
	}

	public void createSelectedModule(final AbstractModule module) {
		configurationArea.removeAll();
		configurationArea.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		configurationArea
				.add(new JLabel("<html><h2>Module: "
						+ module.getConfig().moduleDisplayName + "</h2></html>"),
						constraints);
		constraints.gridx++;
		final JRadioButton enabledButton = new JRadioButton(
				"Enabled for extraction");
		enabledButton.setSelected(module.isSelected());
		enabledButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				module.setSelected(enabledButton.isSelected());
				createSelectedModule(module);
			}
		});
		configurationArea.add(enabledButton, constraints);
		constraints.gridx--;
		constraints.gridy++;
		constraints.gridwidth = 2;
		configurationArea.add(new JLabel("Module description: "), constraints);
		constraints.gridy++;
		JTextArea textArea = new JTextArea(module.getModuleDescription());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		configurationArea.add(textArea, constraints);
		constraints.gridy++;
		configurationArea
				.add(new JLabel(
						"<html><h2>Configuration file editor - use JSON format!</h2></html>"),
						constraints);
		constraints.gridy++;
		final ModuleConfigurationEditor editor = new ModuleConfigurationEditor(
				module);
		configurationArea.add(editor, constraints);
		constraints.gridy++;
		constraints.gridwidth = 1;
		JButton reloadButton = new JButton("Reload config file (changes lost)");
		configurationArea.add(reloadButton, constraints);
		constraints.gridx++;
		JButton saveConfigButton = new JButton("Save and use config file");
		configurationArea.add(saveConfigButton, constraints);
		constraints.gridx--;
		constraints.gridy++;
		constraints.gridwidth = 2;
		final JLabel validLabel = new JLabel(" ");
		configurationArea.add(validLabel, constraints);
		constraints.gridy++;
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.reloadConfigFile();
				validLabel.setText("");
			}
		});
		saveConfigButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (editor.saveAndUseConfigFile()) {
					validLabel
							.setText("<html><h3>Config file valid - config file saved</h3></html>");
					updateModuleDisplayName();
				} else {
					validLabel
							.setText("<html><h3><font color='red'>Config file invalid - not saved</font></h3></html>");
				}
			}
		});
		configurationArea.repaint();
		configurationArea.revalidate();
	}

	private void addModule() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Add to profile");
		dialog.setLayout(new GridBagLayout());
		dialog.setPreferredSize(new Dimension(800, 600));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0.4;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = 2;
		dialog.add(new JLabel("<html><h3>Add modules to profile:</h3></html>"),
				constraints);
		constraints.weighty = 0.9;
		constraints.gridy++;
		final DefaultListModel<GenericModule> model = new DefaultListModel<GenericModule>();
		final JList<GenericModule> moduleList = new JList<GenericModule>(model);
		moduleList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		GenericModule[] c = ModuleController.getGenericModuleSet().toArray(
				new GenericModule[0]);
		Arrays.sort(c, new Comparator<GenericModule>() {
			@Override
			public int compare(GenericModule o1, GenericModule o2) {
				return o1.moduleName.compareTo(o2.moduleName);
			}
		});
		for (GenericModule genericModule : c) {
			model.addElement(genericModule);
		}
		JScrollPane modulePanel = new JScrollPane(moduleList);
		dialog.add(modulePanel, constraints);
		constraints.gridwidth = 1;
		constraints.gridy++;
		JButton addButton = new JButton("Add selected modules");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Profile profile = profileTab.getSelectedProfile();
				for (GenericModule genericModule : moduleList
						.getSelectedValuesList()) {
					profile.addModule(genericModule.getInstance());
				}
				profileTab.updateProfile(profile);
				dialog.setVisible(false);
			}
		});
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weighty = 0.1;
		dialog.add(addButton, constraints);
		constraints.gridx++;
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		dialog.add(cancelButton, constraints);
		constraints.gridx = 3;
		constraints.gridy = 0;
		constraints.gridheight = 3;
		constraints.weightx = 0.6;
		constraints.weighty = 0.5;
		constraints.fill = GridBagConstraints.BOTH;
		final JTextArea moduleDescription = new JTextArea();
		moduleDescription.setText("Module description");
		moduleDescription.setLineWrap(true);
		moduleDescription.setWrapStyleWord(true);
		moduleDescription.setBorder(BorderFactory.createLineBorder(Color.WHITE,
				5));
		moduleDescription.setEditable(false);
		JScrollPane descriptionScrollPane = new JScrollPane(moduleDescription);
		dialog.add(descriptionScrollPane, constraints);
		moduleList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				GenericModule selectedModule = moduleList.getSelectedValue();
				moduleDescription.setText(selectedModule.moduleDescription);
			}
		});
		dialog.pack();
		dialog.setVisible(true);
	}

	public void removeModule() {
		AbstractModule selectedModule = moduleList.getSelectedValue();
		if (selectedModule == null)
			return;
		Profile selectedProfile = profileTab.getSelectedProfile();
		selectedProfile.removeModule(selectedModule);
		profileTab.updateProfile(selectedProfile);
	}

	/**
	 * Call this method, if a moduleDisplayName has changed
	 */
	public void updateModuleDisplayName() {
		AbstractModule selectedModule = moduleList.getSelectedValue();
		createAllModules(profileTab.getSelectedProfile());
		moduleList.setSelectedValue(selectedModule, false);
	}

	class SelectionListener implements ListSelectionListener {
		@SuppressWarnings("unchecked")
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == moduleList) {
				JList<AbstractModule> list = (JList<AbstractModule>) e
						.getSource();
				AbstractModule module = list.getSelectedValue();
				if (module != null) {
					createSelectedModule(module);
				} else {
					configurationArea.removeAll();
				}
				repaint();
				revalidate();
			}
		}
	}

	class EventListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addModuleButton) {
				addModule();
			} else if (e.getSource() == removeModuleButton) {
				removeModule();
			}
		}
	}
}

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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.ExtractionResultCollection;
import model.Part;
import model.Profile;

public class ProfilePartsTab extends JPanel {
	private static final long serialVersionUID = 1L;
	private final ProfileTab profileTab;

	private final JSplitPane contentSplitter;
	private final JPanel selectedPart = new JPanel();
	private final JPanel allParts = new JPanel();
	final JList<Part> partList = new JList<Part>();
	private final JButton addPartsButton = new JButton("Add Files", ADD);
	private final JButton removePartsButton = new JButton("Remove Files", DELETE);
	public static final Icon ADD = new ImageIcon(
			getImage("/images/add.png"));
	public static final Icon DELETE = new ImageIcon(
			getImage("/images/delete.png"));


	public static Image getImage(String where) {
		URL u = ProfileModuleTab.class.getResource(where);
		Image image = null;
		if (u == null)
			return null;
		image = Toolkit.getDefaultToolkit().getImage(u);
		return image;
	}

	public ProfilePartsTab(ProfileTab profileTab) {
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
		contentSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, allParts,
				selectedPart);
		contentSplitter.setResizeWeight(.3d);
		contentSplitter.setDividerLocation(.3d);
		add(contentSplitter, constraints);
		EventListener listener = new EventListener();
		addPartsButton.addActionListener(listener);
		removePartsButton.addActionListener(listener);
		selectedPart
				.add(new JLabel(
						"<html><h4>Extraction Result (select a file first)</h4></html>"),
						constraints);
	}

	public void createAllParts(Profile profile) {
		new FileDrop(partList, new FileDrop.Listener() {
			@Override
			public void filesDropped(java.io.File[] files) {
				addFilesToProfile(files);
			}
		});
		allParts.removeAll();
		allParts.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		allParts.add(new JLabel(
				"<html><h2>Monitored files</h2></html>"),
				constraints);
		constraints.gridy++;
		partList.addListSelectionListener(new SelectionListener());
		partList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultListModel<Part> model = new DefaultListModel<Part>();
		partList.setModel(model);
		for (Part part : profile.getParts()) {
			model.addElement(part);
		}
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = 2;
		constraints.weightx = 0.5;
		constraints.weighty = 0.8;
		JScrollPane partPane = new JScrollPane(partList);
		partPane.setPreferredSize(new Dimension(400, 200));
		allParts.add(partPane, constraints);
		constraints.fill = GridBagConstraints.NONE;
		constraints.weighty = 0.2;
		constraints.gridwidth = 1;
		constraints.gridy++;
		allParts.add(addPartsButton, constraints);
		constraints.gridx++;
		allParts.add(removePartsButton, constraints);
		revalidate();
		repaint();
	}

	private void createTree(ExtractionResultCollection collection) {
		selectedPart.removeAll();
		selectedPart.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;

		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.5;
		constraints.weighty = 0.5;

		selectedPart.add(new InformationView(collection), constraints);
		repaint();
		revalidate();
	}

	public void updateSelectedPart(Profile profile) {
		Part selectedPart = partList.getSelectedValue();
		createAllParts(profile);
		if (selectedPart != null) {
			partList.setSelectedValue(selectedPart, true);
			createTree(selectedPart);
		}
	}

	public void removeParts() {
		List<Part> selectedParts = partList.getSelectedValuesList();
		DefaultListModel<Part> model = ((DefaultListModel<Part>) partList
				.getModel());
		for (Part part : selectedParts) {
			model.removeElement(part);
			profileTab.getSelectedProfile().removePart(part);
		}
	}

	private void addFiles() {
		final JFileChooser fileChooser = new JFileChooser(
				"Choose files to be added");
		fileChooser.setDialogType(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setDialogTitle("Choose files to be added");
		final int result = fileChooser.showDialog(null, "add");
		if (result == JFileChooser.APPROVE_OPTION) {
			File[] files = fileChooser.getSelectedFiles();
			addFilesToProfile(files);
		}
	}

	private void addFilesToProfile(File[] files) {
		if (files == null)
			return;
		HashSet<Path> paths = new HashSet<Path>();
		for (File file : files) {
			paths.add(file.toPath());
		}
		Profile selectedProfile = profileTab.getSelectedProfile();
		selectedProfile.addAllPartsFromPaths(paths, true);
		//profileTab.updateProfile(selectedProfile);
	}

	class SelectionListener implements ListSelectionListener {
		@SuppressWarnings("unchecked")
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == partList) {
				JList<Part> list = (JList<Part>) e.getSource();
				Part part = list.getSelectedValue();
				if (part != null) {
					createTree(part);
				}
			}
		}
	}

	class EventListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addPartsButton) {
				addFiles();
			} else if (e.getSource() == removePartsButton) {
				removeParts();
			}
		}
	}
}

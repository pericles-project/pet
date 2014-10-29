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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.MutableComboBoxModel;

import model.Environment;
import model.ExtractionResultCollection;
import model.Part;
import controller.StorageController;

/**
 * Container for the displays of the extracted information belonging to an
 * ExtractionResultCollection.
 * 
 */
public class InformationView extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JButton changeButton = new JButton(
			"Visualise information change");
	private final JButton treeButton = new JButton("Show information tree");
	private final JButton jsonButton = new JButton("Show information in JSON");
	private JComboBox<String> moduleBox;
	private final ExtractionResultCollection collection;

	/**
	 * Constructor for the display class of extracted information.
	 * 
	 * @param collection
	 *            The ExtractionResultCollection which information should be
	 *            shown.
	 */
	protected InformationView(ExtractionResultCollection collection) {
		this.collection = collection;
		if (collection == null) {
			System.err.println("collection null at init of InformationView");
		}
		setLayout(new GridBagLayout());
		EventListener listener = new EventListener();
		changeButton.addActionListener(listener);
		treeButton.addActionListener(listener);
		jsonButton.addActionListener(listener);
		showInformationTree();
	}

	/**
	 * Displays the tree with extracted information belonging to a file, or to
	 * the environment of a profile.
	 */
	protected void showInformationTree() {
		removeAll();
		GridBagConstraints constraints = initConstraints();
		add(new JScrollPane(new InformationTree(collection)), constraints);
		addFooter(constraints);
	}

	/**
	 * Displays information extracted by a specific module, belonging to a file,
	 * or the environment of a profile.
	 */
	protected void showInformationChangeTable() {
		removeAll();
		GridBagConstraints constraints = initConstraints();
		add(new JScrollPane(new InformationChangeTable(collection,
				getSelectedModule())), constraints);
		addFooter(constraints);
	}

	protected void showInformationJSONEditor() {
		removeAll();
		GridBagConstraints constraints = initConstraints();
		add(new JScrollPane(new InformationJSONView(collection,
				getSelectedModule())), constraints);
		addFooter(constraints);
	}

	private String getSelectedModule() {
		return (String) moduleBox.getSelectedItem();
	}

	private void addFooter(GridBagConstraints constraints) {
		constraints.gridy++;
		constraints.weightx = 0.1;
		constraints.weighty = 0.1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 1;
		add(new JLabel("Select a module: "), constraints);
		constraints.gridx++;
		moduleBox = initModuleBox();
		add(moduleBox, constraints);
		constraints.gridx--;
		constraints.gridy++;
		add(treeButton, constraints);
		constraints.gridx++;
		add(changeButton, constraints);
		constraints.gridx++;
		add(jsonButton, constraints);
		this.revalidate();
		this.repaint();
	}

	private JComboBox<String> initModuleBox() {
		JComboBox<String> box = new JComboBox<String>();
		((MutableComboBoxModel<String>) box.getModel()).addElement("all");
		HashSet<String> moduleNames = getUsedModulesFromDatabase();
		for (String moduleName : moduleNames) {
			((MutableComboBoxModel<String>) box.getModel())
					.addElement(moduleName);
		}
		box.setSelectedItem("all");
		return box;
	}

	private HashSet<String> getUsedModulesFromDatabase() {
		HashSet<String> moduleNames = new HashSet<String>();
		if (collection instanceof Part) {
			moduleNames = StorageController.storage
					.getAllUsedModules(collection);
		} else if (collection instanceof Environment) {
			moduleNames = StorageController.storage
					.getAllUsedModules(collection);
		}
		return moduleNames;
	}

	private GridBagConstraints initConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.weightx = 0.9;
		constraints.weighty = 0.9;
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 0;
		return constraints;
	}

	class EventListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == treeButton) {
				showInformationTree();
			} else if (e.getSource() == changeButton) {
				showInformationChangeTable();
			} else if (e.getSource() == jsonButton) {
				showInformationJSONEditor();
			}
		}
	}
}

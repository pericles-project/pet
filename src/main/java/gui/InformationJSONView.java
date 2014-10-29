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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.ExtractionResultCollection;
import modules.AbstractModule;
import controller.StorageController;

/**
 * View to display all extraction results of one {@link AbstractModule}
 * belonging to an {@link ExtractionResultCollection} in JSON format.
 */
public class InformationJSONView extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor to create the JSON view, will be called by the
	 * {@link InformationView}.
	 * 
	 * @param collection
	 *            collection where the extracted information belongs to
	 * @param moduleName
	 *            name of the module that extracted the information
	 */
	public InformationJSONView(final ExtractionResultCollection collection,
			final String moduleName) {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(getJSONViewPanel(collection, moduleName, false), constraints);
	}

	private JPanel getJSONViewPanel(
			final ExtractionResultCollection collection,
			final String moduleName, boolean fullScreen) {
		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 0;
		if (moduleName.equals("all")) {
			viewPanel.add(new JLabel("Please, select a module first!"),
					constraints);
		} else {
			constraints.gridwidth = 1;
			viewPanel.add(
					new JLabel("<html><h1>" + moduleName + "</h1></html>"),
					constraints);
			if (!fullScreen) {
				addFullscreenButton(collection, moduleName, viewPanel,
						constraints);
			}
			constraints.gridy++;
			constraints.gridwidth = 2;
			String resultString = StorageController.storage.getStringResults(
					collection, moduleName);
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 0.5;
			constraints.weighty = 0.5;
			JsonEditor JSONEditor = new JsonEditor(true);
			JSONEditor.textArea.setText(resultString);
			JSONEditor.textArea.setEditable(false);
			JSONEditor.textArea.setHighlightCurrentLine(false);
			JSONEditor.textArea.setLineWrap(true);
			viewPanel.add(JSONEditor, constraints);
		}
		return viewPanel;
	}

	private void addFullscreenButton(
			final ExtractionResultCollection collection,
			final String moduleName, JPanel viewPanel,
			GridBagConstraints constraints) {
		JButton fullscreenButton = new JButton("Open fullscreen in new window");
		fullscreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showFullscreenView(collection, moduleName);
			}
		});
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridx++;
		viewPanel.add(fullscreenButton, constraints);
		constraints.gridx--;
	}

	private void showFullscreenView(ExtractionResultCollection collection,
			String moduleName) {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Information results JSON view");
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.9;
		constraints.weighty = 0.9;
		JScrollPane scrollPane = new JScrollPane(getJSONViewPanel(collection,
				moduleName, true));
		dialog.add(scrollPane, constraints);
		dialog.pack();
		dialog.setSize(getToolkit().getScreenSize());
		dialog.setPreferredSize(getToolkit().getScreenSize());
		dialog.setVisible(true);
	}
}

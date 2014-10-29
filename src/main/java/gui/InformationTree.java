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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import model.Environment;
import model.ExtractionResultCollection;
import model.Part;
import controller.StorageController;

/**
 * GUI tree to display the extraction results belonging to an
 * {@link ExtractionResultCollection}/
 */
public class InformationTree extends JPanel {
	private static final long serialVersionUID = 1L;
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode(
			"<html><h2>Extracted Information</html></h2>");;
	private final JTree tree = new JTree();

	public InformationTree(final ExtractionResultCollection collection) {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		if (collection instanceof Part) {
			StorageController.storage.createResultTree(collection, root);
			add(new JLabel("<html><h3>Environment information for file: "
					+ ((Part) collection).getPath() + "</h3></html>"),
					constraints);
		} else if (collection instanceof Environment) {
			StorageController.storage.createResultTree(collection, root);
			add(new JLabel(
					"<html><h3>File independent environment information</h3></html>"),
					constraints);
		}
		tree.setModel(new DefaultTreeModel(root));
		tree.getCellRenderer();
		tree.expandRow(0);
		constraints.gridwidth = 1;
		constraints.gridy++;
		JButton expandButton = new JButton("Expand Tree");
		expandButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
		});
		add(expandButton, constraints);
		constraints.gridx++;
		JButton collapseButton = new JButton("Collapse Tree");
		collapseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = tree.getRowCount() - 1; i > -1; i--) {
					tree.collapseRow(i);
				}
				tree.expandRow(0);
			}
		});
		add(collapseButton, constraints);
		constraints.gridx--;
		constraints.gridy++;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.5;
		constraints.weighty = 0.5;
		constraints.gridwidth = 2;
		add(tree, constraints);

		MouseListener clickListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						treeDoubleClick(selRow, selPath);
					}
				}
			}

			private void treeDoubleClick(int selRow, TreePath selPath) {
				if (selPath.getPathCount() < 4) {
					return;
				}
				((DefaultMutableTreeNode) selPath.getParentPath()
						.getLastPathComponent()).getChildAt(2);
				String moduleName = ((DefaultMutableTreeNode) selPath
						.getParentPath().getLastPathComponent()).getChildAt(1)
						.toString().substring("Moudle type: ".length());
				String extractionDate = ((DefaultMutableTreeNode) selPath
						.getParentPath().getLastPathComponent()).getChildAt(0)
						.toString().substring("Extraction date: ".length());
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
				dialog.add(
						getJSONViewPanel(collection, moduleName, extractionDate),
						constraints);
				dialog.pack();
				dialog.setSize(getToolkit().getScreenSize());
				dialog.setPreferredSize(getToolkit().getScreenSize());
				dialog.setVisible(true);
			}
		};
		tree.addMouseListener(clickListener);
	}

	private JPanel getJSONViewPanel(
			final ExtractionResultCollection collection,
			final String moduleName, final String extractionDate) {
		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		viewPanel.add(new JLabel("<html><h1>" + moduleName + "</h1></html>"),
				constraints);
		constraints.gridy++;
		constraints.gridwidth = 2;
		String resultString = StorageController.storage.getStringResults(
				collection, moduleName, extractionDate);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.5;
		constraints.weighty = 0.5;
		JsonEditor JSONEditor = new JsonEditor();
		JSONEditor.textArea.setText(resultString);
		JSONEditor.textArea.setEditable(false);
		JSONEditor.textArea.setHighlightCurrentLine(false);
		JSONEditor.textArea.setLineWrap(true);
		viewPanel.add(JSONEditor, constraints);
		return viewPanel;
	}
}

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

import static configuration.Constants.EXPORTED_TABLES_DIRECTORY;
import static configuration.Log.EXCEPTION_LOGGER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import model.ExtractionResultCollection;
import model.Part;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;
import controller.StorageController;

/**
 * Visualizes the extraction results of one chosen extraction module belonging
 * to a {@link model.Part} or an {@link model.Environment}. Each column
 * represents one extraction, which makes it possible to compare the different
 * result sets.
 */
public class InformationChangeTable extends JPanel {
	private static final long serialVersionUID = 1L;

	private TableModel currentModel = null;

	/**
	 * The first column shows the timestamp of extraction. Each row shows a
	 * result, whereby the first row (= the title description row) shows the
	 * keys of the result and the other rows display the results value at the
	 * specific timestamp.
	 * 
	 * @param collection
	 *            Part or Environment where the extracted information belongs to
	 * @param moduleName
	 *            The extraction module which results should be compared
	 */
	public InformationChangeTable(final ExtractionResultCollection collection,
			final String moduleName) {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 0;
		if (moduleName.equals("all")) {
			showErrorMessage(collection);
		} else {
			constraints.gridwidth = 1;
			add(new JLabel("<html><h1>" + moduleName + "</h1></html>"),
					constraints);
			constraints.anchor = GridBagConstraints.EAST;
			constraints.gridx++;
			JButton exportButton = new JButton("Export table to xls");
			exportButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (currentModel != null) {
						String outputFileName = EXPORTED_TABLES_DIRECTORY
								+ collection.profileUUID + "_" + moduleName;
						if (collection instanceof Part) {
							outputFileName += "_"
									+ ((Part) collection).fileName.replace(".",
											"-");
						}
						outputFileName += ".xls";
						currentModel.export2XLS(new File(outputFileName));
					}
				}
			});
			add(exportButton, constraints);
			constraints.gridx++;
			JButton fullscreenButton = new JButton(
					"Show table fullscreen in new Window");
			fullscreenButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showFullscreenTable(collection, moduleName);
				}
			});
			add(fullscreenButton, constraints);
			constraints.weightx = 0.1;
			constraints.weighty = 0.1;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridwidth = 3;
			constraints.gridx -= 2;
			constraints.gridy++;
			final JScrollPane tablePane = getTablePane(collection, moduleName,
					constraints);
			add(tablePane, constraints);
		}
	}

	/**
	 * Opens the current table in a new full screen Window.
	 * 
	 * @param collection
	 *            Part or Environment where the extracted information belongs to
	 * @param moduleName
	 *            The extraction module which results should be compared
	 */
	private void showFullscreenTable(
			final ExtractionResultCollection collection, final String moduleName) {
		final TableModel tableModel = currentModel;
		final JDialog dialog = new JDialog();
		dialog.setTitle("Information change table");
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0.9;
		constraints.weighty = 0.05;
		dialog.add(new JLabel("Profile: " + collection.profileUUID),
				constraints);
		constraints.gridy++;
		if (collection instanceof Part) {
			dialog.add(new JLabel("File: " + ((Part) collection).fileName),
					constraints);
			constraints.gridy++;
		} else {
			dialog.add(new JLabel("Environment information"), constraints);
			constraints.gridy++;
		}
		dialog.add(new JLabel("Module: " + moduleName), constraints);
		constraints.gridy++;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 0.75;
		GridBagConstraints constraints2 = constraints;
		constraints2.gridy++;
		dialog.add(getTablePane(collection, moduleName, constraints2),
				constraints);
		constraints.fill = GridBagConstraints.NONE;
		constraints.weighty = 0.1;
		constraints.gridy++;
		constraints.gridwidth = 1;
		JButton closeButton = new JButton("Close Window");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		dialog.add(closeButton, constraints);
		constraints.gridx++;
		JButton exportButton = new JButton("Export table to xls");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tableModel != null) {
					String outputFileName = EXPORTED_TABLES_DIRECTORY
							+ collection.profileUUID + "_" + moduleName;
					if (collection instanceof Part) {
						outputFileName += "_"
								+ ((Part) collection).fileName
										.replace(".", "-");
					}
					outputFileName += ".xls";
					tableModel.export2XLS(new File(outputFileName));
				}
			}
		});
		dialog.add(exportButton, constraints);
		constraints.gridx--;
		constraints.gridy++;
		dialog.pack();
		dialog.setSize(getToolkit().getScreenSize());
		dialog.setPreferredSize(getToolkit().getScreenSize());
		dialog.setVisible(true);
	}

	/**
	 * Returns the scrollable table
	 * 
	 * @param collection
	 *            Part or Environment where the extracted information belongs to
	 * @param moduleName
	 *            The extraction module which results should be compared
	 * @param constraints
	 *            Layout configurations
	 * @return scrollable table
	 */
	private JScrollPane getTablePane(ExtractionResultCollection collection,
			String moduleName, GridBagConstraints constraints) {
		ArrayList<ArrayList<String>> data = null;
		String[] header = null;
		header = StorageController.storage.getResultsHeader(collection,
				moduleName);
		data = StorageController.storage.getResults(collection, moduleName);
		if (data != null && header != null) {
			TableModel model = new TableModel(data, header);
			currentModel = model;
			JTable table = new JTable(model);
			for (int row = 0; row < model.getRowCount(); row++) {
				ArrayList<String> column = data.get(row);
				for (int columnEntry = 0; columnEntry < model.getColumnCount(); columnEntry++) {
					table.getModel().setValueAt(column.get(columnEntry), row,
							columnEntry);
				}
			}
			table.setFillsViewportHeight(true);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			return new JScrollPane(table);
		}
		return null;
	}

	/**
	 * Error message will be shown, if no module was selected to display its
	 * results.
	 * 
	 * @param collection
	 *            Part or Environment where the extracted information belongs to
	 */
	private void showErrorMessage(ExtractionResultCollection collection) {
		JTextArea textArea = new JTextArea(
				"Here are the differences between the extraction results shown.\n"
						+ "Directly successive extraction results will be compared with each other.\n"
						+ "If new information comes in addition at an extraction run, it is displayed green.\n"
						+ "Information that is not present anymore at a newer extraction result, but was present at the previous extraction result, "
						+ "is displayed in red.");
		textArea.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setMinimumSize(new Dimension(200, 100));
		textArea.setEditable(false);
		String[] results = StorageController.storage.getRawResults(collection);
		diff_match_patch d = new diff_match_patch();
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<h2> Diff between first and second extractions </h2>");
		if (results.length < 2) {
			sb.append("<pre><code>");
			sb.append(
					results[0].replace("&", "&amp;").replace("<", "&lt;")
							.replace(">", "&gt;").replace("\n", "<br>"))
					.append('\n');
			sb.append("</code></pre>");
		} else {
			LinkedList<Diff> l2 = d.diff_main(results[0], results[0 + 1]);
			d.diff_cleanupEfficiency(l2);
			sb.append(d.diff_prettyHtml(l2));
			// System.out.println(d.diff_prettyHtml(l2));
			for (int c = 0; c < results.length - 1; c++) {
				sb.append("<h2> Other differences between " + (c + 1) + " and "
						+ (c + 2) + "</h2>");
				LinkedList<Diff> l = d.diff_main(results[c], results[c + 1]);
				d.diff_cleanupEfficiency(l);
				sb.append("<pre><code>");
				for (Diff f : l)
					if (f.operation != Operation.EQUAL) {
						sb.append(
								f.operation
										+ ": "
										+ f.text.replace("&", "&amp;")
												.replace("<", "&lt;")
												.replace(">", "&gt;")
												.replace("\n", "<br>")).append(
								'\n');
					}
				sb.append("</code></pre>");
			}
		}
		sb.append("</html>");
		setLayout(new BorderLayout());
		JEditorPane jep = new JEditorPane();
		jep.setEditable(false);
		jep.setContentType("text/html");

		jep.setText(sb.toString());
		JScrollPane scrollPane = new JScrollPane(jep);

		setLayout(new BorderLayout());
		add(textArea, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * TableModel for the entry management of the information change table.
	 */
	class TableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private final ArrayList<ArrayList<String>> data;
		private final int columnNumber;
		private final int rowNumber;
		private final String[] header;

		public TableModel(ArrayList<ArrayList<String>> data, String[] header) {
			this.data = data;
			this.rowNumber = data.size();
			this.columnNumber = data.get(0).size();
			this.header = header;
		}

		@Override
		public int getColumnCount() {
			return columnNumber;
		}

		@Override
		public int getRowCount() {
			return rowNumber;
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data.get(row).get(col);
		}

		@Override
		public void setValueAt(Object entry, int row, int col) {
			data.get(row).set(col, (String) entry);
		}

		@Override
		public String getColumnName(int col) {
			return header[col];
		}

		public void export2XLS(File outputFile) {
			try {
				WritableWorkbook workBook = Workbook.createWorkbook(outputFile);
				WritableSheet sheet = workBook.createSheet(outputFile.getName()
						.toString(), 0);
				for (int col = 0; col < getColumnCount(); col++) {
					Label colLabel = new Label(col, 0, getColumnName(col));
					sheet.addCell(colLabel);
				}
				for (int row = 0; row < getRowCount(); row++) {
					for (int col = 0; col < getColumnCount(); col++) {
						Label rowLabel = new Label(col, row + 1, getValueAt(
								row, col).toString());
						sheet.addCell(rowLabel);
					}
				}
				workBook.write();
				workBook.close();
			} catch (IOException | WriteException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception while exporting table", e);
			}
		}
	};
}

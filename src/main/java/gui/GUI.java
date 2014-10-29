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
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import model.Profile;

import com.bric.plaf.AquaSpinningProgressBarUI;

import controller.ExtractionController;

/**
 * Graphical User Interface - The application can run with, or without an
 * enabled GUI.
 */
public class GUI {
	/** The applications {@link ExtractionController} */
	protected final ExtractionController controller;
	/** GUI tab with the list of {@link Profile}s */
	public ProfileTab profileTab;
	/** GUI tab that shows usage help */
	public HelpTab helpTab;
	/** GUI tab to display occurring events */
	public EventsTab eventTab = new EventsTab();
	protected final JFrame mainFrame = new JFrame("Pericles Extraction Tool");
	private final JPanel workPanel = new JPanel(new BorderLayout());
	private final EventListener listener = new EventListener();
	private final JButton startStop = new JButton("Start extraction");
	private final JButton once = new JButton("Snapshot", SNAPSHOT);
	private final JLabel status = new JLabel("Status");
	private final JLabel status2 = new JLabel("");
	private final JProgressBar bar1 = new JProgressBar();

	public static final Icon SNAPSHOT = new ImageIcon(
			getImage("/images/camera_add.png"));
	public static final Icon STOP = new ImageIcon(
			getImage("/images/control_stop_blue.png"));
	public static final Icon PLAY = new ImageIcon(
			getImage("/images/control_play_blue.png"));
	public static final Icon PROADD = new ImageIcon(
			getImage("/images/application_add.png"));
	public static final Icon PROADDT = new ImageIcon(
			getImage("/images/application_form_add.png"));
	public static final Icon PRODEL = new ImageIcon(
			getImage("/images/application_delete.png"));

	/**
	 * GUI constructor: Initializes all tabs, menu and other GUI elements.
	 * 
	 * @param controller
	 *            The applications ExtractionController
	 */
	public GUI(ExtractionController controller) {
		this.controller = controller;
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainFrame.setContentPane(contentPanel);
		new Menu(this);
		createWorkPanel();
		helpTab = new HelpTab();
		profileTab = new ProfileTab(this);
		mainFrame.setLayout(new BorderLayout(5, 5));
		mainFrame.add(workPanel, BorderLayout.PAGE_END);
		mainFrame.add(profileTab, BorderLayout.CENTER);
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.setVisible(true);
	}

	/**
	 * Utility method to load a GUI image.
	 * 
	 * @param where
	 *            path of the image
	 * @return Image object
	 */
	private static Image getImage(String where) {
		URL u = ProfileModuleTab.class.getResource(where);
		Image image = null;
		if (u == null)
			return null;
		image = Toolkit.getDefaultToolkit().getImage(u);
		return image;
	}

	/**
	 * Creates the GUIs working panel.
	 */
	private void createWorkPanel() {
		startStop.addActionListener(listener);
		once.addActionListener(listener);
		once.setEnabled(true);
		bar1.setUI(new AquaSpinningProgressBarUI());
		bar1.putClientProperty("useStrokeControl", new Boolean(true));
		bar1.setVisible(false);
		workPanel.add(status2, BorderLayout.CENTER);
		JPanel flow = new JPanel();
		flow.add(startStop);
		flow.add(once);
		once.setEnabled(true);
		flow.add(bar1);
		workPanel.add(flow, BorderLayout.WEST);
		if (controller.extractor.getUpdateExtraction()) {
			startExtraction(false);
		} else {
			stopExtraction(false);
		}
	}

	/**
	 * Updates the GUI elements, if continuous extraction is running.
	 * 
	 * @param daemons
	 *            Should the daemons be started?
	 */
	protected void startExtraction(boolean daemons) {
		status.setText("[ Continuous extraction: true ]");
		startStop.setText("Stop monitor");
		startStop.setIcon(STOP);
		startStop.setSelected(true);
		controller.extractor.setUpdateExtraction(true);
		if (daemons) {
			controller.extractor.startDaemons();
		}
	}

	/**
	 * Updates the GUI elements, if continuous extraction is NOT running.
	 * 
	 * @param daemons
	 *            Should the daemons be started?
	 */
	protected void stopExtraction(boolean daemons) {
		status.setText("[ Continuous extraction: false ]");
		startStop.setText("Start monitor");
		startStop.setIcon(PLAY);
		startStop.setSelected(false);
		controller.extractor.setUpdateExtraction(false);
		if (daemons) {
			controller.extractor.stopDaemons();
		}
	}

	/**
	 * Closes the GUI and informs the {@link ExtractionController}.
	 */
	public void close() {
		mainFrame.setVisible(false);
		mainFrame.dispose();
		controller.stopGui();
	}

	/**
	 * Informs the profile tab that a specific {@link Profile} was altered, and
	 * updates the tab, it the updated Profile is the current shown Profile.
	 * 
	 * @param profile
	 */
	public void updateProfile(Profile profile) {
		profileTab.updateProfile(profile);
	}

	/**
	 * Updates the profile tab.
	 */
	public void update() {
		profileTab.update();
	}

	/**
	 * An extraction has started - GUI update.
	 */
	public void hasStartedExtract() {
		once.setEnabled(false);
		bar1.setVisible(true);
	}

	/**
	 * The extraction is done - GUI update.
	 */
	public void isDoneExtract() {
		once.setEnabled(true);
		bar1.setVisible(false);
		// profileTab.isDone();
		status2.setText("");
	}

	/**
	 * Gets the GUI window to the foreground.
	 */
	public void toFg() {
		mainFrame.toFront();
		mainFrame.repaint();
	}

	/**
	 * Updates a state String.
	 * 
	 * @param task
	 *            The String to be shown.
	 */
	public void updateDoing(String task) {
		status2.setText(task);
	}

	/**
	 * Event handler for the main GUI class.
	 */
	class EventListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == startStop) {
				if (startStop.isSelected()) {
					stopExtraction(true);
				} else {
					startExtraction(true);
				}
			} else if (e.getSource() == once) {
				controller.extractor
						.extract(controller.profileController.getProfiles(),
								true, false);
			}
		}
	}

}

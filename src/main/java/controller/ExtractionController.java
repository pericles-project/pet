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
package controller;

import static configuration.Constants.EXTRACTION_PREFERENCES_FILE;
import static configuration.Log.FLOW_LOGGER;
import gui.GUI;
import gui.StartupWindow;
import gui.SystemTrayIcon;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.Part;
import model.Profile;
import utility.PropertiesSaverAndLoader;
import cli.CLI;
import controller.ProfileController.ProfilePart;

/**
 * The ExtractionController is the main controlling class of the application; It
 * controls the main application thread and the extraction process and the
 * {@link Extractor}. Furthermore it manages the interaction between
 * {@link ProfileController}, {@link ModuleController} and
 * {@link StorageController}, and it updates the user interfaces {@link GUI} and
 * {@link CLI}. A {@link FileMonitorDaemon} is used to observe file changes of
 * the files added to the application.
 */
public class ExtractionController {
	public ModuleController modules = new ModuleController();
	public ProfileController profileController;
	public StorageController storageController;
	public Extractor extractor = null;
	public GUI gui = null;
	public FileMonitorDaemon fileMonitorDaemon = null;
	private SystemTrayIcon sysTray = null;
	public static EventController eventq;

	/**
	 * Constructor to start without graphics and updates, mainly for testing
	 * purposes. Use the {@link ExtractionControllerBuilder} for non-testing
	 * purposes.
	 */
	public ExtractionController() {
		this(new ExtractionControllerBuilder().headless().once());
	}

	/**
	 * Constructor that works with the builder pattern. Use the
	 * {@link ExtractionControllerBuilder} for configurations.
	 * 
	 * @param builder
	 *            Builder for this class
	 */
	public ExtractionController(ExtractionControllerBuilder builder) {
		// Warning: the order of init is important.
		storageController = new StorageController(builder.storageSystem);
		extractor = new Extractor(builder.updateExtraction, this);
		eventq = new EventController(this);
		fileMonitorDaemon = new FileMonitorDaemon(this);
		profileController = new ProfileController(this);
		new CLI(this);
		if (builder.graphic && !GraphicsEnvironment.isHeadless()) {
			// check if graphic is wanted and possible, then start it
			 try {
	                System.setProperty("apple.laf.useScreenMenuBar", "true");
	                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        }
	        catch(ClassNotFoundException e) {
	                System.out.println("ClassNotFoundException: " + e.getMessage());
	        }
	        catch(InstantiationException e) {
	                System.out.println("InstantiationException: " + e.getMessage());
	        }
	        catch(IllegalAccessException e) {
	                System.out.println("IllegalAccessException: " + e.getMessage());
	        }
	        catch(UnsupportedLookAndFeelException e) {
	                System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
	        }
			sysTray = new SystemTrayIcon(this);
			startGui();
			if (builder.firstStart){
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new StartupWindow(new Point(100,100),new Dimension(410,450));
					}
				});				
			}
		}
	}

	/**
	 * Starts the graphical user interface. The application can run without the
	 * GUI.
	 * 
	 * @see GUI
	 */
	public void startGui() {
		final ExtractionController me = this;
		if (gui == null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					gui = new GUI(me);
					gui.toFg();
				}
			});
		}

	}

	/**
	 * Called by the graphical user interface to indicate that the GUI was
	 * closed.
	 */
	public void stopGui() {
		gui = null;
	}

	/**
	 * Deletes all extracted information. Warning: Also the information from
	 * storage will be deleted!
	 */
	public void deleteAllMetadata() {
		storageController.deleteAllMetadata();
		profileController.deleteAllMetadata();
		gui.update();
	}

	/**
	 * A file has changed. Update-extract all modules that include the file.
	 * 
	 * @param file
	 *            Path to the modified file.
	 */
	public void updateFileModification(Path file) {
		FLOW_LOGGER.info("Update file modification: " + file);
		if (extractor.getUpdateExtraction()) {
			HashSet<ProfilePart> profiles = profileController
					.getProfilesWithPath(file);
			for (ProfilePart p : profiles) {
				extractor.extractNewParts(new Part[] { p.part }, p.profile,
						true);
			}
		}
	}

	/**
	 * A file was deleted. Inform all profiles that include this file to remove
	 * it and update the {@link GUI}.
	 * 
	 * @param file
	 */
	public void updateFileDeletion(Path file) {
		FLOW_LOGGER.info("Update file deletion: " + file);
		profileController.getProfilesWithPath(file);
		if (gui != null) {
			gui.update();
		}
	}

	/**
	 * Updates the {@link GUI} at profile changes.
	 * 
	 * @param profile
	 */
	public void updateProfileView(Profile profile) {
		if (gui != null) {
			gui.updateProfile(profile);
		}
	}

	/**
	 * Saves all application configurations and profile settings during the
	 * application shutdown.
	 */
	public void saveConfigs() {
		FLOW_LOGGER.info("Save configurations");
		profileController.saveConfig();
		this.saveConfig();
	}

	/**
	 * Saves all application configurations during the application shutdown.
	 */
	private void saveConfig() {
		Properties properties = new Properties();
		properties.setProperty("once", "" + !extractor.getUpdateExtraction());
		properties.setProperty("nographic", "" + (sysTray == null));
		PropertiesSaverAndLoader.saveProperties(properties,
				EXTRACTION_PREFERENCES_FILE);
	}

	/**
	 * Exit this application.
	 */
	public void exit() {
		System.exit(0);
	}

	/**
	 * Updates the current-running-extraction-Symbol of the {@link GUI}.
	 * 
	 * @param running
	 *            Was the extraction run started, or finished?
	 */
	public void runningExtraction(boolean running) {
		if (gui != null) {
			if (running) {
				gui.hasStartedExtract();
			} else {
				gui.isDoneExtract();
			}
		}
	}

	/**
	 * Shows a state String at the {@link GUI}.
	 * 
	 * @param task
	 *            the state String
	 */
	public void updateDoing(String task) {
		if (gui != null) {
			gui.updateDoing(task);
		}
	}
}

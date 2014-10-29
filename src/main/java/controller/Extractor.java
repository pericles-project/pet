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

import static configuration.Log.EXCEPTION_LOGGER;
import static configuration.Log.FLOW_LOGGER;
import static utility.FileUtils.fileExists;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import model.Environment;
import model.ExtractionResult;
import model.Part;
import model.Profile;
import modules.AbstractEnvironmentModule;
import modules.AbstractFileDependentModule;
import modules.AbstractModule;

/**
 * This information extractor extracts information from the computer system
 * environment. This information can be file-independent, and therewith valid
 * for the whole environment, or file-dependent information.
 */
public class Extractor {
	private final ExtractionController controller;
	private boolean updateExtraction;
	private boolean daemonsRunning;
	ExecutorService STE;

	/**
	 * Construct an Extractor. This will be called by the
	 * {@link ExtractionController}.
	 * 
	 * @param updateExtraction
	 * @param controller
	 */
	protected Extractor(boolean updateExtraction,
			ExtractionController controller) {
		this.updateExtraction = updateExtraction;
		this.controller = controller;
		STE = Executors.newSingleThreadExecutor();
	}

	/**
	 * Start an extraction for all profiles in the HashSet. This will use all
	 * file dependent modules of each profile on the profiles files and the file
	 * independent modules to extract the environment.
	 * 
	 * @param profiles
	 *            set of {@link Profile}s to be extracted
	 * @param isAsync
	 *            flag whether the extraction can be executed asynchronous or
	 *            synchronous.
	 * @param partsOnly
	 *            flag whether only file-dependent information should be
	 *            extracted
	 * @return Future object
	 */
	public Future<Object> extract(final Collection<Profile> profiles,
			final boolean isAsync, final boolean partsOnly) {
		Future<Object> future = STE.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				if (isAsync) {
					controller.runningExtraction(true);
				}
				for (final Profile profile : profiles) {
					extract(profile, partsOnly);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							controller.updateProfileView(profile);
						}
					});
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						controller.runningExtraction(false);
					}
				});
				return null;
			}
		});
		if (!isAsync) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Synchronization exception at extraction", e);
			}
		}
		return future;
	}

	/**
	 * Extract one specific {@link Part} belonging to a {@link Profile}. This
	 * will use all file dependent modules of the profile on the parts file.
	 * 
	 * This method will be called, if a file has been modified.
	 * 
	 * @param parts
	 *            the parts to extract
	 * @param profile
	 *            the profile to extract, null ot extract on all profiles
	 * @param isAsync
	 *            flag whether the extraction can be executed asynchronous or
	 *            synchronous.
	 * @return future object
	 */
	public Future<Object> extractNewParts(final Part[] parts, Profile profile,
			final boolean isAsync) {
		final Collection<Profile> profiles;
		if (profile != null) {
			profiles = new HashSet<Profile>();
			profiles.add(profile);
		} else {
			profiles = controller.profileController.getProfiles();
		}
		Future<Object> future = STE.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				if (isAsync) {
					controller.runningExtraction(true);
				}
				for (final Profile profile : profiles) {
					if (!profile.isEnabled()) {
						continue;
					}
					for (final Part part : parts) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								controller.updateDoing(profile.getName() + ": "
										+ part.fileName);
							}
						});
						for (AbstractModule module : profile.getModules()) {
							extract(part, module, profile);
						}
					}
					try {
						StorageController.storage.save(profile);
					} catch (Exception e) {
						EXCEPTION_LOGGER.log(Level.SEVERE,
								"Exception while saving results", e);
					}
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							controller.updateProfileView(profile);
						}
					});
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						controller.runningExtraction(false);
					}
				});
				return null;
			}
		});
		if (!isAsync) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Synchronization exception at extraction", e);
			}
		}
		return future;
	}

	/**
	 * Starts extraction of a {@link Profile}
	 * 
	 * @param profile
	 *            Profile to be extracted
	 * @param partsOnly
	 *            Only the file-dependent information should be extracted.
	 */
	private void extract(final Profile profile, boolean partsOnly) {
		if (!profile.isEnabled())
			return;// don't extract disabled profiles
		for (final Part part : profile.getParts()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					controller.updateDoing(profile.getName() + ": "
							+ part.fileName);
				}
			});
			for (AbstractModule module : profile.getModules()) {
				extract(part, module, profile);
			}
		}
		if (!partsOnly) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					controller.updateDoing("environment for: "
							+ profile.getName());
				}
			});
			for (AbstractModule module : profile.getModules()) {
				extract(module, profile.getEnvironment());
			}
		}
		try {
			StorageController.storage.save(profile);
		} catch (Exception e) {
			EXCEPTION_LOGGER.log(Level.SEVERE,
					"Exception while saving results", e);
		}
	}

	/**
	 * Starts the extraction of file-dependent information for a specific
	 * (mostly new added) {@link Part} belonging to a {@link Profile} with a
	 * specific {@link AbstractModule}.
	 * 
	 * @param part
	 *            extracts information depending on this file
	 * @param module
	 *            executes this extraction module
	 * @param profile
	 *            the part belongs to this Profile
	 */
	private void extract(Part part, AbstractModule module, Profile profile) {
		if (isExtractableModule(module)
				&& module instanceof AbstractFileDependentModule) {
			if (fileExists(part.getFile())) {
				try {
					ExtractionResult result = ((AbstractFileDependentModule) module)
							.extractFileDependentInformation(part.getFile());
					part.addExtractionResult(result);
					FLOW_LOGGER.info("Part extraction with module: "
							+ module.getConfig().moduleDisplayName + " and part: "
							+ part.getPath());
				} catch (Exception e) {
					EXCEPTION_LOGGER.log(Level.SEVERE,
							"Exception at module extraction call for module: "
									+ module.moduleName, e);
				}
			}
		}
	}

	/**
	 * Extract a file-independent extraction module and store the extracted
	 * information into the given {@link Environment} class.
	 * 
	 * @param module
	 *            {@link AbstractModule} to be executed
	 * @param environment
	 *            {@link Environment} to save the information
	 */
	private void extract(AbstractModule module, Environment environment) {
		if (isExtractableModule(module)
				&& module instanceof AbstractEnvironmentModule) {
			try {
				ExtractionResult result = ((AbstractEnvironmentModule) module)
						.extractInformation();
				environment.addExtractionResult(result);
				FLOW_LOGGER.info("Environment extraction with module: "
						+ module.getConfig().moduleDisplayName);
			} catch (Exception e) {
				EXCEPTION_LOGGER.log(Level.SEVERE,
						"Exception at module extraction call for module: "
								+ module.getConfig().moduleDisplayName, e);
			}
		}
	}

	/**
	 * Checks whether an {@link AbstractModule} can be executed. Is true, when
	 * the module is enabled and well configured.
	 * 
	 * @param module
	 *            AbstractModule to be checked.
	 * @return flag to indicate whether the module can be extracted
	 */
	private boolean isExtractableModule(AbstractModule module) {
		if (module == null)
			return false;
		return module.isSelected() && module.supportsThisOS()
				&& module.getConfig().configurationIsValid();
	}

	/**
	 * This method will enable or disable update extractions, if files were
	 * modified. Update extractions will be enabled in continuous extraction
	 * mode.
	 * 
	 * @param updateExtraction
	 */
	public void setUpdateExtraction(boolean updateExtraction) {
		this.updateExtraction = updateExtraction;
	}

	/**
	 * Get the status, if update extractions are enabled or disabled. If updated
	 * extractions are enabled, the tool is in continuous extraction mode.
	 * 
	 * @return status of update extractions
	 */
	public boolean getUpdateExtraction() {
		return updateExtraction;
	}

	/**
	 * Get the status, if the environment monitor daemons are running.
	 * 
	 * @return status of running daemons
	 */
	public boolean getDaemonsRunning() {
		return daemonsRunning;
	}

	/**
	 * Stops all daemon modules of all profiles.
	 */
	public void stopDaemons() {
		FLOW_LOGGER.info("All daemon modules stopped");
		for (final Profile profile : controller.profileController.getProfiles()) {
			profile.stopAllDaemons();
		}
		daemonsRunning = false;
	}

	/**
	 * Starts all daemon modules of all profiles.
	 */
	public void startDaemons() {
		FLOW_LOGGER.info("All daemon modules started");
		for (final Profile profile : controller.profileController.getProfiles()) {
			profile.startAllDaemons();
		}
		daemonsRunning = true;
	}
}

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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import model.Event;
import model.Profile;
import configuration.Log;

public class EventController {
	private final LinkedBlockingQueue<Event> theQueue = new LinkedBlockingQueue<Event>();
	ExtractionController ec;
	private final Thread eventThread;
	StorageEventProcessor store = new StorageEventProcessor();
	private final LinkedList<EventProcessorInterface> eventProcessors = new LinkedList<EventProcessorInterface>();
	LinkedList<Event> late = new LinkedList<Event>();

	public EventController(ExtractionController ec) {
		super();
		this.ec = ec;
		eventThread = new Thread(new EventProcessor(), "EventPocessor");
		eventThread.setDaemon(true);
		eventThread.start();
	}

	public void submitEvent(Event e) {
		theQueue.add(e);
	}

	public void addProcessor(EventProcessorInterface p) {
		eventProcessors.add(p);
	}

	public void removeProcessor(EventProcessorInterface p) {
		eventProcessors.remove(p);
	}

	private class EventProcessor implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Event e = theQueue.take();

					// Report event to the GUI
					if (ec.gui != null && ec.gui.eventTab != null) {
						for (Event t : late)
							ec.gui.eventTab.appendEvent(t.toString() + "\n");
						ec.gui.eventTab.appendEvent(e.toString() + "\n");
					} else {
						late.add(e);
					}
					if ( e.reporter != null) {
						// First we check if we need to record the event
						if (e.reporter.getConfig().recordEvents || e.record) {
							store.processevent(e);
						}
						// finally we look if we need to add the file to a profile
						String profileName = e.reporter.getConfig().eventAddToProfile;
						if (e.fileName != null && profileName != null
								&& profileName.trim().length() > 0) {
							HashSet<Profile> pp = ec.profileController
									.getProfiles();
							Path dest = Paths.get(e.fileName);
							try {
								Profile p = null;
								for (Profile a:pp){
									if (a.getName().equals(profileName))
										p = a;
								}
								if (p != null) {
									HashSet<Path> paths = new HashSet<Path>();
									paths.add(dest);
									p.addAllPartsFromPaths(paths, true);
									// System.out.println("Adding part " + dest);
								}
							} catch (Exception x) {
								Log.EXCEPTION_LOGGER.log(
										Level.SEVERE,
										"Error adding file from Event"
												+ e.toString(), x);
							}
						}
					}
					// then we look up other event processors
					for (EventProcessorInterface p : eventProcessors) {
						p.processevent(e);
					}

				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}

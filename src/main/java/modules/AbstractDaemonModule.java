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
package modules;

import model.Event;
import controller.ExtractionController;

public abstract class AbstractDaemonModule extends AbstractModule implements
		Runnable {
	abstract protected void observe() throws InterruptedException;

	protected Thread runner;
	protected boolean isDaemon = true;

	public AbstractDaemonModule() {
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				observe();
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// EXCEPTION_LOGGER.log(Level.SEVERE,
				// "Exception at run() of abstract monitor", e);
				return;
			}
		}
	}

	public void stop() {
		if (runner != null) {
			runner.interrupt();
			runner = null;
		}
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	public void create() {
		runner = new Thread(this);
		runner.setDaemon(isDaemon);
		runner.setName(moduleName + "-thread");
	}

	public void start() {
		if (isSelected() && supportsThisOS()) {
			if (runner == null) {
				create();
			}
			if (!runner.isAlive()) {
				runner.start();
			}
		}
	}

	public boolean isRunning() {
		if (runner == null)
			return false;
		return runner.isAlive();

	}

	protected void submitEvent(Event e) {
		if (ExtractionController.eventq != null) {
			ExtractionController.eventq.submitEvent(e);
		} else {
			System.out.println("Null event controller!");
		}
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		if (!selected) {
			stop();
		} else {
			start();
		}
	}

}

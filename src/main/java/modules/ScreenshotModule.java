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

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.ExtractionResult;
import controller.StorageController;

public class ScreenshotModule extends AbstractEnvironmentModule {

	public class ScreenShot {
		public String format;
		public String screenName;
		public String imageContentsId;
		
		public ScreenShot() {
			// TODO Auto-generated constructor stub
		}
	}

	public ScreenshotModule() {
		super();
		this.getConfig().enabled = false;
	}

	/**
	 * Stores a screenshot of the selected component to the binary array.
	 * format: "jpg", "png",..
	 * 
	 * @param format
	 * @param screen
	 * @return binary array that represents the screenshot
	 */
	public static byte[] screenShot(String format, GraphicsDevice screen) {
		try {
			Robot robot = new Robot();
			Rectangle captureSize = new Rectangle(screen.getDisplayMode()
					.getWidth(), screen.getDisplayMode().getHeight());
			BufferedImage bufferedImage = robot
					.createScreenCapture(captureSize);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, format, bo);
			return bo.toByteArray();
		} catch (AWTException e) {
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public void setModuleName() {
		this.moduleName = "Screenshot module";
	}

	@Override
	public ExtractionResult extractInformation() {
		ExtractionResult r = new ExtractionResult(this);
		String format = "jpg";
		if (!GraphicsEnvironment.isHeadless()) {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			ScreenShot[] screenshots = new ScreenShot[gs.length];
			for (int j = 0; j < gs.length; j++) {
				GraphicsDevice gd = gs[j];
				screenshots[j] = new ScreenShot();
				screenshots[j].format = format;
				screenshots[j].screenName = gd.getIDstring();
				byte[] sshot = screenShot("jpg", gd);
				screenshots[j].imageContentsId = StorageController.storage
						.storeExternalData(sshot);
			}
			r.results = screenshots;
			return r;
		}
		return null;
	}

	@Override
	public void setVersion() {
		this.version = "0.1";
	}

	@Override
	public String getModuleDescription() {
		return "Screnshot module";
	}
}

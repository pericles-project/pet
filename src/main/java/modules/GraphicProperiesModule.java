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

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.LinkedList;

import model.ExtractionResult;

/**
 * Module that extracts a list of meta data attributes via java
 * System.getProperty(..).
 */
public class GraphicProperiesModule extends AbstractEnvironmentModule {

	public class DisplayInformation {

		public boolean isDefaultDisplay;
		public String idString;
		public int bitDepth;
		public int refreshRate;
		public int width;
		public int height;

	}

	public class GraphicProperies {
		public String[] font_family_names;
		public LinkedList<DisplayInformation> displayInformation;
		public int screenResolution;
	}

	@Override
	public ExtractionResult extractInformation() {
		ExtractionResult pr = new ExtractionResult(this);
		if (GraphicsEnvironment.isHeadless())
			return null;
		GraphicProperies gp = new GraphicProperies();
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		gp.font_family_names = ge.getAvailableFontFamilyNames();
		// for (Font f:ge.getAllFonts()) {
		// try {
		// System.out.println(f);
		// f = f.deriveFont(12.2f);
		// Method m = Font.class.getDeclaredMethod("getFont2D");
		// m.setAccessible(true);
		// Object f2d = m.invoke(f);
		// System.out.println(f2d.getClass());
		// Field fd =sun.font.PhysicalFont.class.getDeclaredField("platName");
		// sun.font.CFontManager d = (CFontManager)
		// sun.font.CFontManager.getInstance();
		//
		// String fontPath = d.getFileNameForFontName(f.getFontName());
		// System.out.println(d.getFontPath(true));
		// System.out.println(fontPath);
		// fontPath = d.getPlatformFontPath(true);
		// System.out.println(fontPath);
		// fontPath = d.getFontConfiguration().getExtraFontPath();
		// System.out.println(fontPath);
		// fontPath =
		// d.getFontConfiguration().getFileNameFromPlatformName(f.getFontName());
		// HashSet<String> c= d.getFontConfiguration().getAWTFontPathSet();
		// System.out.println(c);
		// } catch (IllegalAccessException | IllegalArgumentException
		// | InvocationTargetException | NoSuchMethodException
		// | SecurityException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NoSuchFieldException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		GraphicsDevice[] gds = ge.getScreenDevices();
		gp.displayInformation = new LinkedList<DisplayInformation>();
		for (GraphicsDevice gd : gds) {
			DisplayInformation di = new DisplayInformation();
			gp.displayInformation.add(di);
			if (gd.equals(ge.getDefaultScreenDevice())) {
				di.isDefaultDisplay = true;
				DisplayMode dm = gd.getDisplayMode();
				di.idString = gd.getIDstring();
				di.bitDepth = dm.getBitDepth();
				di.refreshRate = dm.getRefreshRate();
				di.width = dm.getWidth();
				di.height = dm.getHeight();
			}

		}
		gp.screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();

		pr.setResults(gp);
		return pr;
	}

	@Override
	public String getModuleDescription() {
		String description = "This module extracts the following operating Graphic system properties:"
				+ "\n- font family names"
				+ "\n- screen resolution"
				+ "\nAnd for each display"
				+ "\n- dispaly size in pixel"
				+ "\n- bit depth"
				+ "\n- refresh rate"
				+ "\n\nThe module uses Java's native GraphicsEnvironment methods.";
		return description;
	}

	@Override
	public void setModuleName() {
		moduleName = "Graphic System properties snapshot";
	}

	@Override
	public void setVersion() {
	}
}

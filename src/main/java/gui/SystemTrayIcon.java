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

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import controller.ExtractionController;

/**
 * This class creates a system tray icon with a menu menu and handles user
 * events, if an option of the menu is selected by the user.
 */
public final class SystemTrayIcon implements ActionListener {
	public static String TRAY_ICON_IMAGE = "/images/logo.png";

	protected TrayIcon trayIcon;
	protected PopupMenu menu;
	protected SystemTray systemTray;
	protected ExtractionController controller;

	public SystemTrayIcon(ExtractionController controller) {
		this.controller = controller;
		this.menu = new PopupMenu();
		this.trayIcon = createTrayIcon();
		if (trayIcon != null)
			addTayIconToSystemTray(trayIcon);
	}

	private TrayIcon createTrayIcon() {
		URL u = this.getClass().getResource(TRAY_ICON_IMAGE);
		Image image = null;
		if (u==null)
			return null;
		image = Toolkit.getDefaultToolkit().getImage(u);
		createMenu();
		TrayIcon trayIcon = new TrayIcon(image, "Pericles", menu);
		trayIcon.setImageAutoSize(true);
		return trayIcon;
	}

	MenuItem guiItem;
	MenuItem exitItem;

	private void createMenu() {
		guiItem = createMenuItem("Open GUI");
		exitItem = createMenuItem("Exit tool");
	}

	private MenuItem createMenuItem(String menuText) {
		MenuItem item = new MenuItem(menuText);
		item.addActionListener(this);
		menu.add(item);
		return item;
	}

	private void addTayIconToSystemTray(TrayIcon trayIcon) {
		this.systemTray = SystemTray.getSystemTray();
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exitItem) {
			this.systemTray = SystemTray.getSystemTray();
			systemTray.remove(trayIcon);
			controller.exit();
		} else if (e.getSource() == guiItem) {
			controller.startGui();
		}
	}
}

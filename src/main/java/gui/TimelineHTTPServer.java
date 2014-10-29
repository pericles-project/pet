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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import main.StartCommands;
import storage.FileStorageInterface;
import configuration.Constants;
import controller.StorageController;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class TimelineHTTPServer extends NanoHTTPD {

	private static final int PORT = 7774;
	private static TimelineHTTPServer s;

	private TimelineHTTPServer(int port) {
		super(port);
	}

	public static TimelineHTTPServer init() {
		if (s == null) {
			s = new TimelineHTTPServer(PORT);
			try {
				s.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		Response r = new Response("nothing to see here!");
		if (method.equals(Method.GET)) {
			// System.out.println(session.getUri());
			if (session.getUri().equalsIgnoreCase("/")) {
				r = getIndex();

			} else {
				r = getFile(session.getUri());
			}
		}
		return r;
	}

	private Response getFile(String uri) {
		String mime = "text/plain";
		if (uri.endsWith(".css")) {
			mime = "text/css";
		} else if (uri.endsWith(".js") || uri.endsWith(".json")) {
			mime = "application/javascript";
		} else if (uri.endsWith(".html")) {
			mime = "text/html";
		}
		if (uri.endsWith("events.json"))
			return getEvents();
		URL u = this.getClass().getResource("/httpdata" + uri);
		Response r;
		try {
			r = new Response(Status.OK, mime, u.openStream());
		} catch (Exception e) {
			return new Response(Status.NOT_FOUND, mime, e.getMessage());
		}
		return r;
	}

	private Response getEvents() {
		String mime = "application/javascript";
		StringBuffer sb = new StringBuffer("var mydata = JSON.parse('[");
		List<String> l = StorageController.storage.readEventData();
		if (l != null) {
			for (int c = 0; c < l.size() - 1; c++) {
				sb.append(l.get(c)).append(",");
				// System.out.println(l.get(c));
			}
			sb.append(l.get(l.size() - 1));
		}
		sb.append("]');");
		return new Response(Status.OK, mime, sb.toString());
	}

	private Response getIndex() {
		return getFile("/documentationEvents.html");
	}

	public static void main(String[] args) {
		StartCommands startInterface = new StartCommands(args);
		new Constants(startInterface.options.destination);
		StorageController.storage = new FileStorageInterface();
		init();
		try {
			System.in.read();
		} catch (Throwable ignored) {
		}
	}
}

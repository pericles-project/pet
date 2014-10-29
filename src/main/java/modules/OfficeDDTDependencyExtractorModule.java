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

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.ExtractionResult;

import org.apache.poi.hssf.record.SupBookRecord;
import org.ddt.Constants;
import org.ddt.Link;
import org.ddt.listener.dsi.DocumentSummaryInfoListener;
import org.ddt.listener.ole.OleStreamListener;
import org.ddt.listener.records.DConRefListener;
import org.ddt.listener.records.DConRefRecord;
import org.ddt.listener.records.SupBookListener;
import org.ddt.processor.FileProcessor;
import org.ddt.processor.HSLFFileProcessor;
import org.ddt.processor.POIFSEventProcessor;
import org.ddt.processor.RecordEventProcessor;

public class OfficeDDTDependencyExtractorModule extends
		AbstractFileDependentModule {
	// private final Logger logger = LogManager
	// .getLogger(OfficeDDTDependencyExtractorModule.class.getName());

	private static List<FileProcessor> processors;

	@Override
	public void setModuleName() {
		moduleName = "Office document dependencies";
	}

	public class OfficeDependency {
		public List<Link> link;
		public String filePath;
	}

	@Override
	public ExtractionResult extractInformation(Path path) {
		Logger.getLogger("org.ddt").setLevel(Level.SEVERE);
		LinkedList<OfficeDependency> res = new LinkedList<OfficeDependency>();
		ExtractionResult extractionResult = new ExtractionResult(this);
		registerDefaultProcessors();
		File file = path.toFile();
		if (!file.exists() || !file.canRead() || file.isDirectory())
			return extractionResult;
		int fileType = Constants.FILETYPE_ALL;
		if (file.getName().endsWith(".doc")) {
			fileType = Constants.FILETYPE_WORD;
		} else if (file.getName().endsWith(".ppt")) {
			fileType = Constants.FILETYPE_POWERPOINT;
		} else if (file.getName().endsWith(".xls")) {
			fileType = Constants.FILETYPE_EXCEL;
		}

		if (fileType == Constants.FILETYPE_ALL)
			return extractionResult;
		OfficeDependency dependency = new OfficeDependency();
		for (FileProcessor processor : processors) {
			try {
				if (processor.acceptsFileType(fileType)) {
					List<Link> l = processor.process(file);
					if (l.size() > 0) {
						if (dependency.link == null) {
							dependency.link = new LinkedList<Link>();
							dependency.filePath = path.toAbsolutePath()
									.toString();
						}
						dependency.link.addAll(l);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		if (dependency.link != null) {
			res.add(dependency);
		}

		if (res.isEmpty())
			return null;
		extractionResult.setResults(res);
		return extractionResult;
	}

	private void registerDefaultProcessors() {
		if (processors != null)
			return;
		processors = new LinkedList<FileProcessor>();
		POIFSEventProcessor pep = new POIFSEventProcessor();
		pep.registerListener(new DocumentSummaryInfoListener());// ,
																// "\005DocumentSummaryInformation");
		POIFSEventProcessor pep2 = new POIFSEventProcessor();
		pep2.registerListener(new OleStreamListener());
		processors.add(pep);
		processors.add(pep2);

		processors.add(new HSLFFileProcessor());

		RecordEventProcessor rep = new RecordEventProcessor();
		rep.registerListener(new SupBookListener(), SupBookRecord.sid);
		rep.registerListener(new DConRefListener(), DConRefRecord.sid);

		processors.add(rep);
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	@Override
	public String getModuleDescription() {
		return "Office document dependencies";
	}
}

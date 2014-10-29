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
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.ExtractionResult;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.google.common.collect.HashMultimap;

public class PDFFontDependencyExtractorModule extends
		AbstractFileDependentModule {

	public static String[] standard14 = new String[] { "Times-Roman",
			"Times-Bold", "Times-Italic", "Times-BoldItalic", "Helvetica",
			"Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique",
			"Courier", "Courier-Bold", "Courier-Oblique",
			"Courier-BoldOblique", "Symbol", "ZapfDingbats" };
	static {
		Arrays.sort(standard14);
	}

	@Override
	public void setModuleName() {
		moduleName = "PDF Font dependencies";
	}

	public class FontInformation {

		public String fontName;
		public String charset;
		public String fontType;
		public int fontFlags;
		public boolean isEmbedded;
		public boolean isSubset;
		public boolean isToUnicode;
		public String pitchAndFamily;
		public String fontFamily;
		public String fontStretch;
		public float fontWeight;
		public int numGlyph;
		public String baseName;
		public String encoding;

		// @Override
		// public boolean equals(Object obj) {
		// if (obj instanceof FontInformation) {
		// FontInformation f = (FontInformation) obj;
		// return fontName.equals(f.fontName);
		// }
		// return false;
		// }
		// @Override
		// public int hashCode() {
		// return fontName.hashCode();
		// }
		@Override
		public String toString() {

			return baseName + fontName + " " + '\t' + fontType + "\t"
					+ isEmbedded + "\t" + encoding + "\n";
		}

	}

	public class PDFFontResults {
		public List<FontInformation> allFonts;
		public List<FontInformation> notEmbeddedFonts;

		public PDFFontResults(List<FontInformation> allFonts,
				List<FontInformation> notEmbeddedFonts) {
			super();
			this.allFonts = allFonts;
			this.notEmbeddedFonts = notEmbeddedFonts;
		}

		public PDFFontResults() {
			// TODO Auto-generated constructor stub
		}
	}

	@Override
	public ExtractionResult extractInformation(Path path) {
		Logger.getLogger("pdf.font").setLevel(Level.SEVERE);

		ExtractionResult extractionResult = new ExtractionResult(this);

		File file = path.toFile();
		if (!file.exists() || !file.canRead() || file.isDirectory())
			return extractionResult;
		;
		try {
			PDFFontResults e = extractFontList(path.toFile());

			extractionResult.setResults(e);
		} catch (IOException e) {
			Logger.getLogger("pdf.font").log(Level.SEVERE,
					"Error parsing PDF file", e);
		} catch (InvalidParameterException x) {
			// System.out.println("not a pdf " + path);
		}
		return extractionResult;
	}

	public PDFFontResults extractFontList(File f) throws IOException,
			InvalidParameterException {
		PDDocument document;
		try {
			document = PDDocument.load(f);
		} catch (IOException x) {
			throw new InvalidParameterException("Not a PDF file");
		}
		SortedSet<FontInformation> ret = new TreeSet<FontInformation>(
				new Comparator<FontInformation>() {

					@Override
					public int compare(FontInformation o1, FontInformation o2) {
						int a = o1.fontName.compareTo(o2.fontName);
						if (a != 0)
							return a;
						else
							return o1.fontType.compareTo(o2.fontType);
					}

				});

		document.getDocumentCatalog().getAllPages();
		// The code down here is easier as it gets all the fonts used in the
		// document. Still, this would inlcude unused fonts, so we get the fonts
		// page by page and add them to a Hash table.
		for (COSObject c : document.getDocument()
				.getObjectsByType(COSName.FONT)) {
			if (c == null || !(c.getObject() instanceof COSDictionary)) {
				continue;
				// System.out.println(c.getObject());
			}

			COSDictionary fontDictionary = (COSDictionary) c.getObject();
			// System.out.println(dic.getNameAsString(COSName.BASE_FONT));
			// }
			// }
			// int pagen = document.getNumberOfPages();
			// i=0;
			// for (int p=0;p<pagen;p++){
			// PDPage page = (PDPage)pages.get(p);
			// PDResources res = page.findResources();
			// //for each page resources
			// if (res==null) continue;
			// // get the font dictionary
			// COSDictionary fonts = (COSDictionary)
			// res.getCOSDictionary().getDictionaryObject( COSName.FONT );
			// for( COSName fontName : fonts.keySet() ) {
			// COSObject font = (COSObject) fonts.getItem( fontName );
			// // if the font has already been visited we ingore it
			// long objectId = font.getObjectNumber().longValue();
			// if (ret.get(objectId)!=null)
			// continue;
			// if( font==null || ! (font.getObject() instanceof COSDictionary) )
			// continue;
			// COSDictionary fontDictionary = (COSDictionary)font.getObject();

			// Type MUSt be font
			if (!fontDictionary.getNameAsString(COSName.TYPE).equals("Font")) {
				continue;
			}
			// get the variables
			FontInformation fi = new FontInformation();
			fi.fontType = fontDictionary.getNameAsString(COSName.SUBTYPE);

			String baseFont = fontDictionary.getNameAsString(COSName.BASE_FONT);
			if (baseFont == null) {
				continue;
			}
			if (Arrays.binarySearch(standard14, baseFont) >= 0) {
				continue;
			}
			COSDictionary fontDescriptor = (COSDictionary) fontDictionary
					.getDictionaryObject(COSName.FONT_DESC);
			COSBase enc = fontDictionary.getItem(COSName.ENCODING);
			COSBase uni = fontDictionary.getItem(COSName.TO_UNICODE);
			fontDictionary.getInt(COSName.FIRST_CHAR);
			fontDictionary.getInt(COSName.LAST_CHAR);
			String encoding;
			boolean toUnicode = uni != null;
			if (enc == null) {
				encoding = "standard14";
			}
			if (enc instanceof COSString) {
				encoding = ((COSString) enc).getString();
			} else {
				encoding = "table";
			}
			fi.isSubset = false;
			boolean t = true;
			// Type one and TT can have subsets defineing the basename see 5.5.3
			// pdfref 1.6
			// if (fi.fontType.lastIndexOf(COSName.TYPE1.getName())!=-1 ||
			// fi.fontType.equals(COSName.TRUE_TYPE.getName()) )
			if (baseFont != null) {
				if (baseFont.length() > 6) {
					for (int k = 0; k < 6; k++)
						if (!Character.isUpperCase(baseFont.charAt(k))) {
							t = false;
						}
					if (baseFont.charAt(6) != '+') {
						t = false;
					}
				} else {
					t = false;
				}
				fi.isSubset = t;
				if (fi.isSubset) {
					fi.baseName = baseFont.substring(0, 6);
					baseFont = baseFont.substring(7);
				}
			}
			fi.fontFlags = 0;
			if (fi.fontType.equals(COSName.TYPE0.getName())
					|| fi.fontType.equals(COSName.TYPE3.getName())) {
				fi.isEmbedded = true;
			}

			if (fontDescriptor != null) {
				// in Type1 charset indicates font is subsetted
				if (fontDescriptor.getItem(COSName.CHAR_SET) != null) {
					fi.isSubset = true;
				}
				if (fontDescriptor.getItem(COSName.FONT_FILE) != null
						|| fontDescriptor.getItem(COSName.FONT_FILE3) != null
						|| fontDescriptor.getItem(COSName.FONT_FILE2) != null) {
					fi.isEmbedded = true;
				}
				fi.fontFlags = fontDescriptor.getInt(COSName
						.getPDFName("Flags"));
				fi.fontFamily = fontDescriptor.getString(COSName.FONT_FAMILY);
				fi.fontStretch = fontDescriptor.getString(COSName.FONT_STRETCH);

			}
			fi.charset = encoding;
			fi.fontName = baseFont;
			fi.isToUnicode = toUnicode;
			fi.encoding = fontDictionary
					.getNameAsString(COSName.CID_TO_GID_MAP);

			ret.add(fi);

		} // for all fonts

		HashMultimap<String, FontInformation> m = HashMultimap.create();

		for (FontInformation ff : ret) {
			m.put(ff.fontName, ff);
		}
		LinkedList<FontInformation> missing = new LinkedList<FontInformation>();
		Set<String> k = m.keySet();
		for (String kk : k) {
			Set<FontInformation> s = m.get(kk);
			if (s.size() < 1) {
				continue;
			}
			if (s.size() > 1) {
				boolean found = false;
				FontInformation ff = null;
				for (FontInformation fonti : s) {
					if (!fonti.isEmbedded) {
						ff = fonti;
					} else {
						found = true;
					}
				}
				if (!found) {
					missing.add(ff);
				}
			} else {
				FontInformation ff = s.iterator().next();
				if (!ff.isEmbedded) {
					missing.add(ff);
				}
			}

		}

		// } // for all pages
		// Iterator<FontInformation> it = ret.iterator();
		// FontInformation prev = null;
		// LinkedList<FontInformation> toDelete = new
		// LinkedList<FontInformation>();
		// while (it.hasNext()) {
		// FontInformation current = it.next();
		//
		// if (prev!= null && prev.fontName.equals(current.fontName) &&
		// (prev.fontType.startsWith("CIDFontType") ||
		// current.fontType.startsWith("CIDFontType")))
		// toDelete.add(current);
		// prev = current;
		// }
		//
		// //ret.removeAll(toDelete);
		// FontInformation[] retArray =toDelete.toArray(new FontInformation[0]);
		//

		if (missing.size() == 0) {
			missing = null;
		} else {
			System.out.println("Found missing fonts: " + f);
			System.out.println(missing);
		}
		return new PDFFontResults(new LinkedList<FontInformation>(ret), missing);
	}

	@Override
	public void setVersion() {
		version = "1.0";
	}

	@Override
	public String getModuleDescription() {
		return "PDF Font dependencies";
	}
}

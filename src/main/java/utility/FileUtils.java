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
package utility;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This utility class simplifies the creation and deletion of files for the
 * application.
 */
public class FileUtils {
	private FileUtils() {
	};

	public static void createFile(String path) {
		Path file = Paths.get(path);
		createFile(file);
	}

	public static void createFile(Path file) {
		if (file != null && !Files.exists(file)) {
			try {
				Files.createFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createDirectory(String path) {
		Path directory = Paths.get(path);
		createDirectory(directory);
	}

	public static void createDirectory(Path path) {
		if (path != null && !fileExists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteFile(String file) {
		deleteFile(Paths.get(file));
	}

	public static void deleteFile(Path file) {
		try {
			Files.deleteIfExists(file);
		} catch (IOException e) {
		}
	}

	public static void deleteDirectory(String directory) {
		File directoryFile = new File(directory);
		if (isDirectory(directory)) {
			File[] files = directoryFile.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i].toPath().toString());
					} else {
						files[i].delete();
					}
				}
			}
		}
		deleteFile(directory);
	}

	public static boolean fileExists(Path file) {
		if (file == null || !Files.isReadable(file)) {
			return false;
		}
		return Files.exists(file);
	}

	public static boolean fileExists(String file) {
		return fileExists(Paths.get(file));
	}

	public static boolean isDirectory(String directoryPath) {
		if (directoryPath == null) {
			return false;
		}
		Path dir = Paths.get(directoryPath);
		return isDirectory(dir);
	}

	public static boolean isDirectory(Path dir) {
		if (dir == null) {
			return false;
		}
		return Files.isDirectory(dir) && Files.isReadable(dir)
				&& Files.isWritable(dir);
	}

	public static File getCurrentJarFolder(Class c) {
		File f;
		try {
			f = new File(c.getProtectionDomain().getCodeSource().getLocation()
					.toURI()).getParentFile();
			if (f.exists() && f.isDirectory()) {
				// System.out.println("Current JAR folder:" + f);
				return f;
			} else {

				System.out
						.println("Something odd getting current JAR location;"
								+ f + " we settle for ");
				f = new File("./");
				System.out.println(f);
				return f;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new File("./");

		}
	}

	public static void main(String[] args) {
		System.out.println(getCurrentJarFolder(FileUtils.class));
		System.out.println(ClassLoader.getSystemClassLoader().getResource(".")
				.getPath());
		try {
			String path = FileUtils.class.getProtectionDomain().getCodeSource()
					.getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			System.out.println(decodedPath);
			System.out.println(new File(FileUtils.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI()).getParentFile());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}

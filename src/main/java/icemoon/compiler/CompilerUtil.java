package icemoon.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class CompilerUtil {

	public static void copyFile(File tf, File file) throws IOException {
		try(var fin = new FileInputStream(tf)) {
			try(var fout = new FileOutputStream(file)) {
				fin.transferTo(fout);
			} 
		}
	}

	public static String basename(String filename, String extension) {
		return basename(filename, extension, File.separator);
	}

	public static String basename(String filename, String extension, String pathSep) {
		if (extension != null && filename.endsWith(extension)) {
			filename = filename.substring(0, filename.length() - extension.length());
		}
		int idx = filename.lastIndexOf(pathSep);
		if (idx != -1) {
			filename = filename.substring(idx + 1);
		}
		return filename;
	}

	public static boolean fileContains(String text, File file) throws IOException {
		BufferedReader fin = new BufferedReader(new FileReader(file));
		try {
			String line;
			while ((line = fin.readLine()) != null) {
				if (line.contains(text)) {
					return true;
				}
			}
			return false;
		} finally {
			fin.close();
		}
	}

}

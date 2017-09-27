package icemoon.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CompilerUtil {
	private static final int DEFAULT_BUFFER_SIZE = 65536;

	public static int copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void copyFile(File tf, File file) throws IOException {
		FileInputStream fin = new FileInputStream(tf);
		try {
			FileOutputStream fout = new FileOutputStream(file);
			try {
				copy(fin, fout);
			} finally {
				fout.close();
			}
		} finally {
			fin.close();
		}
		// TODO Auto-generated method stub

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

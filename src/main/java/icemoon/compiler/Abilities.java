package icemoon.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Abilities extends AbstractTool {

	private static Abilities instance = new Abilities();

	public static Abilities get() {
		return instance;
	}

	private Abilities() {
	}

	public boolean compile(String in, String out, File dir) throws IOException, InterruptedException {
		File outF = new File(out);
		if (!outF.isAbsolute() && dir != null) {
			outF = new File(dir, outF.getPath());
		}

		File inF = new File(in);
		if (!inF.isAbsolute() && dir != null) {
			inF = new File(dir, inF.getPath());
		}

		if (!inF.getName().endsWith(".txt")) {
			throw new IOException("Input file must end in .txt");
		}

		if (!outF.getName().endsWith(".nut")) {
			throw new IOException("Output file must end in .txt");
		}

		if (outF.getParentFile() != null && !outF.getParentFile().exists() && !outF.getParentFile().mkdirs()) {
			throw new IOException("Failed to create output directory for " + outF);
		}

		// Do the actual work in a temporary directory
		BufferedReader r = new BufferedReader(new FileReader(inF));
		try {
			String line;
			FileWriter fw = new FileWriter(outF);
			try {
				fw.write("AbilityIndex <- [");
				int lno = 1;
				int w = 0;
				while ((line = r.readLine()) != null) {
					String[] args = line.split("\t");
					if (args.length > 0) {
						if (args.length != 28 && args.length != 29) {
							System.err.println(
									"Incorrect number of fields on line " + lno + ". Expected 0, got " + args.length);
						} else {
							StringBuilder b = new StringBuilder();
							for (int i = 0 ; i < 28 ; i++) {
								if (b.length() > 0) {
									b.append(",");
								}
								b.append(process(lno, args[i]));
							}
							if (w > 0) {
								fw.write(",");
							}
							w++;
							fw.write("\r\n[" + b.toString() + "]");
						}
					}
					lno++;
				}
				fw.write("\r\n];\r\n");
				fw.flush();
			} finally {
				fw.close();
			}
		} finally {
			r.close();
		}
		outF.setLastModified(inF.lastModified());
		return true;
	}

	String process(int line, String l) throws IOException {
		if(!l.startsWith("\"") || !l.endsWith("\"")) {
			throw new IOException("Expected element on line " + line + " to start with \"");
		}
		return l.length() == 2 ? "\"\"" : "\"" + l.substring(1, l.length() - 1).replace("\"", "\\\"") + "\"";
	}
}

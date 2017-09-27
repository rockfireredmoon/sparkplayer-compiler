package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Sq extends AbstractTool {

	private static Sq instance = new Sq();

	private File compilerFile;

	public static Sq get() {
		return instance;
	}

	private Sq() {
		compilerFile = extract("sq.exe");
	}

	public boolean compile(String in, String out, File dir) throws IOException, InterruptedException {
		File outF = new File(out);
		if (!outF.isAbsolute() && dir != null)
			outF = new File(dir, outF.getPath());

		File inF = new File(in);
		if (!inF.isAbsolute() && dir != null)
			inF = new File(dir, inF.getPath());

		if (outF.getParentFile() != null && !outF.getParentFile().exists() && !outF.getParentFile().mkdirs())
			throw new IOException("Failed to create output directory for " + outF);

		List<String> args = getArgs();
		args.add(compilerFile.getAbsolutePath());
		args.add("-o");
		args.add(outF.getAbsolutePath());
		args.add("-c");
		args.add(in);

		StringBuilder sb = new StringBuilder();
		int ret = run(dir, args, sb);
		String output = sb.toString();
		System.out.println(output);
		if(output.contains("Error")) {
			return false;
		}
		return ret == 0;
	}

}

package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Abilities extends AbstractTool {

	private static Abilities instance = new Abilities();

	private File compilerFile;

	public static Abilities get() {
		return instance;
	}

	private Abilities() {
		compilerFile = extract("EEUtilAbilityTable.exe");
	}

	public boolean compile(String in, String out, File dir) throws IOException, InterruptedException {
		File outF = new File(out);
		if (!outF.isAbsolute() && dir != null)
			outF = new File(dir, outF.getPath());

		File inF = new File(in);
		if (!inF.isAbsolute() && dir != null)
			inF = new File(dir, inF.getPath());

		if (!inF.getName().endsWith(".txt")) {
			throw new IOException("Input file must end in .txt");
		}

		if (!outF.getName().endsWith(".nut")) {
			throw new IOException("Output file must end in .txt");
		}

		if (outF.getParentFile() != null && !outF.getParentFile().exists() && !outF.getParentFile().mkdirs())
			throw new IOException("Failed to create output directory for " + outF);

		// Do the actual work in a temporary directory
		File workdir = getTempDir();
		CompilerUtil.copyFile(inF, new File(workdir, "AbilityTable.txt"));
		List<String> args = getArgs();
		args.add(compilerFile.getAbsolutePath());
		int ret = run(workdir, args);
		if (ret == 0) {
			CompilerUtil.copyFile(new File(workdir, "AbilityTable.nut"), outF);
			outF.setLastModified(inF.lastModified());
		}
		return ret == 0;
	}

}

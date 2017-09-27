package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WAV extends AbstractTool {

	private static WAV instance = new WAV();

	public static WAV get() {
		return instance;
	}

	private WAV() {
		wine = false;
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
		args.add("oggenc");
		args.add("-Q");
		args.add(in);
		args.add("-o");
		args.add(outF.getAbsolutePath());

		int ret = run(dir, args);
		if (ret == 0) {
			outF.setLastModified(inF.lastModified());
		}
		return ret == 0;
	}

}

package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MP3 extends AbstractTool {

	private static MP3 instance = new MP3();

	public static MP3 get() {
		return instance;
	}

	private MP3() {
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
		args.add("ffmpeg");
		args.add("-v");
		args.add("0");
		args.add("-i");
		args.add(in);
		args.add("-acodec");
		args.add("libvorbis");
		args.add(outF.getAbsolutePath());

		int ret = run(dir, args);
		if (ret == 0) {
			outF.setLastModified(inF.lastModified());
		}
		return ret == 0;
	}

}

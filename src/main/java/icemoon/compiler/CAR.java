package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CAR extends AbstractTool {

	private static CAR instance = new CAR();

	private File compilerFile;

	public static CAR get() {
		return instance;
	}

	private CAR() {
		compilerFile = extract(SystemUtils.IS_OS_WINDOWS ? "car.exe" : "car");
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

		if (outF.getParentFile() != null && !outF.getParentFile().exists() && !outF.getParentFile().mkdirs()) {
			throw new IOException("Failed to create output directory for " + outF);
		}

		List<String> args = getArgs();
		args.add(compilerFile.getAbsolutePath());
		args.add("create");
		args.add(inF.getAbsolutePath());
		args.add(outF.getAbsolutePath());

		int ret = run(dir, args);
		if (ret == 0) {
			outF.setLastModified(inF.lastModified());
		}
		return ret == 0;
	}

}

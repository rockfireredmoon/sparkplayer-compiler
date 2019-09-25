package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Deprecated
public class CARDecode extends AbstractTool {

	private static CARDecode instance = new CARDecode();

	private File compilerFile;

	public static CARDecode get() {
		return instance;
	}

	private CARDecode() {
		compilerFile = extract("CARDecode.exe");
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
		args.add(in);

		int ret = run(dir, args);
		if (ret == 0) {
			String bn = inF.getName();
			int idx = bn.lastIndexOf(".");
			if (idx != -1) {
				bn = bn.substring(0, idx);
			}
			File carOut = new File(inF.getParentFile(), bn + ".car");
			if (carOut.exists() && !carOut.equals(outF)) {
				if (!carOut.renameTo(outF)) {
					CompilerUtil.copyFile(carOut, outF);
				}
			}

			outF.setLastModified(inF.lastModified());
		}
		return ret == 0;
	}

}

package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import icemoon.compiler.Fixer.Type;

public class Mesh extends AbstractTool implements Output {

	private static Mesh instance = new Mesh();

	public static Mesh get() {
		return instance;
	}

	protected Mesh() {
		wine = false;
	}

	public static String getMeshBasePath(String sourceFileName) {
		String basepath = sourceFileName;
		int idx = basepath.lastIndexOf(".");
		if (idx != -1) {
			idx = basepath.lastIndexOf(".", idx - 1);
			if (idx != -1) {
				basepath = basepath.substring(0, idx);
			}
		}
		return basepath;
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

		if (!in.endsWith(".mesh.xml") && !in.endsWith(".skeleton.xml")) {
			throw new IOException("Mesh compiler only processes files ending with .mesh.xml or .skeleton.xml");
		}
		if (!out.endsWith(".mesh") && !out.endsWith(".skeleton")) {
			throw new IOException("Mesh compiler only outputs files ending with .mesh or .skeleton");
		}

		return doMesh(dir, outF, inF);
	}

	protected boolean doMesh(File dir, File outF, File inF) throws IOException, InterruptedException {
		List<String> args = getArgs();
		args.add("OgreXMLConverter");
		args.add("-d3d");
		args.add(inF.getAbsolutePath());

		int ret = run(dir, args);
		if (ret == 0) {
			String bn = getMeshBasePath(inF.getName());
			File meshOut = new File(inF.getParentFile(), bn + (inF.getName().endsWith(".skeleton.xml") ? ".skeleton" : ".mesh"));
			if (meshOut.exists() && !meshOut.equals(outF)) {
				if (!meshOut.renameTo(outF)) {
					CompilerUtil.copyFile(meshOut, outF);
				}
			}

			Fixer fixer = new Fixer(this, Type.UNFIX);
			fixer.addFile(outF);
			fixer.run();

			outF.setLastModified(inF.lastModified());

		}
		return ret == 0;
	}

	public void error(String text) {
		error(text, null);
	}

	public void error(String text, Throwable exception) {
		System.err.println("FIXER ERR: " + text);
		if (exception != null)
			exception.printStackTrace();

	}

	public void message(String text) {
		System.out.println("FIXER: " + text);
	}

	public void refresh(File file) {
	}

	public OutputStream getErrorStream() {
		return System.err;
	}

	public OutputStream getStandardStream() {
		return System.out;
	}

}

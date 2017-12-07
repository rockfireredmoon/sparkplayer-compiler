package icemoon.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

public abstract class AbstractTool {

	protected File getTempDir() {
		File tempDir = new File(new File(System.getProperty("java.io.tmpdir")),
				"ice-" + System.getProperty("user.name"));
		if (!tempDir.exists() && !tempDir.mkdirs())
			throw new RuntimeException("Could not create temporary directory.");
		return tempDir;
	}

	protected int run(File dir, List<String> args) throws IOException, InterruptedException {
		return run(dir, args, null);
	}

	protected List<String> getArgs() {
		List<String> args = new ArrayList<String>();
		return args;
	}

	protected int run(File dir, List<String> args, StringBuilder buf) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Map<String, String> environment = pb.environment();
		populateRunEnvironment(environment);
		if (dir != null)
			pb.directory(dir);
		Process process = pb.start();
		if (buf == null) {
			CompilerUtil.copy(process.getInputStream(), System.out);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CompilerUtil.copy(process.getInputStream(), baos);
			buf.append(baos.toString("UTF-8"));
		}
		int ret = process.waitFor();
		if (ret != 0) {
			System.err.println("Command '" + args + "' failed with exit code " + ret);
		}
		return ret;
	}

	protected File extract(String tool) {
		File tempDir = getTempDir();
		File compilerFile = new File(tempDir, tool);
		try {
			FileOutputStream fos = new FileOutputStream(compilerFile);
			try {
				InputStream in = AbstractTool.class.getResourceAsStream("/" + tool);
				if (tool == null)
					throw new IOException(tool + " not found in resources.");
				try {
					CompilerUtil.copy(in, fos);
				} finally {
					in.close();
				}
			} finally {
				fos.close();
			}
			if(SystemUtils.IS_OS_UNIX)
				compilerFile.setExecutable(true);
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to extract compiler binary.", ioe);
		}
		return compilerFile;
	}

	static public void extractFolder(InputStream in, File destDir) throws ZipException, IOException {
		if (!destDir.exists() && !destDir.mkdirs())
			throw new IOException("Failed to create target to extract to");

		ProcessBuilder pb = new ProcessBuilder("tar", "xzf", "-");
		pb.directory(destDir);
		pb.redirectErrorStream(true);
		Process p = pb.start();
		try {
			final InputStream cin = p.getInputStream();
			new Thread() {
				public void run() {
					try {
						CompilerUtil.copy(cin, System.out);
					} catch (IOException ioe) {
					}
				}
			}.start();
			OutputStream out = p.getOutputStream();
			try {
				int r;
				byte[] buf = new byte[32768];

				while ((r = in.read(buf)) != -1) {
					out.write(buf, 0, r);
				}
				out.flush();
			} finally {
				out.close();
			}
		} finally {
			try {
				int ret = p.waitFor();
				if (ret != 0)
					throw new IOException("Tar returned non-zero exit " + ret);
			} catch (InterruptedException iie) {
				throw new IOException("Interruped while completing.", iie);
			}
		}
	}
	
	protected void populateRunEnvironment(Map<String, String> environment) {
	}
}

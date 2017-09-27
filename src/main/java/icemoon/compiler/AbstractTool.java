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

public class AbstractTool {

	private static final String WINE_LINUX_X86 = "wine-linux-x86-2.17.tar.gz";

	static {
		// Make a script for a persistent wine server

	}

	protected boolean wine;
	private String extractedWine;

	protected AbstractTool() {
		String msystem = System.getenv().get("MSYSTEM");
		wine = !(msystem != null && msystem.startsWith("MINGW"));

		/*
		 * We MUST use a 32-bit wine, so if WINEBIN is not set then extract this copy
		 * and use it
		 */
		if (wine && System.getenv("WINEBIN") == null) {
			File extracted = new File(System.getProperty("user.home") + File.separator + ".cache" + File.separator
					+ "tawcompiler" + File.separator + "wine");
			if (!extracted.exists()) {
				System.out.print(String.format("Extracting Wine %s to %s", WINE_LINUX_X86, extracted));
				InputStream in = AbstractTool.class.getClassLoader().getResourceAsStream(WINE_LINUX_X86);
				if (in == null)
					throw new IllegalStateException(String.format(
							"Cannot find 32-bit wine installation to extract. Either set WINEBIN to point to one (it MUST be 32-bit), or ensure the resource %s exists.",
							WINE_LINUX_X86));
				try {
					extractFolder(in, extracted);
				} catch (ZipException e) {
					throw new IllegalStateException("Failed to extract Wine.", e);
				} catch (IOException e) {
					throw new IllegalStateException("Failed to extract Wine.", e);
				}
			}
			extractedWine = extracted.getPath() + File.separator + "bin" + File.separator + "wine";
		}
	}

	protected String getWine() {
		if (!wine)
			throw new IllegalStateException();
		if (extractedWine != null)
			return extractedWine;
		String winebin = System.getenv("WINEBIN");
		return winebin == null ? "wine" : winebin;
	}

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

	protected int run(File dir, List<String> args, StringBuilder buf) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Map<String, String> environment = pb.environment();
		environment.putAll(System.getenv());
		if (wine) {
			environment.put("WINEPREFIX", new File(getTempDir(), "wine").getAbsolutePath());
		}
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
				InputStream in = Sq.class.getResourceAsStream("/" + tool);
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
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to extract compiler binary.", ioe);
		}
		return compilerFile;
	}

	protected List<String> getArgs() {
		List<String> args = new ArrayList<String>();
		if (wine) {
			args.add(getWine());
		}
		return args;
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
						int r;
						byte[] buf = new byte[32768];
						while ((r = cin.read(buf)) != -1) {
							System.out.write(buf, 0, r);
							System.out.flush();
						}
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
}

package icemoon.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractTool {

	protected File getTempDir() {
		File tempDir = new File(new File(System.getProperty("java.io.tmpdir")),
				"ice-" + System.getProperty("user.name"));
		if (!tempDir.exists() && !tempDir.mkdirs()) {
			throw new RuntimeException("Could not create temporary directory.");
		}
		return tempDir;
	}

	protected int run(File dir, List<String> args) throws IOException, InterruptedException {
		return run(dir, args, null);
	}

	protected List<String> getArgs() {
		List<String> args = new ArrayList<>();
		return args;
	}

	protected int run(File dir, List<String> args, StringBuilder buf) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Map<String, String> environment = pb.environment();
		populateRunEnvironment(environment);
		if (dir != null) {
			pb.directory(dir);
		}
		Process process = pb.start();
		if (buf == null) {
			process.getInputStream().transferTo(System.out);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			process.getInputStream().transferTo(baos);
			buf.append(baos.toString("UTF-8"));
		}
		int ret = process.waitFor();
		if (ret != 0) {
			System.err.println("Command '" + args + "' failed with exit code " + ret);
		}
		return ret;
	}

	protected File extract(String tool) {
		for(String p : System.getenv("PATH").split(File.pathSeparator)) {
			File f = new File(p, tool);
			if(f.exists()) {
				return f;
			}
		}
		throw new IllegalArgumentException("Could not find tool ' " + tool + "'. Is it installed and on your PATH");
	}

	protected void populateRunEnvironment(Map<String, String> environment) {
	}
}

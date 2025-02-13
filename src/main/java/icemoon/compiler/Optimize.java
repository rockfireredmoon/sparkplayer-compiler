package icemoon.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import icemoon.compiler.Fixer.Type;

public class Optimize extends AbstractTool {
	
	private String optimizePath;
	private String[] optimizeArgs;
	private boolean collision;
	private final Output output;
	
	public Optimize(Output output) {
		this.output = output;
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

	public boolean compile(String in, File outDir, File dir) throws IOException, InterruptedException {

		File inF = new File(in);
		if (!inF.isAbsolute() && dir != null) {
			inF = new File(dir, inF.getPath());
		}

		if (!outDir.exists() && !outDir.mkdirs()) {
			throw new IOException("Failed to create output directory for " + outDir);
		}

		if (!in.endsWith(".mesh")) {
			throw new IOException("Mesh optmizer only processes files ending with .mesh");
		}

		return doMesh(dir, outDir, inF, collision);
	}

	protected boolean doMesh(File dir, File outDir, File inF, boolean collision) throws IOException, InterruptedException {

		var fixed = false;
		if(Fixer.isNeedsFix(inF)) {
			Fixer fixer = new Fixer(output, Type.FIX);
			fixer.addFile(inF);
			fixer.run();	
			fixed = true;
		}
		
		List<String> args = getArgs();

		if(optimizePath == null)
			args.add("mesh-optimizer");
		else
			args.add(optimizePath);
		
		if(optimizeArgs != null) {
			args.addAll(Arrays.asList(optimizeArgs));
		}
		else {
			args.add(inF.getAbsolutePath());
			if(collision)
				args.add("-collision");
			args.add("-outdir=" + outDir.getAbsolutePath());
		}
		
		try {
			int ret = run(dir, args);
			if (ret == 0) {
				File outF = new File(outDir, inF.getName());
				outF.setLastModified(inF.lastModified());
			}
			return ret == 0;
		}
		finally {
		
			if(fixed) {
				Fixer fixer = new Fixer(output, Type.UNFIX);
				fixer.addFile(inF);
				fixer.run();
			}
		}
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}

	public String getOptimizePath() {
		return optimizePath;
	}

	public void setOptimizePath(String optimizePath) {
		this.optimizePath = optimizePath;
	}

	public String[] getOptimizeArgs() {
		return optimizeArgs;
	}

	public void setOptimizeArgs(String[] optimizeArgs) {
		this.optimizeArgs = optimizeArgs;
	}


}

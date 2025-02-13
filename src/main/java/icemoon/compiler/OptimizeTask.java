package icemoon.compiler;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.util.FileNameMapper;

public class OptimizeTask extends AbstractCompilerTask {

	public static final String ERROR_NO_BASE_EXISTS = "base or destdir does not exist: ";
	/** base not a directory message */
	public static final String ERROR_NOT_A_DIR = "base or destdir is not a directory:";
	/** base attribute not set message */
	public static final String ERROR_BASE_NOT_SET = "base or destdir attribute must be set!";
	

	private boolean collision;
	private boolean ignoreErrors;
	private String optimizePath;
	private String[] optimizeArgs;

	@Override
	public void execute() throws BuildException {
		try {
			Optimize opt = new Optimize(this);
			opt.setOptimizePath(optimizePath);
			opt.setOptimizeArgs(optimizeArgs);
			opt.setCollision(true);

			compileList.clear();

			final File outputDir = getOutputDir();
			if (outputDir == null) {
				throw new BuildException(ERROR_BASE_NOT_SET, getLocation());
			}
			if (!outputDir.exists()) {
				throw new BuildException(ERROR_NO_BASE_EXISTS + outputDir, getLocation());
			}
			if (!outputDir.isDirectory()) {
				throw new BuildException(ERROR_NOT_A_DIR + outputDir, getLocation());
			}

			DirectoryScanner ds = this.getDirectoryScanner(baseDir);
			String[] files = ds.getIncludedFiles();

			FileNameMapper mapper = new FileNameMapper() {

				@Override
				public void setTo(String to) {
				}

				@Override
				public void setFrom(String from) {
				}

				@Override
				public String[] mapFileName(String sourceFileName) {
					File srcFile = new File(sourceFileName);
					String basepath = Mesh.getMeshBasePath(sourceFileName);
					String ext = ".mesh";
					String cnut = outputDir == null
							? ((srcFile.getParent() == null ? "" : (srcFile.getParent() + File.separator)) + basepath
									+ ext)
							: (new File(outputDir, basepath + ext).getPath());
					return new String[] { cnut };
				}
			};

			scanDir(baseDir, files, mapper);

			for (String src : compileList) {
				try {
					if (!opt.compile(src, outputDir, baseDir) && failOnError) {
						throw new BuildException(String.format("Compile of %s failed.", src));
					}
				}
				catch(Exception e) {
					if(ignoreErrors) {
						System.out.println("Ignoring error in " + src + ". " + e.getMessage());
					}
					else
						throw e;
				}
			}
		} catch (IOException ioe) {
			throw new BuildException("Compiler error.", ioe);

		} catch (InterruptedException ie) {
			throw new BuildException("Compiler error.", ie);

		} finally {
			cleanup();
		}
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

	public boolean isIgnoreErrors() {
		return ignoreErrors;
	}

	public void setIgnoreErrors(boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}

}

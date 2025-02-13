package icemoon.compiler;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.util.FileNameMapper;

public class MP3CompilerTask extends AbstractCompilerTask {

	public static final String ERROR_NO_BASE_EXISTS = "base or destdir does not exist: ";
	/** base not a directory message */
	public static final String ERROR_NOT_A_DIR = "base or destdir is not a directory:";
	/** base attribute not set message */
	public static final String ERROR_BASE_NOT_SET = "base or destdir attribute must be set!";

	@Override
	public void execute() throws BuildException {
		try {

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
					String basepath = sourceFileName;
					String ext = ".ogg";
					int idx = basepath.lastIndexOf(".");
					if (idx != -1) {
						basepath = basepath.substring(0, idx);
					}
					String ogg = outputDir == null
							? ((srcFile.getParent() == null ? "" : (srcFile.getParent() + File.separator)) + basepath
									+ ext)
							: (new File(outputDir, basepath + ext).getPath());
					return new String[] { ogg };
				}
			};

			scanDir(baseDir, files, mapper);

			for (String src : compileList) {
				if (!MP3.get().compile(src, mapper.mapFileName(src)[0], baseDir) && failOnError) {
					throw new BuildException(String.format("Compile of %s failed.", src));
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

}

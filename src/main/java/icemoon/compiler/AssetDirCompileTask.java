package icemoon.compiler;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.util.FileNameMapper;

public class AssetDirCompileTask extends AbstractCompilerTask {
	@Override
	public void execute() throws BuildException {
//		try {
//
//			compileList.clear();
//
//			final File outputDir = getOutputDir();
//			if (outputDir == null) {
//				throw new BuildException(ERROR_BASE_NOT_SET, getLocation());
//			}
//			if (!outputDir.exists()) {
//				throw new BuildException(ERROR_NO_BASE_EXISTS + outputDir, getLocation());
//			}
//			if (!outputDir.isDirectory()) {
//				throw new BuildException(ERROR_NOT_A_DIR + outputDir, getLocation());
//			}
//
//			DirectoryScanner ds = this.getDirectoryScanner(baseDir);
//			String[] files = ds.getIncludedFiles();
//
//			FileNameMapper mapper = new FileNameMapper() {
//
//				public void setTo(String to) {
//				}
//
//				public void setFrom(String from) {
//				}
//
//				public String[] mapFileName(String sourceFileName) {
//					File srcFile = new File(sourceFileName);
//					String basepath = sourceFileName;
//					int idx = basepath.lastIndexOf(".");
//					if (idx != -1) {
//						basepath = basepath.substring(0, idx);
//					}
//					String cnut = outputDir == null
//							? ((srcFile.getParent() == null ? "" : (srcFile.getParent() + File.separator)) + basepath
//									+ ".nut")
//							: (new File(outputDir, basepath + ".nut").getPath());
//					return new String[] { cnut };
//				}
//			};
//
//			scanDir(baseDir, files, mapper);
//
//			for (String src : compileList) {
//				if (!Abilities.get().compile(src, mapper.mapFileName(src)[0], baseDir) && failOnError) {
//					throw new BuildException(String.format("Compile of %s failed.", src));
//				}
//			}
//		} catch (IOException ioe) {
//			throw new BuildException("Compiler error.", ioe);
//
//		} catch (InterruptedException ie) {
//			throw new BuildException("Compiler error.", ie);
//
//		} finally {
//			cleanup();
//		}
	}

}

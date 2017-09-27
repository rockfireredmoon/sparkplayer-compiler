package icemoon.compiler;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.SourceFileScanner;

public class AbstractCompilerTask extends MatchingTask {

	protected File baseDir;
	protected File destDir;
	protected boolean incremental = true;
	protected boolean failOnError = true;

	protected Vector<String> compileList = new Vector<String>();

	public File getOutputDir() {
		if (getDestdir() != null) {
			return getDestdir();
		}
		return getBase();
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public File getBase() {
		return baseDir;
	}

	public void setBase(File baseDir) {
		this.baseDir = baseDir;
	}

	public File getDestdir() {
		return destDir;
	}

	public void setDestdir(File destDir) {
		this.destDir = destDir;
	}

	public boolean isIncremental() {
		return incremental;
	}

	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}

	protected void cleanup() {
	}

	protected void scanDir(File baseDir, String[] files, FileNameMapper mapper) {
		String[] newFiles = files;
		if (!incremental) {
			log("no uptodate test as -always option has been specified", Project.MSG_VERBOSE);
		} else {
			SourceFileScanner sfs = new SourceFileScanner(this);
			newFiles = sfs.restrict(files, baseDir, getOutputDir(), mapper);
		}
		for (int i = 0; i < newFiles.length; i++) {
			compileList.addElement(newFiles[i]);
		}
	}
}

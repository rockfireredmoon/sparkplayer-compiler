package icemoon.compiler;

import java.io.File;
import java.io.OutputStream;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.SourceFileScanner;

public class AbstractCompilerTask extends MatchingTask implements Output {

	protected File baseDir;
	protected File destDir;
	protected boolean incremental = true;
	protected boolean failOnError = true;
	protected boolean verbose = true;

	protected Vector<String> compileList = new Vector<>();

	public File getOutputDir() {
		if (getDestdir() != null) {
			return getDestdir();
		}
		return getBase();
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
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
		for (String newFile : newFiles) {
			compileList.addElement(newFile);
		}
	}

	@Override
	public void error(String text) {
		error(text, null);
	}

	@Override
	public void error(String text, Throwable exception) {
		System.err.println("ERR: " + text);
		if (exception != null) {
			exception.printStackTrace();
		}

	}

	@Override
	public void message(String text) {
		System.out.println("MSG: " + text);
	}

	@Override
	public void refresh(File file) {
	}

	@Override
	public OutputStream getErrorStream() {
		return System.err;
	}

	@Override
	public OutputStream getStandardStream() {
		return System.out;
	}
}

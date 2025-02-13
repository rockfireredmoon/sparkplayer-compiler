package icemoon.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBatchFileProcessor implements Runnable {

	protected Output console;
	private List<File> files = new ArrayList<>();

	public AbstractBatchFileProcessor(Output console) {
		this.console = console;
	}

	public AbstractBatchFileProcessor addFile(File file) {
		files.add(file);
		return this;
	}

	@Override
	public void run() {
		for (final File file : files) {
			doFile(file);
		}
	}

	protected abstract boolean doFile(File file);
}

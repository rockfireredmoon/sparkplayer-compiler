package icemoon.compiler;

import java.io.File;
import java.io.OutputStream;

public class DumbOutput implements Output {

	@Override
	public void error(String text) {
		System.err.println("ERROR: " +text);

	}

	@Override
	public void error(String text, Throwable exception) {
		System.err.println("ERROR: " +text);
		exception.printStackTrace();
	}

	@Override
	public void message(String text) {
		System.err.println("INFO: " +text);
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

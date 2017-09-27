package icemoon.compiler;

import java.io.File;
import java.io.OutputStream;

public class DumbOutput implements Output {

	public void error(String text) {
		System.err.println("ERROR: " +text);
		
	}

	public void error(String text, Throwable exception) {
		System.err.println("ERROR: " +text);
		exception.printStackTrace();		
	}

	public void message(String text) {
		System.err.println("INFO: " +text);
	}

	public void refresh(File file) {
	}

	public OutputStream getErrorStream() {
		return System.err;
	}

	public OutputStream getStandardStream() {
		return System.out;
	}

}

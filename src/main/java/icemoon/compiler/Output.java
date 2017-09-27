package icemoon.compiler;

import java.io.File;
import java.io.OutputStream;


public interface Output {
	void error(String text);

	void error(String text, Throwable exception);

	void message(String text);

	void refresh(File file);

	OutputStream getErrorStream();

	OutputStream getStandardStream();
}

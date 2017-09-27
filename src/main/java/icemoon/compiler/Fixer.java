package icemoon.compiler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Fixer extends AbstractBatchFileProcessor {

	private final Type type;

	public enum Type {

		FIX, UNFIX
	}

	// private PropTreeModel treeModel;

	public static void main(String[] args) {
		if (args.length >= 2) {
			Type type = Type.valueOf(args[0]);
			Fixer fixer = new Fixer(new DumbOutput(), type);
			boolean ok = true;
			for (int i = 1; i < args.length; i++) {
				if (!fixer.doFile(new File(args[i]))) {
					ok = false;
				}
			}
			System.exit(ok ? 0 : 1);
		} else {
			System.err.println(Fixer.class.getName() + ": <" + Type.FIX + "|" + Type.UNFIX + "> <filename>");
			System.exit(2);
		}
	}

	public static String getSerializerVersion(File file) {
		try {
			DataInputStream fin = new DataInputStream(new FileInputStream(file));
			byte[] buf = new byte[32];
			try {
				fin.readFully(buf);
				StringBuilder bui = null;
				for (int i = 0; i < buf.length; i++) {
					if (buf[i] == '[') {
						bui = new StringBuilder();
					} else if (buf[i] == ']' && bui != null) {
						return bui.toString();
					} else if (bui != null) {
						bui.append((char) buf[i]);
					}
				}
				throw new IOException("No serializer found. Not a mesh file?");
			} finally {
				fin.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isNeedsFix(File file) {
		return getSerializerVersion(file).equals("MeshSerializer_v1.40o");
	}

	public static boolean isNeedsUnfix(File file) {
		return !getSerializerVersion(file).equals("MeshSerializer_v1.40o");
	}

	public Fixer(Output console, Type type) {
		super(console);
		this.type = type;
	}

	@Override
	protected boolean doFile(final File file) {
		if ((type.equals(Type.FIX) && !isNeedsFix(file)) || (type.equals(Type.UNFIX) && !isNeedsUnfix(file))) {
			console.message("Skipping " + file);
			return false;
		}

		File tf = null;
		try {
			console.message(type + "ING serializer version in " + file.getName());
			tf = File.createTempFile("mesh", "conv");
			FileOutputStream fos = new FileOutputStream(tf);
			try {
				DataInputStream din = new DataInputStream(new FileInputStream(file));
				int iidx = -1;
				try {
					while (true) {
						byte b = din.readByte();
						if (iidx == -1 && (char) b == '[') {
							// Start of serializer
							iidx = 0;
						} else if (iidx == 0 && (char) b == ']') {
							// End of serializer
							fos.write((type.equals(Type.FIX) ? "[MeshSerializer_v1.40]" : "[MeshSerializer_v1.40o]").getBytes());
							iidx = 1;
							CompilerUtil.copy(din, fos);
							break;
						} else if (iidx != 0) {
							fos.write(b);
						}
					}

				} finally {
					din.close();
				}
			} finally {
				fos.close();
			}
			CompilerUtil.copyFile(tf, file);
			console.message(type + "ED serializer version in " + file);
			console.refresh(file);
			return true;

		} catch (IOException ioe) {
			ioe.printStackTrace();
			console.message("Failed to fix. " + ioe.getMessage());
			throw new RuntimeException("Failed to fix serializer version.", ioe);
		} finally {
			if (tf != null) {
				tf.delete();
			}
		}
	}
}

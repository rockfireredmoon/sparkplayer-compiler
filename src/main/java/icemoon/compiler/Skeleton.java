package icemoon.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Skeleton extends Mesh {

	private boolean deleteTemp = true;


	public Skeleton(Output output) {
		super(output);
	}

	public boolean isDeleteTemp() {
		return deleteTemp;
	}

	public void setDeleteTemp(boolean deleteTemp) {
		this.deleteTemp = deleteTemp;
	}

	@Override
	protected boolean doMesh(File dir, File outF, File inF) throws IOException, InterruptedException {
		File inDir = inF.getParentFile();
		File tmpFile = new File(inDir, "tmp." + inF.getName());
		if (deleteTemp) {
			tmpFile.deleteOnExit();
		}

		/*
		 * If there is a .skeletonlist file in this directory, see if the skel
		 * we are decompiling is in it
		 */

		String animPrefix = CompilerUtil.basename(inF.getName(), ".skeleton.xml");
		String based = CompilerUtil.basename(inF.getName(), ".xml");

		if (inDir.exists()) {
			for (File f : inDir.listFiles()) {
				if (f.getName().endsWith(".skeletonlist")) {
					if (CompilerUtil.fileContains(based, f)) {
						animPrefix = CompilerUtil.basename(f.getName(), ".skeletonlist");
					}
				}
			}
		}

		List<File> animFiles = new ArrayList<>();
		for (File f : inDir.listFiles()) {
			if (f.getName().startsWith(animPrefix) && f.getName().endsWith(".anim")) {
				animFiles.add(f);
			}
		}

		if (!animFiles.isEmpty()) {
			try {
				AnimToXML aaToXml = new AnimToXML(inF);
				OutputStream out = new FileOutputStream(tmpFile);
				try {
					for (File f : animFiles) {
						try {
							aaToXml.process(new FileInputStream(f));
						} catch (IOException ioe) {
							throw new IOException(f + " failed.", ioe);
						}
					}
					aaToXml.output(out);
				} finally {
					out.close();
				}
				tmpFile.setLastModified(inF.lastModified());
				try {
					return super.doMesh(dir, outF, tmpFile);
				} finally {
					if (deleteTemp) {
						tmpFile.delete();
					}
				}
			} catch (SAXException se) {
				throw new IOException(se);
			} catch (ParserConfigurationException e) {
				throw new IOException(e);
			}
		} else {
			return super.doMesh(dir, outF, inF);
		}
	}

}

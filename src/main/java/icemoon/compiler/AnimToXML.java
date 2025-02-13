package icemoon.compiler;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

// Source of serializer
//
// http://gamekit.googlecode.com/svn/trunk/Ogre-1.9a/OgreMain/src/OgreSkeletonSerializer.cpp
//
// Some constants
//
// http://ogrenightly.free.fr/docs/OgreSkeletonFileFormat_8h_source.html
//
// https://github.com/ehsan/ogre/blob/master/OgreMain/src/OgreSerializer.cpp

public class AnimToXML {

	public static class TransformKeyFrame {
		float time;
		float[] rot;
		float[] trans;
		float[] scale;

		TransformKeyFrame(float time) {
			this.time = time;
		}
	}

	public static class NodeAnimationTrack {
		private short boneHandle;
		private List<TransformKeyFrame> frames = new ArrayList<>();

		NodeAnimationTrack(short boneHandle) {
			this.boneHandle = boneHandle;
		}

		public TransformKeyFrame createNodeKeyFrame(float time) {
			TransformKeyFrame transformKeyFrame = new TransformKeyFrame(time);
			frames.add(transformKeyFrame);
			return transformKeyFrame;
		}
	}

	public final static int SKELETON_ANIMATION_BASEINFO = 0x4010;
	public final static int SKELETON_ANIMATION = 0x4000;
	public final static int SKELETON_ANIMATION_TRACK = 0x4100;
	public final static int SKELETON_ANIMATION_TRACK_KEYFRAME = 0x4110;

	public static class Animation {
		String name;
		float len;
		float baseKeyTime;
		String baseAnimName;
		boolean something;
		List<NodeAnimationTrack> tracks = new ArrayList<>();

		Animation(String name, float len) {
			this.name = name;
			this.len = len;
		}

		public void setUseBaseKeyFrame(boolean something, float baseKeyTime, String baseAnimName) {
			this.something = something;
			this.baseKeyTime = baseKeyTime;
			this.baseAnimName = baseAnimName;
		}

		public NodeAnimationTrack createTrack(short boneHandle) {
			NodeAnimationTrack nodeAnimationTrack = new NodeAnimationTrack(boneHandle);
			tracks.add(nodeAnimationTrack);
			return nodeAnimationTrack;
		}
	}

	public static class Skeleton {
		private List<Animation> animations = new ArrayList<>();

		public Animation createAnimation(String name, float len) {
			Animation animation = new Animation(name, len);
			animations.add(animation);
			return animation;
		}
	}

	public static class Chunk {
		short id;
		int len;
		DataInput din;

		Chunk(DataInput din) throws IOException {
			this.din = din;
			read();
		}

		void read() throws IOException {
			id = din.readShort();
			len = din.readInt();
		}
	}

	public static int HEADER_STREAM_ID_EXT = 0x1000;
	private SkelHandler skelHandler;
	private Skeleton skel;
	private File skelIn;

	public AnimToXML(File skelIn) throws ParserConfigurationException, SAXException, IOException {
		// Create a JAXP SAXParserFactory and configure it
		this.skelIn = skelIn;
		skel = new Skeleton();

		SAXParserFactory spf = SAXParserFactory.newInstance();

		// Set namespaceAware to true to get a parser that corresponds to
		// the default SAX2 namespace feature setting. This is necessary
		// because the default value from JAXP 1.0 was defined to be false.
		spf.setNamespaceAware(true);

		// Validation part 1: set whether validation is on
		spf.setValidating(false);

		// Create a JAXP SAXParser
		SAXParser saxParser = spf.newSAXParser();

		// Get the encapsulated SAX XMLReader
		XMLReader xmlReader = saxParser.getXMLReader();

		// Set the ContentHandler of the XMLReader
		xmlReader.setContentHandler(skelHandler = new SkelHandler());

		// // Set an ErrorHandler before parsing
		// ErrorHandler eh;
		// xmlReader.setErrorHandler(new HandlerBase() {
		// });

		// Tell the XMLReader to parse the XML document
		FileReader fr = new FileReader(skelIn);
		try {
			InputSource is = new InputSource(fr);
			xmlReader.parse(is);
		} finally {
			fr.close();
		}
	}

	public void process(InputStream in) throws IOException {
		LittleEndianDataInputStream din = new LittleEndianDataInputStream(in);

		try {
			// Outter ofusion version
			String ofusionVer = readFileHeader(din);
			if (!ofusionVer.equals("[oFusion_Serializer_v1.0o]") && !ofusionVer.equals("[oFusion_Serializer_v1.0]")) {
				throw new IOException("Not an oFusion .anim file (" + ofusionVer + ")");
			}

			short noBones = din.readShort();
			System.out.println("Number of bones: " + noBones);
			System.out.println("OFusion:" + ofusionVer);

			// Inner ogre version
			// IOUtils.copy(din, out);

			String ogreVer = readFileHeader(din);
			if (!ogreVer.equals("[Serializer_v1.10]") && !ogreVer.equals("[Serializer_v1.80]")) {
				throw new IOException("Not an OGRE serialized file");
			}
			System.out.println("OGRE:" + ogreVer);

			Chunk ct = new Chunk(din);
			while (true) {
				// A chunk
				switch (ct.id) {
				case SKELETON_ANIMATION:
					// Skeleton animation
					readAnimation(din, ct, skel);
					break;
				default:
					throw new IOException("Unknown chunk type " + ct.id);
				}
			}
			//
		} catch (EOFException eof) {
		} finally {
			din.close();
		}
	}

	public void output(OutputStream out) throws IOException {

		PrintWriter pw = new PrintWriter(out, true);
		try {

			BufferedReader r = new BufferedReader(new FileReader(skelIn));
			try {
				String line;
				boolean inAnim = false;
				while ((line = r.readLine()) != null) {
					String tline = line.trim();
					if (tline.equals("<animations>")) {
						inAnim = true;
					} else if (tline.equalsIgnoreCase("</animations>")) {
						inAnim = false;
					} else if (tline.replace(" ", "").equalsIgnoreCase("<animations/>")) {
					} else if (tline.equals("</skeleton>")) {
						pw.println("    <animations>");
						for (Animation a : skel.animations) {
							pw.println("        <animation name=\"" + a.name + "\" length=\"" + a.len + "\">");
							pw.println("            <tracks>");
							for (NodeAnimationTrack t : a.tracks) {
								pw.println("                <track bone=\"" + skelHandler.bones.get((int) t.boneHandle) + "\">");
								pw.println("                    <keyframes>");
								for (TransformKeyFrame f : t.frames) {
									pw.println("                        <keyframe time=\"" + f.time + "\">");
									pw.println("                            <translate x=\"" + f.trans[0] + "\" y=\"" + f.trans[1]
											+ "\" z=\"" + f.trans[2] + "\"/>");

									Quaternion q = new Quaternion(f.rot[0], f.rot[1], f.rot[2], f.rot[3]);
									Vector3f axis = new Vector3f();
									float ang = q.toAngleAxis(axis);
									pw.println("                            <rotate angle=\"" + ang + "\">");
									pw.println("                                <axis x=\"" + axis.x + "\" y=\"" + axis.y
											+ "\" z=\"" + axis.z + "\"/>");
									pw.println("                            </rotate>");
									if (f.scale != null) {
										pw.println("                            <scale x=\"" + f.scale[0] + "\" y=\"" + f.scale[1]
												+ "\" z=\"" + f.scale[2] + "\"/>");
									}
									pw.println("                        </keyframe>");
								}
								pw.println("                    </keyframes>");
								pw.println("                </track>");

							}
							pw.println("\t\t\t</tracks>");
							pw.println("\t\t</animation>");
						}
						pw.println("\t</animations>");
						pw.println(line);
					} else {
						if (!inAnim) {
							pw.println(line);
						}
					}
				}
			} finally {
				r.close();
			}
		} finally {
			pw.close();
		}

	}

	private void readAnimation(DataInput din, Chunk ct, Skeleton skel) throws IOException {
		String name = readString(din);
		float animLength = din.readFloat();

		Animation anim = skel.createAnimation(name, animLength);

		ct.read();
		switch (ct.id) {
		case SKELETON_ANIMATION_BASEINFO:
			String baseAnimName = readString(din);
			float baseKeyTime = din.readFloat();
			ct.read();
			anim.setUseBaseKeyFrame(true, baseKeyTime, baseAnimName);
			break;
		}

		while (ct.id == SKELETON_ANIMATION_TRACK) {
			readAnimationTrack(ct, din, anim, skel);
			// ct.read();
		}
	}

	private void readAnimationTrack(Chunk ct, DataInput din, Animation anim, Skeleton skel) throws IOException {
		short boneHandle = din.readShort();
		NodeAnimationTrack track = anim.createTrack(boneHandle);
		ct.read();
		while (ct.id == SKELETON_ANIMATION_TRACK_KEYFRAME) {
			readKeyFrame(ct, din, track, skel);
			// ct.read();
		}

	}

	private void readKeyFrame(Chunk ct, DataInput din, NodeAnimationTrack track, Skeleton skel) throws IOException {
		float time = din.readFloat();

		float[] rot = new float[4];
		rot[0] = din.readFloat();
		rot[1] = din.readFloat();
		rot[2] = din.readFloat();
		rot[3] = din.readFloat();

		float[] trans = new float[3];
		trans[0] = din.readFloat();
		trans[1] = din.readFloat();
		trans[2] = din.readFloat();

		float[] scale = null;
		if (ct.len > 38) {
			scale = new float[3];
			scale[0] = din.readFloat();
			scale[1] = din.readFloat();
			scale[2] = din.readFloat();
		}

		TransformKeyFrame kf = track.createNodeKeyFrame(time);
		kf.rot = rot;
		kf.trans = trans;
		kf.scale = scale;

		ct.read();
	}

	private String readFileHeader(DataInput din) throws IOException {
		short headerID = din.readShort();
		if (headerID != HEADER_STREAM_ID_EXT) {
			throw new IOException("Not a serialized OGRE file (" + headerID + ")");
		}
		String ver = readString(din);
		return ver;
	}

	public static String readString(DataInput din) throws IOException {
		StringBuilder bui = new StringBuilder();
		byte c;
		while ((c = din.readByte()) != 0x0a) {
			bui.append((char) c);
		}
		return bui.toString();
	}

	public final static void main(String[] args) throws Exception {
		AnimToXML aa = new AnimToXML(new File(args[0]));
		OutputStream out = new FileOutputStream(new File(args[1]));
		for (int i = 2; i < args.length; i++) {

			aa.process(new FileInputStream(new File(args[i])));
		}
		aa.output(out);
	}

	class SkelHandler extends DefaultHandler {
		private Map<Integer, String> bones = new HashMap<>();

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			String key = localName;
			if (key.equalsIgnoreCase("bone")) {
				bones.put(Integer.parseInt(atts.getValue("id")), atts.getValue("name"));
			}
		}

		@Override
		public void endDocument() throws SAXException {
		}
	}
}

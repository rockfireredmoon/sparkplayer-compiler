package icemoon.compiler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

public class Base extends AbstractTool {

	private static Base instance = new Base();

	private Map<String, BufferedImage> cache = new HashMap<>();

	public static Base get() {
		return instance;
	}

	private Base() {
	}

	public boolean compile(String in, String out, File dir) throws IOException, InterruptedException {
		File outF = new File(out);
		if (!outF.isAbsolute() && dir != null) {
			outF = new File(dir, outF.getPath());
		}

		File inF = new File(in);
		if (!inF.isAbsolute() && dir != null) {
			inF = new File(dir, inF.getPath());
		}

		if (outF.getParentFile() != null && !outF.getParentFile().exists() && !outF.getParentFile().mkdirs()) {
			throw new IOException("Failed to create output directory for " + outF);
		}

		System.out.println(String.format("Compiling %s to %s", inF, outF));

		BufferedImage bim = ImageIO.read(inF);
		BufferedImage bout = new BufferedImage(bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_ARGB);

		/*
		 * Look for the .cfg file in the same folder and load it to get the terrain
		 * splatting images to actually use.
		 */
		List<String> parts = Arrays.asList(inF.getName().split("_"));
		int idx = parts.indexOf("Coverage");
		String lastPath = parts.get(parts.size() - 1);
		int pidx = lastPath.lastIndexOf(".");
		if (pidx != -1) {
			lastPath = lastPath.substring(0, pidx);
		}
		String templateName = String.join("_", parts.subList(0, idx));
		String basepath = templateName + "_" + lastPath + ".cfg";
		File cfgFile = new File(inF.getParentFile(), basepath);
		System.out.print(String.format("Configuration file is %s", cfgFile));
		if (!cfgFile.exists()) {
			cfgFile = new File(new File(cfgFile.getParentFile().getParentFile(), "Terrain-" + templateName),
					"Terrain-" + templateName + ".cfg");
			System.out.println(String.format("No tile specific configuration file, trying %s", cfgFile));
		}
		Properties splatProps = new Properties();
		FileInputStream fin = new FileInputStream(cfgFile);
		try {
			splatProps.load(fin);
		} finally {
			fin.close();
		}

		BufferedImage[] components = new BufferedImage[4];
		idx = 0;
		for (String p : new String[] { "Texture.Splatting0", "Texture.Splatting1", "Texture.Splatting2",
				"Texture.Splatting3" }) {
			String v = splatProps.getProperty(p);
			if (v != null && v.length() > 0) {
				components[idx] = getImage(v, new File(inF.getParentFile().getParentFile(), "Terrain-Common"), bim.getWidth(), bim.getHeight());
			} else {
				System.err.println(String.format("No splat texture for %s", p));
			}
			idx++;
		}

		Graphics2D g2 = (Graphics2D) bout.getGraphics();

		for (int y = 0; y < bim.getHeight(); y++) {
			for (int x = 0; x < bim.getWidth(); x++) {
				/*
				 * Color in coverage. We use R, G, B and A as the alpha value for each of the
				 * splat components
				 */
				Color c = new Color(bim.getRGB(x, y));

				drawComponent(components, g2, x, y, c.getRed(), 0);
				drawComponent(components, g2, x, y, c.getGreen(), 1);
				drawComponent(components, g2, x, y, c.getBlue(), 2);
				drawComponent(components, g2, x, y, c.getAlpha(), 3);
			}
		}

		ImageIO.write(bout, "JPEG", outF);
		outF.setLastModified(inF.lastModified());
		return true;
	}

	private void drawComponent(BufferedImage[] components, Graphics2D g2, int x, int y, int alpha, int idx) {
		/* Color in splat image **/
		if (components[idx] != null) {


			Color sc = new Color(components[idx].getRGB(x, y));

			/* Final color */
			Color fc = new Color(sc.getRed(), sc.getGreen(), sc.getBlue(), alpha);

			g2.setColor(fc);
			g2.drawLine(x, y, x, y);
		}
	}

	BufferedImage getImage(String name, File dir, int w, int h) throws IOException {
		BufferedImage img = cache.get(name + "_" + w + "_" + h);
		if (img == null) {
			File f = new File(dir, name);
			if (!f.exists()) {
				throw new FileNotFoundException(String.format("No splat texture %s", f));
			}
			img = ImageIO.read(f);
			if(img.getWidth() != w || img.getHeight() != h) {
				Image img2 = img.getScaledInstance(w, h, Image.SCALE_FAST);
				img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				img.getGraphics().drawImage(img2, 0, 0, null);
			}
			cache.put(name, img);
		}
		return img;
	}
}

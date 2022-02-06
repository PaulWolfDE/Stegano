package de.paulwolf.stegano.core;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageUtility {

	static BufferedImage imageToBufferedImage(Image image) {

		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(image, 0, 0, null);
		bg.dispose();
		return bi;
	}

	static int getA(int pixel) {
		return (pixel & 0xff000000) >>> 24;
	}

	static int getR(int pixel) {
		return (pixel & 0xff0000) >> 16;
	}

	static int getG(int pixel) {
		return (pixel & 0xff00) >> 8;
	}

	static int getB(int pixel) {
		return (pixel) & 0xff;
	}

	static int getARGB(int a, int r, int g, int b) {
		int ret = a << 24;
		ret += r << 16;
		ret += g << 8;
		ret += b;
		return ret;
	}

	static int manipulateBit(int p, int b) {
		if (b == 0)
			return p & 0b11111110;
		if (b == 1)
			return p | 0b1;

		throw new IllegalArgumentException("b must be 1 or 0!");
	}
}

package de.paulwolf.stegano.core;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import static de.paulwolf.stegano.core.RecoverMessage.binStringToByte;
import static de.paulwolf.stegano.core.RecoverMessage.byteListToArray;

public class ImageUtility {

	/*
	* DEBUG
	*/
	static void echoBits(BufferedImage img, int start, int finish) {

		StringBuilder buffer = new StringBuilder();

		for (int i = start; i < finish; i++) {
			int argb = img.getRGB(i % img.getWidth(), i / img.getHeight());
			buffer.append(getLeastSignificantBit(getR(argb)));
			buffer.append(getLeastSignificantBit(getG(argb)));
			buffer.append(getLeastSignificantBit(getB(argb)));
		}
		String[] bitCharacter = buffer.toString().split("(?<=\\G.{8})");
		ArrayList<Byte> message = new ArrayList<>();

		for (int i = 0; i < (finish-start)*3/8; i++)
			message.add(binStringToByte(bitCharacter[i]));

		System.out.println("huhu " + Arrays.toString(byteListToArray(message)));
	}

	private static int getLeastSignificantBit(int in) {
		return (in & 0b1);
	}

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

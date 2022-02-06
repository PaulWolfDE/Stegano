package de.paulwolf.stegano.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HideMessage {

	public static File hideMessage(File source, File dest, byte[] message) throws IOException {

		BufferedImage s = ImageUtility.imageToBufferedImage(ImageIO.read(source));
		BufferedImage d = new BufferedImage(s.getWidth(), s.getHeight(), BufferedImage.TYPE_INT_ARGB);

		if (message.length > (s.getWidth() * s.getHeight() * 3 - 24) / 8) {
			throw new IllegalArgumentException("Message length too long!");
		}

		d = copyImage(s, d);
		d = writeLength(s, d, message.length);
		d = hideMessage(d, message);
		
		ImageIO.write(d, "png", dest);

		return dest;
	}
	
	private static BufferedImage copyImage(BufferedImage img, BufferedImage dest) {

		for (int i = 0; i < img.getWidth(); i++)
			for (int j = 0; j < img.getHeight(); j++)
				dest.setRGB(i, j, img.getRGB(i, j));
		return dest;
	}

	private static BufferedImage hideMessage(BufferedImage img, byte[] message) {

		String binMessage = bytesToBinaryString(message);
		StringBuilder buffer = new StringBuilder();
		buffer.append(binMessage);
		for (int i = 0; i < 3 - binMessage.length() % 3; i++)
			buffer.append('0');

		for (int i = 0; i < buffer.length() / 3; i++) {

			int argb = img.getRGB((i + 24) % img.getWidth(), (int) (i + 24) / img.getWidth());
			int a =ImageUtility.getA(argb);
			int r = ImageUtility.getR(argb);
			int g = ImageUtility.getG(argb);
			int b = ImageUtility.getB(argb);
			r = ImageUtility.manipulateBit(r, (int) (buffer.charAt(i * 3) - '0'));
			g = ImageUtility.manipulateBit(g, (int) (buffer.charAt(i * 3 + 1) - '0'));
			b = ImageUtility.manipulateBit(b, (int) (buffer.charAt(i * 3 + 2) - '0'));
			argb = ImageUtility.getARGB(a, r, g, b);
				argb = ImageUtility.getARGB(a, r, g, b);
			img.setRGB((i + 24) % img.getWidth(), (int) (i + 24) / img.getWidth(), argb);
		}
		return img;
	}

	private static BufferedImage writeLength(BufferedImage img, BufferedImage dest, int length) {

		String binLength = Integer.toBinaryString(length);
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < 3 - binLength.length() % 3; i++)
			buffer.append('0');
		buffer.append(binLength);
		for (int i = 0; i < 24; i++) {
			int argb = img.getRGB(i % img.getWidth(), (int) i / img.getWidth());
			int a =ImageUtility.getA(argb);
			int r = ImageUtility.getR(argb);
			int g = ImageUtility.getG(argb);
			int b = ImageUtility.getB(argb);
			r = ImageUtility.manipulateBit(r, 0);
			g = ImageUtility.manipulateBit(g, 0);
			b = ImageUtility.manipulateBit(b, 0);
			if (24 - i <= buffer.length() / 3) {
				r = ImageUtility.manipulateBit(r,
						Integer.parseInt(String.valueOf((buffer.charAt((buffer.length() / 3 - 24 + i) * 3)))));
				g = ImageUtility.manipulateBit(g,
						Integer.parseInt(String.valueOf((buffer.charAt((buffer.length() / 3 - 24 + i) * 3 + 1)))));
				b = ImageUtility.manipulateBit(b,
						Integer.parseInt(String.valueOf((buffer.charAt((buffer.length() / 3 - 24 + i) * 3 + 2)))));
			}
				argb = ImageUtility.getARGB(a, r, g, b);
			dest.setRGB(i % img.getWidth(), (int) i / img.getWidth(), argb);
		}
		return dest;
	}

	private static String bytesToBinaryString(byte[] in) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < in.length; i++)
			sb.append(intToBinByte((int) in[i]));

		return sb.toString();
	}

	private static String intToBinByte(int in) {

		StringBuilder sb = new StringBuilder();
		sb.append("00000000");
		sb.append(Integer.toBinaryString(in));
		return sb.substring(sb.length() - 8);
	}
}

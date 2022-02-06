package de.paulwolf.stegano.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class RecoverMessage {

	public static byte[] recoverMessage(File imgFile) throws IOException {

		BufferedImage img = ImageUtility.imageToBufferedImage(ImageIO.read(imgFile));
		
		int messageLength = getMessageLength(img);
		byte[] message = extractMessage(img, messageLength);

		return message;
	}

	private static byte[] extractMessage(BufferedImage img, int messageLength) {

		StringBuilder messageBits = new StringBuilder();

		for (int i = 0; i < messageLength * 8 / 3 + 1; i++) {

			int argb = img.getRGB((i + 24) % img.getWidth(), (int) (i + 24) / img.getWidth());
			messageBits.append(getLeastSignificantBit(ImageUtility.getR(argb)));
			messageBits.append(getLeastSignificantBit(ImageUtility.getG(argb)));
			messageBits.append(getLeastSignificantBit(ImageUtility.getB(argb)));
		}

		String[] bitCharacter = messageBits.toString().split("(?<=\\G.{8})");
		ArrayList<Byte> message = new ArrayList<>();
 
		for (int i = 0; i < messageLength; i++)
			message.add(binStringToByte(bitCharacter[i]));

		return byteListToArray(message);
	}

	private static int getMessageLength(BufferedImage img) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 24; i++) {
			int argb = img.getRGB(i % img.getWidth(), (int) i / img.getWidth());
			sb.append(getLeastSignificantBit(ImageUtility.getR(argb)));
			sb.append(getLeastSignificantBit(ImageUtility.getG(argb)));
			sb.append(getLeastSignificantBit(ImageUtility.getB(argb)));
		}
		return Integer.parseInt(sb.toString(), 2);
	}
	
	private static int getLeastSignificantBit(int in) {
		return (in & 0b1);
	}
	
	private static byte[] byteListToArray(ArrayList<Byte> list) {
		byte[] ret = new byte[list.size()];
		for (int i = 0; i < list.size(); i++)
			ret[i] = list.get(i);
		return ret;
	}
	private static byte binStringToByte(String in) {
	    byte ret = Byte.parseByte(in.substring(1), 2);
	    ret -= (in.charAt(0) - '0') * 128;
	    return ret;
	}
}

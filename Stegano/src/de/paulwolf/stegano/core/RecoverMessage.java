package de.paulwolf.stegano.core;

import de.paulwolf.stegano.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RecoverMessage {

    public static byte[] recoverInitializationVector(File imgFile) throws IOException {

        BufferedImage img = ImageUtility.imageToBufferedImage(ImageIO.read(imgFile));
        int messageLength = getMessageLength(img);
        return extractInitializationVector(img, messageLength);
    }

    public static byte[] extractInitializationVector(BufferedImage img, int messageLength) {

        int start = 24 + messageLength * 8 / 3 + 1, finish = start + 42 + 1;

        StringBuilder buffer = new StringBuilder();

        for (int i = start; i < finish; i++) {
            int argb = img.getRGB(i % img.getWidth(), i / img.getHeight());
            buffer.append(getLeastSignificantBit(ImageUtility.getR(argb)));
            buffer.append(getLeastSignificantBit(ImageUtility.getG(argb)));
            buffer.append(getLeastSignificantBit(ImageUtility.getB(argb)));
        }
        String[] bitCharacter = buffer.toString().split("(?<=\\G.{8})");
        ArrayList<Byte> message = new ArrayList<>();

        for (int i = 0; i < (finish - start) * 3 / 8; i++)
            message.add(binStringToByte(bitCharacter[i]));

        if (Main.DEBUG) {
            System.out.println(Arrays.toString(byteListToArray(message)));
            ImageUtility.echoBits(img, start, finish);
        }
        return byteListToArray(message);
    }

    public static byte[] recoverMessage(File imgFile) throws IOException {

        BufferedImage img = ImageUtility.imageToBufferedImage(ImageIO.read(imgFile));
        int messageLength = getMessageLength(img);
        return extractMessage(img, messageLength);
    }

    private static byte[] extractMessage(BufferedImage img, int messageLength) {

        StringBuilder messageBits = new StringBuilder();

        for (int i = 0; i < messageLength * 8 / 3 + 1; i++) {
            int argb = img.getRGB((i + 24) % img.getWidth(), (i + 24) / img.getWidth());
            if (i * 3 < messageLength * 8)
                messageBits.append(getLeastSignificantBit(ImageUtility.getR(argb)));
            if (i * 3 + 1 < messageLength * 8)
                messageBits.append(getLeastSignificantBit(ImageUtility.getG(argb)));
            if (i * 3 + 2 < messageLength * 8)
                messageBits.append(getLeastSignificantBit(ImageUtility.getB(argb)));
        }

        String[] bitCharacter = messageBits.toString().split("(?<=\\G.{8})");
        if (Main.DEBUG) {
            System.out.println(Arrays.toString(bitCharacter));
            ImageUtility.echoBits(img, 24, 24 + messageBits.length() / 3 + 1);
        }
        ArrayList<Byte> message = new ArrayList<>();

        for (int i = 0; i < messageLength; i++)
            message.add(binStringToByte(bitCharacter[i]));

        return byteListToArray(message);
    }

    private static int getMessageLength(BufferedImage img) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            int argb = img.getRGB(i % img.getWidth(), i / img.getWidth());
            sb.append(getLeastSignificantBit(ImageUtility.getR(argb)));
            sb.append(getLeastSignificantBit(ImageUtility.getG(argb)));
            sb.append(getLeastSignificantBit(ImageUtility.getB(argb)));
        }
        return Integer.parseInt(sb.toString(), 2);
    }

    private static int getLeastSignificantBit(int in) {
        return (in & 0b1);
    }

    static byte[] byteListToArray(ArrayList<Byte> list) {
        byte[] ret = new byte[list.size()];
        for (int i = 0; i < list.size(); i++)
            ret[i] = list.get(i);
        return ret;
    }

    static byte binStringToByte(String in) {
        byte ret = Byte.parseByte(in.substring(1), 2);
        ret -= (in.charAt(0) - '0') * 128;
        return ret;
    }
}

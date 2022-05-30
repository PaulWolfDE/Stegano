package de.paulwolf.stegano.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class HideMessage {

    public static void hideMessage(File source, File dest, byte[] message, byte[] iv) throws IOException {

        BufferedImage img = ImageUtility.imageToBufferedImage(ImageIO.read(source));

        if (message.length > ((img.getWidth() * img.getHeight() * 3 /* Max bits */ - 129 /* IV */ - 72 /* Length */) / 8 /* Bits to bytes */))
            throw new IllegalArgumentException("Message length too long!");

        writeLength(img, message.length);
        assert message.length == RecoverMessage.getMessageLength(img) : "Program wrote or read wrong length!";
        hideMessage(img, message);
        assert Arrays.equals(message, RecoverMessage.extractMessage(img, message.length)) : "Program wrote or read wrong message!";
        hideInitializationVector(img, iv, message.length);
        assert Arrays.equals(iv, RecoverMessage.extractInitializationVector(img, message.length)) : "Program wrote or read wrong IV!";

        ImageIO.write(img, "png", dest);
    }

    private static void hideMessage(BufferedImage img, byte[] message) {

        String binMessage = bytesToBinaryString(message);

        for (int i = 0; i < binMessage.length() / 3 + 1; i++) {

            int argb = img.getRGB((i + 24) % img.getWidth(), (i + 24) / img.getWidth());
            int a = ImageUtility.getA(argb);
            int r = ImageUtility.getR(argb);
            int g = ImageUtility.getG(argb);
            int b = ImageUtility.getB(argb);
            if (i * 3 < binMessage.length())
                r = ImageUtility.manipulateBit(r, binMessage.charAt(i * 3) - '0');
            if (i * 3 + 1 < binMessage.length())
                g = ImageUtility.manipulateBit(g, binMessage.charAt(i * 3 + 1) - '0');
            if (i * 3 + 2 < binMessage.length())
                b = ImageUtility.manipulateBit(b, binMessage.charAt(i * 3 + 2) - '0');
            argb = ImageUtility.getARGB(a, r, g, b);
            img.setRGB((i + 24) % img.getWidth(), (i + 24) / img.getWidth(), argb);
        }
    }

    private static void hideInitializationVector(BufferedImage img, byte[] iv, int messageLength) {

        int start = 24 + messageLength * 8 / 3 + 1, finish = start + 42 + 1;

        String binIV = bytesToBinaryString(iv);

        StringBuilder buffer = new StringBuilder();
        buffer.append(binIV);
        for (int i = 0; i < 3 - binIV.length() % 3; i++)
            buffer.append('0');

        for (int i = start; i < finish; i++) {
            int argb = img.getRGB(i % img.getWidth(), i / img.getWidth());
            int a = ImageUtility.getA(argb);
            int r = ImageUtility.getR(argb);
            int g = ImageUtility.getG(argb);
            int b = ImageUtility.getB(argb);
            r = ImageUtility.manipulateBit(r, buffer.charAt((i - start) * 3) - '0');
            g = ImageUtility.manipulateBit(g, buffer.charAt((i - start) * 3 + 1) - '0');
            b = ImageUtility.manipulateBit(b, buffer.charAt((i - start) * 3 + 2) - '0');
            argb = ImageUtility.getARGB(a, r, g, b);
            img.setRGB(i % img.getWidth(), i / img.getWidth(), argb);
        }
    }

    private static void writeLength(BufferedImage img, int length) {

        String binLength = Integer.toBinaryString(length);
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < 3 - binLength.length() % 3; i++)
            buffer.append('0');
        buffer.append(binLength);
        for (int i = 0; i < 24; i++) {
            int argb = img.getRGB(i % img.getWidth(), i / img.getWidth());
            int a = ImageUtility.getA(argb);
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
            img.setRGB(i % img.getWidth(), i / img.getWidth(), argb);
        }
    }

    private static String bytesToBinaryString(byte[] in) {

        StringBuilder sb = new StringBuilder();
        for (byte b : in) sb.append(intToBinByte(b));
        return sb.toString();
    }

    private static String intToBinByte(int in) {

        StringBuilder sb = new StringBuilder();
        sb.append("00000000");
        sb.append(Integer.toBinaryString(in));
        return sb.substring(sb.length() - 8);
    }
}

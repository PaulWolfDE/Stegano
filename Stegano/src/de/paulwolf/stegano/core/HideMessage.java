package de.paulwolf.stegano.core;

import de.paulwolf.stegano.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class HideMessage {

    public static void hideMessage(File source, File dest, byte[] message, byte[] iv) throws IOException {

        if (Main.DEBUG)
            System.out.println("Iv:" + Arrays.toString(iv));

        BufferedImage s = ImageUtility.imageToBufferedImage(ImageIO.read(source));
        BufferedImage d = new BufferedImage(s.getWidth(), s.getHeight(), BufferedImage.TYPE_INT_ARGB);

        if (message.length > (s.getWidth() * s.getHeight() * 3 - 24) / 8) {
            throw new IllegalArgumentException("Message length too long!");
        }

        copyImage(s, d);
        writeLength(s, d, message.length);
        hideMessage(d, message);
        hideInitializationVector(d, iv, message.length * 8 / 3 + 1 /* IV otherwise overwrites ciphertext */ + 24);

        ImageIO.write(d, "png", dest);
    }

    private static void copyImage(BufferedImage img, BufferedImage dest) {

        for (int i = 0; i < img.getWidth(); i++)
            for (int j = 0; j < img.getHeight(); j++)
                dest.setRGB(i, j, img.getRGB(i, j));
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

        if (Main.DEBUG) {
            System.out.println(Arrays.toString(binMessage.split("(?<=\\G.{8})")));
            ImageUtility.echoBits(img, 24, 24 + binMessage.length() / 3 + 1);
        }
    }

    private static void hideInitializationVector(BufferedImage img, byte[] iv, int offset) {

        String binIV = bytesToBinaryString(iv);

        StringBuilder buffer = new StringBuilder();
        buffer.append(binIV);
        for (int i = 0; i < 3 - binIV.length() % 3; i++)
            buffer.append('0');

        for (int i = 0; i < 42 + 1; i++) {

            int argb = img.getRGB((i + offset) % img.getWidth(), (i + offset) / img.getWidth());
            int a = ImageUtility.getA(argb);
            int r = ImageUtility.getR(argb);
            int g = ImageUtility.getG(argb);
            int b = ImageUtility.getB(argb);
            r = ImageUtility.manipulateBit(r, buffer.charAt(i * 3) - '0');
            g = ImageUtility.manipulateBit(g, buffer.charAt(i * 3 + 1) - '0');
            b = ImageUtility.manipulateBit(b, buffer.charAt(i * 3 + 2) - '0');
            argb = ImageUtility.getARGB(a, r, g, b);
            img.setRGB((i + offset) % img.getWidth(), (i + offset) / img.getWidth(), argb);
        }
    }

    private static void writeLength(BufferedImage img, BufferedImage dest, int length) {

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
            dest.setRGB(i % img.getWidth(), i / img.getWidth(), argb);
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

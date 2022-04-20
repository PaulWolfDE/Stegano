package de.paulwolf.stegano;

import de.paulwolf.stegano.ui.MenuUI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Main {

	public static final String VERSION_NAME = "Stegano";
	public static final char STD_ECHO_CHAR = '*';
	public static Font STD_FONT;
	public static Image IMAGE;
	
	public static void main(String[] args) {

		try {
			IMAGE = ImageIO.read(Objects.requireNonNull(Main.class.getResource("/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String osName = System.getProperty("os.name");
		System.out.println(osName);
		if (osName.toLowerCase().contains("linux"))
			STD_FONT = new Font("Bitstream Charter", Font.PLAIN, 15);
		else if (osName.toLowerCase().contains("windows"))
			STD_FONT = new Font("Times New Roman", Font.PLAIN, 15);
		else
			STD_FONT = new Font("Verdana", Font.PLAIN, 15);

		new MenuUI();
	}
}
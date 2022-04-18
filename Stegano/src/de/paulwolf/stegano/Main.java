package de.paulwolf.stegano;

import de.paulwolf.stegano.ui.MenuUI;

import java.awt.*;
import java.util.Locale;

public class Main {

	public static final String VERSION_NAME = "Stegano";
	public static final char STD_ECHO_CHAR = '*';
	public static Font STD_FONT;
	
	public static void main(String[] args) {

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
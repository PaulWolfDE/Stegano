package de.paulwolf.stegano.ui;

import de.paulwolf.stegano.Main;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class AboutUI extends JFrame {

    public AboutUI() {

        JEditorPane text = new JEditorPane();
        JScrollPane pane = new JScrollPane(text);

        text.setEditable(false);
        text.setFont(Main.STD_FONT);
        HTMLEditorKit ek = new HTMLEditorKit();
        text.setEditorKit(ek);
        text.setText("<html><h1 id=\"stegano-version-1-1-2\">Stegano Version 1.1.2</h1>\n" +
                "<p>Stegano is an open-source software for steganography. In cryptography, steganography is a method of hiding information in inconspicuous files. This often has utility when cryptography is banned in totalitarian states and must be hidden that files are encrypted.</p>\n" +
                "<p>Stegano hides plain text in image files. These are modified in such a way that it is invisible to the naked eye. Each pixel in an image consists of 3 or 4 bytes of information (red, green, blue, [alpha]). From the three color bytes Stegano always changes the least significant bit with the lowest valence. The message is translated into binary code and a number of bits are modified according to this message. The changes aim at minimal deviations, where a value of 42 (<code>00101010</code>) becomes 43 (<code>00101011</code>) or stays at 42 for a bit with value <code>0</code>.</p>\n" +
                "<p>Resulting image files are always saved in lossless PNG format. A signature in the image is intentionally omitted so that encryption with Stegano by algorithms is not conspicuous. However, this means that there will be no error message that can be distinguished from a wrong password if a wrong file is to be decrypted with Stegano.</p>\n" +
                "<p>Data is encrypted using the AES/Rijndael algorithm in GCM mode. The initialization vector for this is chosen randomly for each encryption.</p>\n" +
                "<p>License: GPL-3<br>" +
                "Created by Paul Wolf<br>" +
                "<a href=\"https://github.com/paulwolfde/stegano\">Github</a></p>\n</html>");

        text.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        this.setTitle(Main.VERSION_NAME + " - About");
        this.add(pane);
        this.setIconImage(Main.IMAGE);
        this.setLocationRelativeTo(null);
        this.setSize(400, 300);
        this.setVisible(true);
    }
}

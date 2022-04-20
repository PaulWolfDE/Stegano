package de.paulwolf.stegano.ui;

import de.paulwolf.stegano.Main;
import de.paulwolf.stegano.core.HideMessage;
import de.paulwolf.stegano.encrypt.EncryptionWizard;
import de.paulwolf.stegano.zip.GZIP;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.*;

public class EncryptUI implements ActionListener {

    JFrame frame = new JFrame(Main.VERSION_NAME);
    JPanel panel = new JPanel();
    JButton browse1 = new JButton("Browse");
    JButton browse2 = new JButton("Browse");
    JButton encrypt = new JButton("Encrypt");
    JToggleButton show = new JToggleButton("Show");
    JPasswordField key1 = new JPasswordField("Encryption Key");
    JPasswordField key2 = new JPasswordField("Repeat Key");
    JTextArea plaintext = new JTextArea("Plaintext", 10, 0);
    JScrollPane scrollpane = new JScrollPane(plaintext, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    PlaceholderField path1 = new PlaceholderField("Image Path");
    PlaceholderField path2 = new PlaceholderField("Destination Path");
    JFileChooser fileChooser = new JFileChooser();
    JFileChooser secondChooser = new JFileChooser();

    String tempURI;
    ProgressUI progress;
    byte[] pt;
    byte[] ciphertext;
    byte[] iv = new byte[16];

    public EncryptUI() {

        secondChooser.setSelectedFile(new File("Picture.png"));
        FileNameExtensionFilter secondFilter = new FileNameExtensionFilter("PNG Files", "png");
        secondChooser.setFileFilter(secondFilter);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (png, jpg, jpeg)", "jpg", "jpeg",
                "png");
        fileChooser.setFileFilter(filter);

        path1.setFont(Main.STD_FONT);
        path1.setPreferredSize(new Dimension(200, 29));
        path2.setFont(Main.STD_FONT);
        path2.setPreferredSize(new Dimension(200, 29));
        plaintext.setFont(Main.STD_FONT);
        key1.setFont(Main.STD_FONT);
        key1.setPreferredSize(new Dimension(150, 29));
        key2.setFont(Main.STD_FONT);
        key2.setPreferredSize(new Dimension(150, 29));
        browse1.setFont(Main.STD_FONT);
        browse2.setFont(Main.STD_FONT);
        show.setFont(Main.STD_FONT);
        encrypt.setFont(Main.STD_FONT);

        browse1.addActionListener(this);
        browse2.addActionListener(this);
        encrypt.addActionListener(this);
        show.addActionListener(this);
        show.setSelected(true);

        key1.setEchoChar((char) 0);
        key2.setEchoChar((char) 0);
        plaintext.setLineWrap(true);

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = createGBC(0, 0, GridBagConstraints.HORIZONTAL, 2, 1);
        panel.add(path1, gbc);
        gbc = createGBC(2, 0, GridBagConstraints.HORIZONTAL, 1, 1);
        panel.add(browse1, gbc);
        gbc = createGBC(0, 1, GridBagConstraints.HORIZONTAL, 2, 1);
        panel.add(path2, gbc);
        gbc = createGBC(2, 1, GridBagConstraints.HORIZONTAL, 1, 1);
        panel.add(browse2, gbc);
        gbc = createGBC(0, 2, GridBagConstraints.BOTH, 3, 1);
        panel.add(scrollpane, gbc);
        gbc = createGBC(0, 3, GridBagConstraints.HORIZONTAL, 1, 1);
        panel.add(key1, gbc);
        gbc = createGBC(1, 3, GridBagConstraints.HORIZONTAL, 1, 1);
        panel.add(key2, gbc);
        gbc = createGBC(2, 3, GridBagConstraints.HORIZONTAL, 1, 1);
        panel.add(show, gbc);
        gbc = createGBC(0, 4, GridBagConstraints.HORIZONTAL, 3, 1);
        panel.add(encrypt, gbc);

        frame.add(panel);
        frame.pack();
        frame.setIconImage(Main.IMAGE);
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == browse1) {
            int returnVal = fileChooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                path1.setText(file.getAbsoluteFile().toString());
            }
        }
        if (e.getSource() == browse2) {
            if (path1.getText().contains(".")) {
                String extension = "png";
                secondChooser.resetChoosableFileFilters();
                secondChooser
                        .setSelectedFile(new File(fileChooser.getSelectedFile().toString().split("\\.")[0] + ".png"));
                secondChooser.setFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + " Files", extension));
            }
            int returnVal = secondChooser.showSaveDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = secondChooser.getSelectedFile();
                path2.setText(file.getAbsoluteFile().toString());
            }
        }
        if (e.getSource() == show) {
            if (!show.isSelected()) {
                key1.setEchoChar(Main.STD_ECHO_CHAR);
                key2.setEchoChar(Main.STD_ECHO_CHAR);
            }
            else {
                key1.setEchoChar((char) 0);
                key2.setEchoChar((char) 0);
            }
        }
        if (e.getSource() == encrypt) {

            if (!new String(key1.getPassword()).equals(new String(key2.getPassword()))
                    || new String(key1.getPassword()).length() == 0) {

                JOptionPane.showMessageDialog(frame, "The keys do not match up!", "Argument error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (plaintext.getText().length() == 0) {

                JOptionPane.showMessageDialog(frame, "The plaintext cannot be empty!", "Argument error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (!new File(path1.getText()).exists()) {

                JOptionPane.showMessageDialog(frame, "There is no file at the specified input path!", "Argument error",
                        JOptionPane.ERROR_MESSAGE);
            } else {

                progress = new ProgressUI(frame, "Compressing...", "Encrypting...", "Writing...");

                String extension = "png";

                tempURI = path2.getText();

                if (tempURI.length() > extension.length() - 1)
                    if (!tempURI.substring(tempURI.length() - extension.length() - 1)
                            .equalsIgnoreCase("." + extension)) {
                        System.out.println(tempURI.substring(tempURI.length() - extension.length() - 1));
                        tempURI += "." + extension;
                    }

                progress.show();

                Thread hide = new Thread(() -> {
                    try {
                        System.out.println(tempURI);
                        HideMessage.hideMessage(new File(path1.getText()), new File(tempURI), ciphertext, iv);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    progress.update();
                });

                // IV gets generated
                (new SecureRandom()).nextBytes(iv);

                Thread encrypt = new Thread(() -> {
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e2) {
                        e2.printStackTrace();
                    }
					assert md != null;
					byte[] keyBytes = md.digest(new String(key1.getPassword()).getBytes());
                    SecretKey key = new SecretKeySpec(keyBytes, "AES");
                    try {
                        ciphertext = EncryptionWizard.encrypt(pt, key, iv);
                    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e2) {
                        e2.printStackTrace();
                    }
                    progress.update();
                    hide.start();
                });

                Thread zip = new Thread(() -> {
                    try {
                        pt = GZIP.compress(plaintext.getText());
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                    progress.update();
                    encrypt.start();
                });

                zip.start();
            }
        }
    }

    private static final int INGS_GAP = 10;

    public static GridBagConstraints createGBC(int x, int y, int fill, int width, int height) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;

        gbc.weightx = x == 2 ? 0.0 : 1.0;
        gbc.weighty = fill == GridBagConstraints.HORIZONTAL ? 0 : 1;

        gbc.insets = new Insets(INGS_GAP, INGS_GAP, INGS_GAP, INGS_GAP);
        return gbc;
    }
}

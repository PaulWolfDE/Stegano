package de.paulwolf.stegano.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.paulwolf.stegano.Main;
import de.paulwolf.stegano.core.ImageUtility;
import de.paulwolf.stegano.core.RecoverMessage;
import de.paulwolf.stegano.encrypt.EncryptionWizard;
import de.paulwolf.stegano.zip.GZIP;

public class DecryptUI implements ActionListener {

	JFrame frame = new JFrame(Main.VERSION_NAME);
	JPanel panel = new JPanel();
	JButton browse = new JButton("Browse");
	JButton decrypt = new JButton("Decrypt");
	JPasswordField key = new JPasswordField("Key");
	JTextArea plaintext = new JTextArea("Plaintext", 10, 0);
	JScrollPane scrollpane = new JScrollPane(plaintext, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	PlaceholderField path = new PlaceholderField("Image Path");
	JFileChooser fileChooser = new JFileChooser();
	JToggleButton show = new JToggleButton("Show");

	byte[] ciphertext;
	byte[] message;
	
	ProgressUI progress;

	public DecryptUI() {

		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Files", "png");
		fileChooser.setFileFilter(filter);

		browse.addActionListener(this);
		decrypt.addActionListener(this);
		show.addActionListener(this);

		path.setFont(Main.STD_FONT);
		path.setPreferredSize(new Dimension(200, 29));
		key.setFont(Main.STD_FONT);
		key.setPreferredSize(new Dimension(200, 29));
		plaintext.setFont(Main.STD_FONT);
		browse.setFont(Main.STD_FONT);
		show.setFont(Main.STD_FONT);
		decrypt.setFont(Main.STD_FONT);

		show.setSelected(true);
		key.setEchoChar((char) 0);
		plaintext.setLineWrap(true);

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = createGBC(0, 0, GridBagConstraints.HORIZONTAL, 1, 1);
		panel.add(path, gbc);
		gbc = createGBC(1, 0, GridBagConstraints.HORIZONTAL, 1, 1);
		panel.add(browse, gbc);
		gbc = createGBC(0, 1, GridBagConstraints.HORIZONTAL, 1, 1);
		panel.add(key, gbc);
		gbc = createGBC(1, 1, GridBagConstraints.HORIZONTAL, 1, 1);
		panel.add(show, gbc);
		gbc = createGBC(0, 2, GridBagConstraints.HORIZONTAL, 2, 1);
		panel.add(decrypt, gbc);
		gbc = createGBC(0, 3, GridBagConstraints.BOTH, 2, 1);
		panel.add(scrollpane, gbc);

		frame.add(panel);
		frame.pack();
		frame.setIconImage(Main.IMAGE);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == show) {

			if (show.isSelected())
				key.setEchoChar((char) 0);
			else
				key.setEchoChar(Main.STD_ECHO_CHAR);
		}

		if (e.getSource() == browse) {
			int returnVal = fileChooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				path.setText(file.getAbsoluteFile().toString());
			}
		}

		if (e.getSource() == decrypt) {

			progress = new ProgressUI(frame, "Reading...", "Decrypting...", "Decompressing...");
			
			Thread decompress = new Thread(() -> {
				try {
					plaintext.setText(GZIP.decompress(message));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				progress.update();
			});
			
			Thread decrypt = new Thread(() -> {
				MessageDigest md = null;
				try {
					md = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e2) {
					e2.printStackTrace();
				}
				assert md != null;
				byte[] keyBytes = md.digest(new String(key.getPassword()).getBytes());
				SecretKey key = new SecretKeySpec(keyBytes, "AES");
				boolean success = true;
				try {
					message = EncryptionWizard.decrypt(ciphertext, key, RecoverMessage.recoverInitializationVector(new File(path.getText())));
				} catch (InvalidKeyException | IllegalBlockSizeException  | InvalidAlgorithmParameterException e2) {
					e2.printStackTrace();
				} catch(BadPaddingException e2) {
					success = false;
					progress.kill();
					JOptionPane.showMessageDialog(frame, "The entered key is incorrect!", "Invalid credentials", JOptionPane.ERROR_MESSAGE);
				} catch(IOException e2) {
					success = false;
					progress.kill();
					JOptionPane.showMessageDialog(frame, "Could not open provided file!", "Invalid file", JOptionPane.ERROR_MESSAGE);
				}
				if (success) {
					progress.update();
					decompress.start();
				}
			});

			Thread read = new Thread(() -> {
				boolean success = true;
				try {
					ciphertext = RecoverMessage.recoverMessage(new File(path.getText()));
				} catch(IOException e2) {
					success = false;
					JOptionPane.showMessageDialog(frame, "Could not open provided file!", "Invalid file", JOptionPane.ERROR_MESSAGE);
					progress.kill();
				}
				if (success) {
					progress.update();
					decrypt.start();
				}
			});
			
			progress.show();
			read.start();
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

		gbc.weightx = x == 1 ? 0.0 : 1.0;
		gbc.weighty = fill == GridBagConstraints.HORIZONTAL ? 0 : 1;

		gbc.insets = new Insets(INGS_GAP, INGS_GAP, INGS_GAP, INGS_GAP);
		return gbc;
	}
}

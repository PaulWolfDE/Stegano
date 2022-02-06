package de.paulwolf.stegano.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.paulwolf.stegano.Main;
import de.paulwolf.stegano.core.RecoverMessage;
import de.paulwolf.stegano.encrypt.EncryptionWizard;
import de.paulwolf.stegano.zip.GZIP;

public class DecryptUI implements ActionListener {

	JFrame frame = new JFrame(Main.VERSION_NAME);
	JPanel panel = new JPanel();
	JButton browse = new JButton("Browse");
	JButton decrypt = new JButton("Decrypt");
	JPasswordField key = new JPasswordField("Key");
	JTextArea plaintext = new JTextArea("Plaintext", 5, 0);
	JScrollPane scrollpane = new JScrollPane(plaintext, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JTextField path = new JTextField("Image Path");
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

		show.setSelected(true);
		key.setEchoChar((char) 0);
		plaintext.setLineWrap(true);

		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(new GridBagLayout());
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1;
		gbc.weighty = 1;

		panel.add(path, gbc);
		gbc.gridx = 1;
		panel.add(browse, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(key, gbc);
		gbc.gridx = 1;
		panel.add(show, gbc);
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		panel.add(decrypt, gbc);
		gbc.gridy = 3;
		panel.add(scrollpane, gbc);

		frame.add(panel);
		frame.pack();
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
				key.setEchoChar('•');
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
			
			Thread decompress = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						plaintext.setText(GZIP.decompress(message));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					progress.update();
				}
			});
			
			Thread decrypt = new Thread(new Runnable() {
				@Override
				public void run() {
					MessageDigest md = null;
					try {
						md = MessageDigest.getInstance("SHA-256");
					} catch (NoSuchAlgorithmException e2) {
						e2.printStackTrace();
					}
					byte[] keyBytes = md.digest(new String(key.getPassword()).getBytes());
					SecretKey key = new SecretKeySpec(keyBytes, "AES");
					try {
						message = EncryptionWizard.ecbDecrypt(ciphertext, key);
					} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e2) {
						e2.printStackTrace();
					}
					progress.update();
					decompress.start();
				}
			});

			Thread read = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ciphertext = RecoverMessage.recoverMessage(new File(path.getText()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					progress.update();
					decrypt.start();
				}
			});
			
			progress.show();
			read.start();
		}
	}
}

package de.paulwolf.stegano.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.paulwolf.stegano.Main;
import de.paulwolf.stegano.core.HideMessage;
import de.paulwolf.stegano.encrypt.EncryptionWizard;
import de.paulwolf.stegano.zip.GZIP;

public class EncryptUI implements ActionListener, KeyListener {

	JFrame frame = new JFrame(Main.VERSION_NAME);
	JPanel panel = new JPanel();
	JButton browse1 = new JButton("Browse");
	JButton browse2 = new JButton("Browse");
	JButton encrypt = new JButton("Encrypt");
	JPasswordField key1 = new JPasswordField("Key");
	JPasswordField key2 = new JPasswordField("Repeat Key");
	JTextArea plaintext = new JTextArea("Plaintext", 10, 0);
	JScrollPane scrollpane = new JScrollPane(plaintext, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JTextField path1 = new JTextField("Image Path");
	JTextField path2 = new JTextField("Destination Path");
	JFileChooser fileChooser = new JFileChooser();
	JFileChooser secondChooser = new JFileChooser();

	String tempURI;
	ProgressUI progress;
	byte[] pt;
	byte[] ciphertext;

	public EncryptUI() {

		secondChooser.setSelectedFile(new File("Picture.png"));
		FileNameExtensionFilter secondFilter = new FileNameExtensionFilter("PNG Files", "png");
		secondChooser.setFileFilter(secondFilter);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (png, jpg, jpeg)", "jpg", "jpeg",
				"png");
		fileChooser.setFileFilter(filter);

		// encrypt.setPreferredSize(new Dimension(200, 40));
		browse1.setFont(new Font("Verdana", Font.BOLD, 14));
		browse2.setFont(new Font("Verdana", Font.BOLD, 14));
		encrypt.setFont(new Font("Verdana", Font.BOLD, 14));
		key1.setFont(new Font("Verdana", Font.PLAIN, 14));
		key2.setFont(new Font("Verdana", Font.PLAIN, 14));
		path1.setFont(new Font("Verdana", Font.PLAIN, 14));
		path2.setFont(new Font("Verdana", Font.PLAIN, 14));
		plaintext.setFont(new Font("Verdana", Font.PLAIN, 14));

		browse1.addActionListener(this);
		browse2.addActionListener(this);
		encrypt.addActionListener(this);

		key1.addKeyListener(this);
		key2.addKeyListener(this);

		key1.setEchoChar((char) 0);
		key2.setEchoChar((char) 0);
		plaintext.setLineWrap(true);

		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(new GridBagLayout());
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1;
		gbc.weighty = 1;

		panel.add(path1, gbc);
		gbc.gridx = 1;
		panel.add(browse1, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(path2, gbc);
		gbc.gridx = 1;
		panel.add(browse2, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		panel.add(scrollpane, gbc);
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		panel.add(key1, gbc);
		gbc.gridx = 1;
		panel.add(key2, gbc);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		panel.add(encrypt, gbc);

		frame.add(panel);
		frame.setSize(850, 460);
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

				Thread hide = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							System.out.println(tempURI);
							HideMessage.hideMessage(new File(path1.getText()), new File(tempURI), ciphertext);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						progress.update();
					}

				});

				Thread encrypt = new Thread(new Runnable() {

					@Override
					public void run() {
						MessageDigest md = null;
						try {
							md = MessageDigest.getInstance("SHA-256");
						} catch (NoSuchAlgorithmException e2) {
							e2.printStackTrace();
						}
						byte[] keyBytes = md.digest(new String(key1.getPassword()).getBytes());
						SecretKey key = new SecretKeySpec(keyBytes, "AES");
						try {
							ciphertext = EncryptionWizard.ecbEncrypt(pt, key);
						} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e2) {
							e2.printStackTrace();
						}
						progress.update();
						hide.start();
					}
				});

				Thread zip = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							pt = GZIP.compress(plaintext.getText());
						} catch (IOException e3) {
							e3.printStackTrace();
						}
						progress.update();
						encrypt.start();
					}
				});

				zip.start();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getSource() == key1)
			key1.setEchoChar('•');
		if (e.getSource() == key2)
			key2.setEchoChar('•');
	}

	@Override
	public void keyReleased(KeyEvent e) {
		;
	}
}

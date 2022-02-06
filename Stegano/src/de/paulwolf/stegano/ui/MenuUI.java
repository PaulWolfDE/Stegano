package de.paulwolf.stegano.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.paulwolf.stegano.Main;

public class MenuUI implements ActionListener {

	JFrame frame = new JFrame(Main.VERSION_NAME);
	JPanel panel = new JPanel();
	JLabel title = new JLabel(Main.VERSION_NAME, SwingConstants.CENTER);
	JButton encrypt = new JButton("Encrypt");
	JButton decrypt = new JButton("Decrypt");

	public MenuUI() {

		title.setFont(new Font("Verdana", Font.BOLD, 20));
		encrypt.setPreferredSize(new Dimension(200, 40));
		encrypt.setFont(new Font("Verdana", Font.BOLD, 14));
		decrypt.setPreferredSize(new Dimension(200, 40));
		decrypt.setFont(new Font("Verdana", Font.BOLD, 14));
		encrypt.addActionListener(this);
		decrypt.addActionListener(this);

		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(new GridBagLayout());
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1;
		gbc.weighty = 1;

		gbc.gridwidth = 2;
		panel.add(title, gbc);
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		panel.add(encrypt, gbc);
		gbc.gridx = 1;
		panel.add(decrypt, gbc);

		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		frame.setVisible(false);

		if (e.getSource() == encrypt)
			new EncryptUI();
		if (e.getSource() == decrypt)
			new DecryptUI();
	}
}

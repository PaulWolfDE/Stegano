package de.paulwolf.stegano.ui;

import de.paulwolf.stegano.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuUI implements ActionListener {

    JFrame frame = new JFrame(Main.VERSION_NAME);
    JPanel panel = new JPanel();
    JLabel title = new JLabel(Main.VERSION_NAME, SwingConstants.CENTER);
    JButton encrypt = new JButton("Hide and Encrypt");
    JButton decrypt = new JButton("Reveal and Decrypt");
    JButton about = new JButton("About");

    public MenuUI() {

        title.setFont(Main.STD_FONT);
        encrypt.setFont(Main.STD_FONT);
        decrypt.setFont(Main.STD_FONT);
        about.setFont(Main.STD_FONT);
        encrypt.addActionListener(this);
        decrypt.addActionListener(this);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGBC(0, 0, GridBagConstraints.HORIZONTAL, 1, 1);
        panel.add(encrypt, gbc);
        gbc = createGBC(1, 0, GridBagConstraints.HORIZONTAL, 1, 1);
        panel.add(decrypt, gbc);
        gbc = createGBC(0, 1, GridBagConstraints.HORIZONTAL, 2, 1);
        panel.add(about, gbc);

        frame.add(panel);
        frame.pack();
		frame.setMinimumSize(frame.getSize());
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
		if (e.getSource() == about)
			new AboutUI();
    }

    private static final int INGS_GAP = 10;

    public static GridBagConstraints createGBC(int x, int y, int fill, int width, int height) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;

        gbc.weightx = 1;
        gbc.weighty = 0;

        gbc.insets = new Insets(INGS_GAP, INGS_GAP, INGS_GAP, INGS_GAP);
        return gbc;
    }
}

package de.paulwolf.stegano.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressUI {

	JFrame frame = new JFrame("Working...");
	JPanel panel = new JPanel();
	JProgressBar bar = new JProgressBar(0);
	JLabel label = new JLabel("Working...");

	String[] t;
	Component parent;
	
	public void show() {
		
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(new GridBagLayout());
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1;
		gbc.weighty = 1;
		panel.add(bar, gbc);
		gbc.gridy = 1;
		panel.add(label, gbc);

		bar.setValue(0);
		bar.setMaximum(t.length);
		label.setText(t[0]);

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public ProgressUI(Component parent, String... tasks) {

		this.parent = parent;
		t = tasks;
	}

	public void update() {

		bar.setValue(bar.getValue() + 1);
		if (bar.getValue() == t.length)
			kill();
		else
			label.setText(t[bar.getValue()]);
	}

	public void kill() {
		frame.setVisible(false);
	}
}

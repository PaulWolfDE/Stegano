package de.paulwolf.stegano.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderField extends JTextField implements FocusListener {

    private final String placeholder;
    private String content;

    public PlaceholderField(String placeholder) {

        this.placeholder = placeholder;
        this.setText(this.placeholder);
        this.setForeground(Color.GRAY);
        this.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().equals(placeholder))
            this.setText(this.content);
        this.setForeground(Color.DARK_GRAY);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if ((this.content = this.getText()).equals("")) {
            this.setText(this.placeholder);
            this.setForeground(Color.GRAY);
        }
    }
}

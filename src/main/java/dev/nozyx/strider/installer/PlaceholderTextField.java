package dev.nozyx.strider.installer;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty() && !this.isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.setFont(getFont());
            Insets insets = getInsets();
            int y = getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2;
            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
        }
    }
}

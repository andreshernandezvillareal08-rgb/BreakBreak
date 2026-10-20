package main.java.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class MaterialButton extends JButton {

    private final Color primaryColor;

    public MaterialButton(String text, Color primaryColor) {
        super(text);
        this.primaryColor = primaryColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(primaryColor.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(primaryColor.brighter());
        } else {
            g2.setColor(primaryColor);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        super.paintComponent(g2);
        g2.dispose();
    }
}
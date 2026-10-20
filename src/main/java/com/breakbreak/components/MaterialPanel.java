package main.java.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class MaterialPanel extends JPanel {

    private final int cornerRadius;
    private final Color backgroundColor;

    public MaterialPanel(int cornerRadius, Color backgroundColor) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);


        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);

        g2.dispose();
    }
}
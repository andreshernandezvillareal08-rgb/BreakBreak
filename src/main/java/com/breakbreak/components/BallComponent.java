package main.java.com.breakbreak.components;

import main.java.com.breakbreak.util.ImageCache;

import javax.swing.*;
import java.awt.*;

public class BallComponent extends GameComponent {

    private int vx;
    private int vy;
    private ImageIcon ballImage;

    public BallComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
        initImage();
        setOpaque(false);
    }

    public BallComponent(int x, int y) {
        super(x, y, 30, 30);
        initImage();
        setOpaque(false);
    }

    private void initImage() {
        try {
            if (ImageCache.getInstance().getImage("ball") == null) {
                setImage("ball", "/images/ball.png");
            }
            ballImage = ImageCache.getInstance().getImage("ball");
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de la pelota: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        if (ballImage != null && ballImage.getImage() != null) {
            // Dibuja únicamente tu imagen sin bordes adicionales
            g2.drawImage(ballImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        g2.dispose();
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public int getVy() {
        return vy;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public void setY(int y) {
        setLocation(getX(), y);
    }
}
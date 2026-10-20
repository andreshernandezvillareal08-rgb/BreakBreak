package main.java.com.breakbreak.components;

import main.java.com.breakbreak.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


public abstract class GameComponent extends JComponent {

    protected String imageName;

    public GameComponent(int x, int y, int width, int height) {
        setBounds(x, y, width, height);
    }


    public void setImage(String name, String path) {
        this.imageName = name;
        if (!ImageCache.getInstance().containsImage(name)) {
            try {
                URL resource = getClass().getResource(path);
                if (resource != null) {
                    ImageIcon image = new ImageIcon(resource);
                    ImageCache.getInstance().addImage(name, image);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error al cargar la imagen: " + path);
            }
        }
    }

    public ImageIcon getImage() {
        if (imageName != null) {
            return ImageCache.getInstance().getImage(imageName);
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        ImageIcon icon = getImage();
        if (icon != null && icon.getImage() != null) {
            g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        g2.dispose();
    }
}
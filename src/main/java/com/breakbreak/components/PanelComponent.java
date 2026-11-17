package main.java.com.breakbreak.components;

import main.java.com.breakbreak.util.ImageCache;

import javax.swing.*;
import java.awt.*;

public class PanelComponent extends MaterialPanel {

    protected ImageIcon image;

    public PanelComponent(String name, String path) {
        super(0, new Color(0, 0, 0, 0)); // Pasamos los argumentos que exige MaterialPanel
        this.image = loadImage(name, path);
        setOpaque(false);
    }

    public PanelComponent(String name, String path, Dimension size) {
        super(0, new Color(0, 0, 0, 0)); // Pasamos los argumentos que exige MaterialPanel
        this.image = loadImage(name, path);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBounds(0, 0, size.width, size.height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setOpaque(false);
    }

    private ImageIcon loadImage(String name, String path) {
        ImageIcon cached = ImageCache.getInstance().getImage(name);
        if (cached != null) {
            return cached;
        }

        var url = getClass().getResource(path);
        if (url == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ocurrió un error al cargar la imagen!",
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }

        ImageIcon loaded = new ImageIcon(url);
        ImageCache.getInstance().addImage(name, loaded);
        return loaded;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null && image.getImage() != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }
}
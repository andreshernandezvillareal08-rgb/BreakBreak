package main.java.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BlockComponent extends GameComponent {
    private Image blockImage;
    private boolean destroyed = false;
    private int durability; // Dureza aleatoria (1 a 3 golpes)

    public BlockComponent(int x, int y, int width, int height, String imageName, String imagePath, int durability) {
        super(x, y, width, height);
        this.durability = durability;

        URL imgUrl = getClass().getResource(imagePath);
        if (imgUrl != null) {
            this.blockImage = new ImageIcon(imgUrl).getImage();
        } else {
            System.err.println("ADVERTENCIA: No se pudo encontrar la imagen del bloque en: " + imagePath);
        }
    }

    public int getDurability() {
        return durability;
    }

    public void hit() {
        durability--;
        if (durability <= 0) {
            destroy();
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        this.destroyed = true;
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!destroyed && blockImage != null) {
            g.drawImage(blockImage, 0, 0, getWidth(), getHeight(), this);

            // Indicador visual de resistencia para bloques más duros (más de 1 golpe)
            if (durability > 1) {
                g.setColor(new Color(255, 255, 255, 60));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
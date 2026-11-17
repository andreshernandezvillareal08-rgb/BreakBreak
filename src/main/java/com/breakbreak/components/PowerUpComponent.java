package main.java.com.breakbreak.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PowerUpComponent extends JComponent {

    public enum Type {
        // Puedes agregar aquí tus 19 power-ups numerados del 0 al 18
        L_RED(0, "L"),
        W_PINK(1, "W"),
        E_BLUE(2, "E");

        private final int index;
        private final String letter;

        Type(int index, String letter) {
            this.index = index;
            this.letter = letter;
        }

        public String getLetter() { return letter; }
        public int getIndex() { return index; }
    }

    private final Type type;
    private final int speed = 3;

    private static BufferedImage spriteSheet;

    // Constantes basadas exactamente en tus dimensiones (704x240, 10 filas, 8 frames)
    private static final int HALF_WIDTH = 352;  // 704 / 2 (Mitad izquierda y derecha)
    private static final int ROW_HEIGHT = 24;   // 240 / 10 filas
    private static final int TOTAL_FRAMES = 8;  // 8 fotogramas de animación

    private int currentFrame = 0;
    private int animationCounter = 0;

    static {
        try {
            URL url = PowerUpComponent.class.getResource("/images/powerups.png");
            if (url != null) {
                spriteSheet = ImageIO.read(url);
            } else {
                File file = new File("src/main/resources/images/powerups.png");
                if (file.exists()) {
                    spriteSheet = ImageIO.read(file);
                } else {
                    System.err.println("❌ No se encuentra 'powerups.png'.");
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error al leer la imagen powerups.png: " + e.getMessage());
        }
    }

    public PowerUpComponent(int x, int y, Type type) {
        this.type = type;
        setBounds(x, y, 40, 20);
    }

    public Type getType() {
        return type;
    }

    public void fall() {
        setLocation(getX(), getY() + speed);

        animationCounter++;
        if (animationCounter >= 5) {
            currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
            animationCounter = 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spriteSheet != null) {
            int idx = type.getIndex();

            // Fila vertical (0 al 9)
            int rowIndex = idx / 2;
            // Lado izquierdo (0) o derecho (1)
            int side = idx % 2;

            // Ancho de cada fotograma individual (352 / 8 = 44 px)
            int frameWidth = HALF_WIDTH / TOTAL_FRAMES;

            // Coordenadas de recorte en la hoja de sprites
            int srcX = (side * HALF_WIDTH) + (currentFrame * frameWidth);
            int srcY = rowIndex * ROW_HEIGHT;

            g.drawImage(spriteSheet,
                    0, 0, getWidth(), getHeight(),
                    srcX, srcY, srcX + frameWidth, srcY + ROW_HEIGHT,
                    null);
        } else {
            // Respaldo visual por si falla la imagen
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.MAGENTA);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.drawString(type.getLetter(), 10, 15);
        }
    }
}
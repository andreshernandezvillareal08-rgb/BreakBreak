package main.java.com.breakbreak.components;

import main.java.com.breakbreak.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LivesComponent extends JComponent {
    public static final int MAX_LIVES = 4;
    public static final int INITIAL_LIVES = 3;
    public static final int ICON_WIDTH = 62;
    public static final int ICON_HEIGHT = 58;
    private static final int ICON_GAP = 4;

    private final ImageIcon lifeIcon;
    private final ImageIcon noLifeIcon;
    private int lives;

    public LivesComponent() {
        this(INITIAL_LIVES);
    }

    public LivesComponent(int lives) {
        this.lives = Math.max(0, Math.min(MAX_LIVES, lives));
        this.lifeIcon = Objects.requireNonNull(loadIcon("life", "/image/life.png"), "No se pudo cargar /image/life.png");
        this.noLifeIcon = Objects.requireNonNull(loadIcon("no-life", "/image/no-life.png"), "No se pudo cargar /image/no-life.png");
        setOpaque(false);
    }

    public LivesComponent(int lives, int x, int y, int width, int height) {
        this(lives);
        setBounds(x, y, width, height);
    }

    private ImageIcon loadIcon(String key, String path) {
        ImageIcon cached = ImageCache.getInstance().getImage(key);
        if (cached != null) return cached;

        var url = getClass().getResource(path);
        if (url == null) return null;
        ImageIcon loaded = new ImageIcon(url);
        ImageCache.getInstance().addImage(key, loaded);
        return loaded;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, Math.min(MAX_LIVES, lives));
        repaint();
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        setLives(lives - 1);
    }

    public void addLife() {
        setLives(lives + 1);
    }

    public void reset() {
        setLives(INITIAL_LIVES);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int totalOrigW = MAX_LIVES * ICON_WIDTH + (MAX_LIVES - 1) * ICON_GAP; // 260
        double scaleX = (double) getWidth() / totalOrigW;
        double scaleY = (double) getHeight() / ICON_HEIGHT;
        double scale = Math.min(scaleX, scaleY);

        int drawW = (int) Math.round(ICON_WIDTH * scale);
        int drawH = (int) Math.round(ICON_HEIGHT * scale);
        int drawGap = (int) Math.round(ICON_GAP * scale);

        int totalDrawW = MAX_LIVES * drawW + (MAX_LIVES - 1) * drawGap;
        int startX = (getWidth() - totalDrawW) / 2;
        int startY = (getHeight() - drawH) / 2;

        for (int i = 0; i < MAX_LIVES; i++) {
            Image img = (i < lives) ? lifeIcon.getImage() : noLifeIcon.getImage();
            int x = startX + i * (drawW + drawGap);
            g2.drawImage(img, x, startY, drawW, drawH, this);
        }
        g2.dispose();
    }
}
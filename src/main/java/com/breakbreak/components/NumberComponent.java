package main.java.com.breakbreak.components;

import main.java.com.breakbreak.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NumberComponent extends JComponent {
    public static final int DIGITS_IN_SHEET = 10;
    public static final int DIGIT_WIDTH = 82;
    public static final int DIGIT_HEIGHT = 94;
    private static final String SHEET_NAME = "numbers";
    private static final String SHEET_PATH = "/image/numbers.png";

    private final ImageIcon sheet;
    private int digits;
    private int value;

    public NumberComponent(int digits) {
        this(digits, 0);
    }

    public NumberComponent(int digits, int value) {
        if (digits < 1) {
            throw new IllegalArgumentException("digits must be >= 1");
        }
        this.digits = digits;
        this.value = Math.max(0, value);
        this.sheet = Objects.requireNonNull(loadSheet(), "No se pudo cargar " + SHEET_PATH);
        setOpaque(false);
        setPreferredSize(new Dimension(DIGIT_WIDTH * digits, DIGIT_HEIGHT));
    }

    public NumberComponent(int digits, int x, int y, int width, int height) {
        this(digits);
        setBounds(x, y, width, height);
    }

    private ImageIcon loadSheet() {
        ImageIcon cached = ImageCache.getInstance().getImage(SHEET_NAME);
        if (cached != null) return cached;

        var url = getClass().getResource(SHEET_PATH);
        if (url == null) return null;
        ImageIcon loaded = new ImageIcon(url);
        ImageCache.getInstance().addImage(SHEET_NAME, loaded);
        return loaded;
    }

    public void setValue(int value) {
        this.value = Math.max(0, value);
        repaint();
    }

    public int getValue() {
        return value;
    }

    public void setDigits(int digits) {
        if (digits < 1) {
            throw new IllegalArgumentException("digits must be >= 1");
        }
        this.digits = digits;
        repaint();
    }

    public int getDigits() {
        return digits;
    }

    public String getDisplayText() {
        return String.format("%0" + digits + "d", value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sheet == null || sheet.getImage() == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        String text = getDisplayText();
        int count = text.length();
        int srcDigitW = sheet.getIconWidth() / DIGITS_IN_SHEET;
        int srcDigitH = sheet.getIconHeight();

        int destW = getWidth() / count;
        int destH = getHeight();

        for (int i = 0; i < count; i++) {
            int digit = text.charAt(i) - '0';
            if (digit < 0 || digit > 9) continue;

            int sx1 = digit * srcDigitW;
            int sx2 = sx1 + srcDigitW;
            int dx1 = i * destW;
            int dx2 = dx1 + destW;

            g2.drawImage(sheet.getImage(), dx1, 0, dx2, destH, sx1, 0, sx2, srcDigitH, this);
        }
        g2.dispose();
    }
}
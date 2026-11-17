package main.java.com.breakbreak.enums;

public enum PowerUpType {

    L_RED(0, "L"),
    W_PINK(1, "W"),
    E_BLUE(2, "E");

    private final int spriteIndex;
    private final String letter;

    PowerUpType(int spriteIndex, String letter) {
        this.spriteIndex = spriteIndex;
        this.letter = letter;
    }

    public int getSpriteIndex() {
        return spriteIndex;
    }

    public String getLetter() {
        return letter;
    }

    public static PowerUpType fromIndex(int index) {
        for (PowerUpType type : values()) {
            if (type.getSpriteIndex() == index) {
                return type;
            }
        }
        throw new IllegalArgumentException("Power-up inválido: " + index);
    }
}
package main.java.com.breakbreak.components;

public class PaddleComponent extends GameComponent {

    public PaddleComponent(int x, int y, int width, int height, String imageName, String imagePath) {
        super(x, y, width, height);
        setImage(imageName, imagePath);
    }

    public PaddleComponent(int x, int y, int width, int height) {
        this(x, y, width, height, "paddle", "/images/paddle.png");
    }
}
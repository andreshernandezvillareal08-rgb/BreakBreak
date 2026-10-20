package main.java.com.breakbreak.components;

public class BlockComponent extends GameComponent {

    public BlockComponent(int x, int y, int width, int height, String imageName, String imagePath) {
        super(x, y, width, height);
        setImage(imageName, imagePath);
    }
}
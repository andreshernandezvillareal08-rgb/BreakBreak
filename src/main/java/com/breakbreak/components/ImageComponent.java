package main.java.com.breakbreak.components;


public class ImageComponent extends GameComponent {


    public ImageComponent(int x, int y, int width, int height, String imageName, String imagePath) {
        super(x, y, width, height);
        setImage(imageName, imagePath);
    }
}
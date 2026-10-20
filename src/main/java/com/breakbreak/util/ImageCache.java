package main.java.com.breakbreak.util;

import javax.swing.ImageIcon;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ImageCache {

    private static ImageCache instance;
    private final Map<String, ImageIcon> images;

    private ImageCache() {
        images = new HashMap<>();
    }

    public static synchronized ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }


    public ImageIcon getImage(String name) {
        if (!name.toLowerCase().endsWith(".png") && !name.toLowerCase().endsWith(".jpg")) {
            name = name + ".png";
        }

        if (images.containsKey(name)) {
            return images.get(name);
        }

        String path = "/images/" + name;
        URL imageUrl = getClass().getResource(path);

        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            images.put(name, icon);
            return icon;
        } else {
            System.err.println("¡ADVERTENCIA! No se pudo encontrar la imagen en la ruta: " + path);
            return null;
        }
    }

    public void addImage(String name, ImageIcon image) {
        images.put(name, image);
    }

    public boolean containsImage(String name) {
        return images.containsKey(name);
    }
}
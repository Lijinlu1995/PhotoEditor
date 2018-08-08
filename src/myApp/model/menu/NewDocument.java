package myApp.model.menu;

import javafx.scene.paint.Color;

public class NewDocument {

    // Width of the document
    private final int width;

    // height of the document
    private final int height;

    // Background color of the document
    private final Color color;

    public NewDocument(int w, int h, Color c) {
        width = w;
        height = h;
        color = c;
    }

    public int getWidth() {
        return width;
    }

    public int getHeiht() {
        return height;
    }

    public Color getColor() {
        return color;
    }
}

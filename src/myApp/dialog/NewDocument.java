package myApp.dialog;

import javafx.scene.paint.Color;

/**
 * <h1>NewDocument</h1>
 *
 * Contain all information about a new document (workspace)
 */
public class NewDocument {

    // Width of the document
    private final int width;

    // height of the document
    private final int height;

    // Background color of the document
    private final Color color;

    /**
     * Constructor
     *
     * @param w width
     * @param h height
     * @param c background color
     */
    public NewDocument(int w, int h, Color c) {
        width = w;
        height = h;
        color = c;
    }

    /**
     * Return width
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Return height
     *
     * @return height
     */
    public int getHeiht() {
        return height;
    }

    /**
     * Return background color
     *
     * @return color
     */
    public Color getColor() {
        return color;
    }
}

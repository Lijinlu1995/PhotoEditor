package myApp.tool.settings;

import javafx.scene.paint.Color;

/**
 * <h1>ColorConfigurableTool</h1>
 *
 * This interface represents tools that can change color. The ToolColorSettings
 * objects use this interface to manage a tool color.
 */
public interface ColorConfigurableTool {

    /**
     * Set the tool current color
     *
     * @param color the new Color of the tool
     */
    public void setColor(Color color);

    /**
     * This method returns the current Color of the tool.
     *
     * @return the current Color
     */
    public Color getColor();
}

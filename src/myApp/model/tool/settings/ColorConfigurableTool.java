package myApp.model.tool.settings;

import javafx.scene.paint.Color;

/* This interface represents tools that can change color. The ToolColorSettings
 * objects use this interface to manage a tool color.*/
public interface ColorConfigurableTool {

    //Set the tool current color

    public void setColor(Color color);

    //This method returns the current Color of the tool.

    public Color getColor();
}

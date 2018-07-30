package myApp.tool.settings;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

/**
 * <h1>ToolColorSettings</h1>
 *
 * ToolColorSettings manages ColorConfigurableTools and offers a javafx control
 * element to do so. It uses a ColorPicker to keep track of an active color and to
 * allow the user to select a color.
 */
public class ToolColorSettings extends ToolSettings {

    // The target to configure
    private ColorConfigurableTool target = null;

    // ColorPicker to manage colors
    private ColorPicker cp;

    /**
     * Constructor. Creates the ColorPicker and sets a default color for the
     * ToolColorSettings instance.
     *
     * @param defaultColor the first color of the ColorPicker
     */
    public ToolColorSettings(Color defaultColor) {
        // Create the ColorPicker and set its color
        cp = new ColorPicker();
        cp.setValue(defaultColor);
        cp.getStyleClass().add("button");

        // Add an action to manage the tool color on user selection
        cp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                target.setColor(cp.getValue());
            }
        });
        cp.getStyleClass().add("small");

        getChildren().add(cp);
    }

    /**
     * Set the current tool target. It must be a ColorConfigurableTool and the
     * ToolColorSettings instance checks if the tool already has a color set, and if
     * so, doesn't update its color.
     *
     * On the contrary, if the tool returns a null object, it pick the current color
     * and applies it to the tool target.
     *
     * @param target
     */
    public void setTarget(ColorConfigurableTool target) {
        this.target = target;

        // Get color and check if the target already has a color set
        Color color = target.getColor();
        if (color != null) {
            // Update the ColorPicker to represent the target
            cp.setValue(color);
        } else {
            // Update the target
            target.setColor(cp.getValue());
        }
    }

    /**
     * Return the current color.
     *
     * @return the current color
     */
    public Color getColor() {
        return cp.getValue();
    }
}

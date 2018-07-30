package myApp.tool.settings;

import javafx.scene.layout.HBox;

/**
 * <h1>ToolSettings</h1>
 *
 * ToolSettings is a general abstraction for a component which purpose is to manage a
 * specific tool instance settings.
 *
 * It extends HBox in order to be used as a block in an other containing Node.
 */
public abstract class ToolSettings extends HBox {

    // Set a general spaceing of 10 pixels.
    public ToolSettings() {
        setSpacing(10);
    }
}

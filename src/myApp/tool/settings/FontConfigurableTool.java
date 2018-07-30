package myApp.tool.settings;

import javafx.scene.text.Font;

/**
 * <h1>FontConfigurableTool</h1>
 *
 * The FontConfigurableTool represents tools that can change Font settings. It uses
 * JavaFx javafx.scene.text.font Font instances to represent font-family and
 * font-sizes for example.
 */
public interface FontConfigurableTool {

    /**
     * Set the new Font of the tool.
     *
     * @param font the new Font
     */
    public void setFont(Font font);

    /**
     * Get the current font of the tool. This method should be allowed to return null
     * if the tool doesn't have a current Font available.
     *
     * @return the current Font
     */
    public Font getFont();
}

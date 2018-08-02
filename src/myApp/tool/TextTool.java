package myApp.tool;

import myApp.tool.settings.ColorConfigurableTool;
import myApp.layer.TextN;
import myApp.tool.settings.FontConfigurableTool;
import myApp.workspace.Workspace;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/* The text tool manages GEMMSTexts, allowing to set their color, size and font. It
 * also allows to change a GEMMSText content by clicking on it.
 */
public class TextTool extends AbstractTool implements ColorConfigurableTool, FontConfigurableTool {

    public TextTool(Workspace workspace) {
        super(workspace);
        workspace.getLayerTool().setCursor(Cursor.TEXT);
    }

    /*  Get a dialog window to ask for a user text input. The resulting Optional
     * object will either contain a null String if the user didn't enter anything, or
     * a String containing one ore more paragraphs separated by '\n' character.
     *
     *
     * @param def the default value of the input text area field
     * @return an Optional<String> object that may contain a String
     */
    public static Optional<String> getDialogText(String def) {
        // Create Dialog
        Dialog dialog = new Dialog<>();

        // Set the title
        dialog.setTitle("Please enter your text.");

        // Set button
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Set text field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Set prompt invite
        Label prompt = new Label("Please enter your text");

        // Set the text area
        final TextArea text = new TextArea();
        if (def != null) {
            text.setText(def);
        }

        // Add elements
        grid.add(prompt, 0, 0);
        grid.add(text, 0, 1);

        // Add to the dialog
        dialog.getDialogPane().setContent(grid);

        // Request focus
        Platform.runLater(() -> text.requestFocus());

        // Return result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                List<CharSequence> ps = text.getParagraphs();
                String result = "";
                int i = 0;
                // Add each paragraph to a String
                for (CharSequence p : ps) {
                    result += p.toString();
                    if (++i < ps.size()) {
                        result += "\n";
                    }
                }
                return result;
            }

            return null;
        });

        return dialog.showAndWait();
    }

    /* Request a dialog text input, and apply the changes to the layer parameter if
     * it is a GEMMSText layer*/
    private static void dialogTextValue(Node layer) {
        // Check if the layer is a GEMMSText
        if (layer instanceof TextN) {
            // Get a default value for the promp dialog
            String def = ((TextN) layer).getText();
            Optional<String> result = getDialogText(def);
            // Modify all GEMMSText layers
            if (result.isPresent()) {
                TextN text = (TextN) layer;
                double oldWidth = text.getBoundsInParent().getWidth();
                text.setText(result.get());
                // Recenter the text horizontally
                text.setTranslateX(text.getTranslateX() + (oldWidth - text.getBoundsInParent().getWidth()) / 2);
            }
        }
    }

    @Override
    public void mousePressed(double x, double y) {
    }
    @Override
    public void mouseDragged(double x, double y) {
    }

    /*
     * The mouseReleased action. TextTool will check the selected layers in the
     * Workspace, and if there is only one selected Node and this Node is a
     * GEMMSText, it will check if the click event happened on the Text boundaries.
     *
     * If so, it will open the dialog window to allow user to change the text
     * content.
     *
     * @param x the x coordinate of the event
     * @param y the y coordinate of the event
     */
    @Override
    public void mouseReleased(double x, double y) {
        // Retrieve the current layers
        List<Node> layers = workspace.getCurrentLayers();

        // If there is only one layer and it is a GEMMSText
        if (layers.size() == 1 && layers.get(0) instanceof TextN) {
            TextN layer = (TextN) layers.get(0);
            /*
          * Get the layer dimensions and position and check if the click 
          * happened inside the boundaries
             */
            // Dimensions
            int layerW = (int) layer.getBoundsInParent().getWidth();
            int layerH = (int) layer.getBoundsInParent().getHeight();
            // Position
            int layerX = (int) (layer.getX() + layer.getTranslateX());
            int layerY = (int) (layer.getY() + layer.getTranslateY() - layerH / 2);

            // Check if the click happened inside the Text boundaries
            if (x >= layerX && y >= layerY && x <= layerX + layerW && y <= layerY + layerH) {
                TextTool.dialogTextValue(layer);
            }
        }

    }

    /*
     * Get the current color of the TextTool. If exactly one GEMMSText layer is
     * currently selected, the tool will return the color of this Text.
     *
     * @return the current Color of the tool.
     */
    @Override
    public Color getColor() {
        List<Node> layers = workspace.getCurrentLayers();
        if (layers.size() == 1 && layers.get(0) instanceof TextN) {
            TextN t = (TextN) layers.get(0);
            return (Color) t.getFill();
        } else {
            return null;
        }
    }

    /*
     * Set the current Color of the tool. If exactly one GEMMSText layer is currently
     * selected, the tool will set the color of this Text.
     *
     * @param color the new color
     */
    @Override
    public void setColor(Color color) {
        List<Node> layers = workspace.getCurrentLayers();
        if (layers.size() == 1 && layers.get(0) instanceof TextN) {
            TextN t = (TextN) layers.get(0);
            t.setFill(color);
        }
    }

    /*
     * Set the current Font of the tool. If exactly one GEMMSText layer is currently
     * selected, the tool will return the Font of this Text.
     *
     * @param font the new font
     */
    @Override
    public void setFont(Font font) {
        List<Node> layers = workspace.getCurrentLayers();
        if (layers.size() == 1 && layers.get(0) instanceof TextN) {
            TextN t = (TextN) layers.get(0);
            double oldWidth = t.getBoundsInParent().getWidth();
            t.setFont(font);
            t.setTranslateX(t.getTranslateX() + (oldWidth - t.getBoundsInParent().getWidth()) / 2);
        }
    }

    /*
     * Get the current font of the tool. If exactly one GEMMSText layer is currently
     * selected, the tool will return the Font of this Text.
     *
     * @return the Font of the tool
     */
    @Override
    public Font getFont() {
        List<Node> layers = workspace.getCurrentLayers();
        if (layers.size() == 1 && layers.get(0) instanceof TextN) {
            TextN t = (TextN) layers.get(0);
            return t.getFont();
        } else {
            return null;
        }
    }

    @Override
    public void mouseMoved(double x, double y) {
    }

}

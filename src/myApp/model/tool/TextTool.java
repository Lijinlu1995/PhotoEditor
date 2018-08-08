package myApp.model.tool;

import myApp.model.tool.settings.ColorConfigurableTool;
import myApp.model.layer.TextN;
import myApp.model.tool.settings.FontConfigurableTool;
import myApp.model.workspace.Workspace;
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

/* The text tool manages Texts, allowing to set their color, size and font. It
 * also allows to change a Text content by clicking on it.
 */
public class TextTool extends AbstractTool implements ColorConfigurableTool, FontConfigurableTool {

    public TextTool(Workspace workspace) {
        super(workspace);
        workspace.getLayerTool().setCursor(Cursor.TEXT);
    }

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

    @Override
    public void setColor(Color color) {
        List<Node> layers = workspace.getCurrentLayers();
        if (layers.size() == 1 && layers.get(0) instanceof TextN) {
            TextN t = (TextN) layers.get(0);
            t.setFill(color);
        }
    }

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

package myApp.model.tool;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
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
import javafx.scene.layout.GridPane;
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

        dialog.setHeight(300);

        GridPane grid1 = new GridPane();
        grid1.setHgap(10);
        grid1.setVgap(5);
        // Set text field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 150, 10, 10));

        // Set prompt invite
        Label prompt = new Label("Please enter your text");

        Button b0 = new Button("normal");
        Button b1 = new Button("circle");
        Button b2 = new Button("triangle");
        Button b3 = new Button("polygon");

        grid1.add(b1,0,0);
        grid1.add(b2,1,0);
        grid1.add(b3,2,0);
        grid1.add(b0,3,0);
        Circle circle = new Circle();
        circle.setCenterX(155);
        circle.setCenterY(50);
        circle.setRadius(90);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setVisible(false);

        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double[]{
                100.0, -50.0,
                0.0, 140.0,
                200.0, 140.0 });
        triangle.setFill(Color.TRANSPARENT);
        triangle.setStroke(Color.BLACK);
        triangle.setVisible(false);

        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
                100.0, -50.0,
                0.0, 70.0,
                0.0, 140.0,
                200.0, 140.0,
                250.0, 70.0
        });
        polygon.setFill(Color.TRANSPARENT);
        polygon.setStroke(Color.BLACK);
        polygon.setVisible(false);
        // Set the text area
        final TextArea text = new TextArea();
        if (def != null) {
            text.setText(def);
        }


        // Add elements
        grid.add(grid1,0,0);
        grid.add(prompt, 0, 1);
        grid.add(text, 0, 2);
        grid.add(circle,0,2);
        grid.add(triangle,0,2);
        grid.add(polygon,0,2);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                circle.setVisible(true);
                triangle.setVisible(false);
                polygon.setVisible(false);
            }
        });
        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                triangle.setVisible(true);
                circle.setVisible(false);
                polygon.setVisible(false);

            }
        });
        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                polygon.setVisible(true);
                triangle.setVisible(false);
                circle.setVisible(false);

            }
        });
        b0.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                polygon.setVisible(false);
                triangle.setVisible(false);
                circle.setVisible(false);

            }
        });

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
     * it is a Text layer*/
    private static void dialogTextValue(Node layer) {
        // Check if the layer is a Text
        if (layer instanceof TextN) {
            // Get a default value for the promp dialog
            String def = ((TextN) layer).getText();
            Optional<String> result = getDialogText(def);
            // Modify all Text layers
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

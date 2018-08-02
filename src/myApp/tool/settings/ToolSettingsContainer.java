package myApp.tool.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

//ToolSettingsContainer are meant to contain ToolSettings.
public class ToolSettingsContainer extends HBox {

    public ToolSettingsContainer(Node... settings) {
        // Set HBox container
        setAlignment(Pos.CENTER);
        setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Set padding and alignement
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(10);
        setAlignment(Pos.CENTER);

        // Add elements
        for (Node s : settings) {
            getChildren().add(s);
        }

    }
}

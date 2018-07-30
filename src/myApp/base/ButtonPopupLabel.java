package myApp.base;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * <h1>ButtonPopuLabel</h1>
 *
 * Create a button popup label styling to describe a button function. This class
 * styles a HBox. Use it to have similar look between all buttons labels.
 */
public class ButtonPopupLabel extends HBox {

    public ButtonPopupLabel(String text) {

        // Set HBox container
        setAlignment(Pos.CENTER);
        setBackground(new Background(new BackgroundFill(Color.web("#cdcdcd"),
                CornerRadii.EMPTY, Insets.EMPTY)));

        // Set padding and alignement
        setPadding(new Insets(5, 5, 5, 5));
        setAlignment(Pos.CENTER);

        setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.25) , 1 ,0 , 1 , 1 );");

        // Add elements
        getChildren().add(new Label(text));
    }
}

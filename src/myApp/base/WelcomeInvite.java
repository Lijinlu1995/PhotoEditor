package myApp.base;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/* Utility class to avoid styling many times the same elements. It create a styled
 * VBox containing a label and a button. Its purposes is for example to propose an
 * action to the user.
 */
public class WelcomeInvite extends VBox {

     /*Constructor. Styles the VBox WelcomeInvite extends from, and adds the label
     and the button to its children.*/

    public WelcomeInvite(Label label, Button button) {
        // Style the VBox
        setBackground(new Background(new BackgroundFill(Color.web("#ededed"),
                new CornerRadii(5), Insets.EMPTY)));
        setPadding(new Insets(15, 15, 25, 15));
        setAlignment(Pos.CENTER);

        // Style the Label and add it to the chidlren
        label.setFont(Font.font(label.getFont().getFamily(), 18));
        label.setPadding(new Insets(20, 30, 20, 30));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setWrapText(true);
        getChildren().add(label);

        // Style the Button and add it to the children
        button.getStyleClass().add("welcome-button");
        button.setPrefSize(100, 80);
        getChildren().add(button);
    }
}

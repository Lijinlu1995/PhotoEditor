package myApp.workspace;

import myApp.base.CSSIcons;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * <h1>DefaultCell</h1>
 *
 * Default Cell implementation for the GEMMS application.
 *
 * @param <T> the type of elements represented
 */
public class DefaultCell<T> extends Cell<T> {

    static int count = 0;
    Text t;
    private static int MAX_LENGTH = 10;

    public DefaultCell(T target) {
        super(target);
        getStyleClass().add("deslected");

        // Create a thumbnail for estetic purposes
        AnchorPane rect = new AnchorPane();
        rect.setPrefSize(35, 35);
        rect.setMinSize(35, 35);

        // Add a Label (To be replaced by the name of the type of Node ?)
        t = new Text("Layer");
        t.setFont(Font.font(t.getFont().getFamily(), 10));
        t.setWrappingWidth(65);

        if (LayerListable.class.isInstance(target)) {
            rect.getStyleClass().add(((LayerListable) target).getThumbnailClass());
            String name = ((LayerListable) target).getLayerName();
            t.setText(parseName(name));
        }

        // Button to toggle the layer visibility
        Button visibility = new Button("");
        visibility.getStyleClass().add(CSSIcons.VISIBLE);
        visibility.setPrefWidth(30);
        visibility.setPrefHeight(10);
        visibility.setMaxHeight(10);
        visibility.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (DefaultCell.this.getTarget() instanceof Node) {
                    Node n = (Node) DefaultCell.this.getTarget();
                    if (n.isVisible()) {
                        n.setVisible(false);
                        visibility.getStyleClass().remove(CSSIcons.VISIBLE);
                        visibility.getStyleClass().add(CSSIcons.HIDDEN);
                    } else {
                        n.setVisible(true);
                        visibility.getStyleClass().remove(CSSIcons.HIDDEN);
                        visibility.getStyleClass().add(CSSIcons.VISIBLE);
                    }
                }
            }
        });

        // Add it to the LayerCell
        getChildren().add(rect);
        getChildren().add(t);
        getChildren().add(visibility);

        // Align everything
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);
    }

    private static String parseName(String name) {
        if (name.length() > MAX_LENGTH) {
            name = name.substring(0, MAX_LENGTH);
            name += "...";
        }
        return name;
    }

    @Override
    public void select() {
        super.select();
        getStyleClass().remove("deselected");
        getStyleClass().add("selected");
    }

    @Override
    public void deSelect() {
        super.deSelect();
        getStyleClass().remove("selected");
        getStyleClass().add("deselected");
    }

    @Override
    public void setLayerName(String name) {
        if (LayerListable.class.isInstance(getTarget())) {
            ((LayerListable) getTarget()).setLayerName(name);
        }

        t.setText(parseName(name));
    }
}

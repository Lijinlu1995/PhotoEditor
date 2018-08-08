package myApp.controller;

import myApp.model.tool.ColorSet;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class LayerController {

    // Main controller
    private MainController mainController;

    @FXML
    private VBox layerController;

    @FXML
    private VBox historyViewer;

    @FXML
    private AnchorPane colorController;

    // Init this controller
    public void init(MainController mainController) {
        this.mainController = mainController;

        colorController.getChildren().add(ColorSet.getInstance().getColorController());
    }

    public void addLayerController(VBox b) {
        layerController.getChildren().add(b);
    }

    public void addHistoryController(ListView view) {
        historyViewer.getChildren().add(view);
    }

    public void clearLayerBox() {
        layerController.getChildren().clear();
    }

    public void clearHistoryBox() {
        historyViewer.getChildren().clear();
    }
}

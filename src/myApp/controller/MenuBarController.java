package myApp.controller;

import myApp.dialog.NewDocument;
import myApp.dialog.NewDocumentDialog;
import myApp.dialog.OpenDocumentDialog;
import myApp.dialog.ResizeDialog;
import myApp.base.Document;
import myApp.layer.CanvasN;
import myApp.workspace.Workspace;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MenuBarController {

    // Main controller
    private MainController mainController;

    @FXML
    private HBox toolSettingsContainer;

    // Init this controller
    public void init(MainController c) {
        mainController = c;
    }

    // Clear tool settings
    public void clearToolSettings() {
        toolSettingsContainer.getChildren().clear();
    }

    //Add and display tool settings
    public void displayToolSetting(HBox toolBox) {
        clearToolSettings();
        if (toolBox != null) {
            toolSettingsContainer.getChildren().add(toolBox);
        }
    }

    /*Action when clicked on new button. Create a new document and a workspace. And
     create a new tab with the workspace.*/
    @FXML
    protected void newButtonAction(ActionEvent e) {

        mainController.hideWelcome();

        // Create a new dialog
        NewDocumentDialog dialog = new NewDocumentDialog();

        // Display dialog
        Optional<NewDocument> result = dialog.showAndWait();

        // Dialog OK
        if (result.isPresent()) {

            int width = result.get().getWidth();
            int height = result.get().getHeiht();
            Color color = result.get().getColor();

            // Create a new document
            Document document = new Document(mainController.getStage(), width, height);

            // Get workspace
            Workspace w = document.workspace();

            CanvasN canvas = new CanvasN(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(color);
            gc.fillRect(0, 0, width, height);

            mainController.createTab(document);

            // Set background
            w.addLayer(canvas);

            mainController.addDocument(document);
        }
    }

    /*Action when clicked on open button. Open a document. And create a new tab with
     the workspace contained in the document.*/
    @FXML
    protected void openButtonAction(ActionEvent e) {

        mainController.hideWelcome();

        OpenDocumentDialog dialog = new OpenDocumentDialog(mainController.getStage());

        File f = dialog.showAndWait();
        if (f != null) {
            Document document = null;
            try {
                document = new Document(mainController.getStage(), f);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(MainController.class.getName())
                        .log(Level.SEVERE, null, ex);
            }

            mainController.createTab(document);

            mainController.addDocument(document);
        }
    }

    // Action when clicked on save button. Save the current workspace document.
    @FXML
    protected void saveButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            // Get current tab
            Tab tab = mainController.getCurrentTab();

            // Research document with workspace
            Document d = mainController.getDocument(w);

            // Save document
            if (d != null) {
                try {
                    d.save();
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }

            // Set tab title
            tab.setText(d.name());
        }
    }

    /**
     * Action when clicked on export button. Export the current workspace as an
     * image. PNG format.
     *
     * @param e
     */
    @FXML
    protected void exportButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            // Research document with workspace
            Document d = mainController.getDocument(w);

            // export document as image
            if (d != null) {
                try {
                    d.export();
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // Action when clicked on resize button. Resize the current workspace. an image.

    @FXML
    protected void resizeButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {

            ResizeDialog dialog = new ResizeDialog(w);

            Optional<Rectangle> result = dialog.showAndWait();

            if (result.isPresent()) {

                w.resizeCanvas((int) result.get().getWidth(),
                        (int) result.get().getHeight(),
                        (int) result.get().getX(),
                        (int) result.get().getY());
            }
        }
    }
}

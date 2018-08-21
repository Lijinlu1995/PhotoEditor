package myApp.controller;

import javafx.scene.shape.Circle;
import myApp.model.base.Constants;
import myApp.model.base.Document;
import myApp.model.base.Utils;
import myApp.model.base.WelcomeInvite;
import myApp.model.layer.CanvasN;
import myApp.model.tool.selection.CircleSelection;
import myApp.model.tool.selection.Selection;
import myApp.model.tool.enums;
import myApp.model.workspace.Workspace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.layout.StackPane;

/**
 * Main Controller for control action of main
 * includes a welcome page and initialize
 */
public class MainController implements Initializable {

    // Stage from main
    private Stage stage;
    private static enums.State state;

    // List of documents
    private ArrayList<Document> documents;

    // Welcome page
    private StackPane welcomeTab;

    // Controllers
    @FXML
    private MenuBarController toolbarController;

    @FXML
    private ToolBarController toolboxController;

    @FXML
    private LayerController rightController;

    // Main pane
    @FXML
    private AnchorPane mainAnchorPane;

    // Contains all workspace (tab)
    @FXML
    private TabPane workspaces;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Document list
        documents = new ArrayList<>();

        // Create a welcome panel
        welcomeTab = new StackPane();
        welcomeTab.setAlignment(Pos.CENTER);

        // Create a VBox to contain elements
        VBox welcomeContainer = new VBox();
        welcomeContainer.setPrefWidth(600);
        welcomeContainer.setBackground(new Background(
                new BackgroundFill(Color.web("#cdcdcd"),
                        CornerRadii.EMPTY, Insets.EMPTY)));
        welcomeContainer.setPadding(new Insets(20));
        welcomeContainer.setAlignment(Pos.CENTER);
        welcomeContainer.setSpacing(30);

        // Grid for multiple panels
        GridPane welcomeGrid = new GridPane();

        // Button for new document invite
        Button newButtonInvite = new Button();
        newButtonInvite.getStyleClass().add("new-document-button");
        newButtonInvite.setOnAction(e -> {
            toolbarController.newButtonAction(e);
        });
        WelcomeInvite newInvite = new WelcomeInvite(
                new Label("Create a new document."), newButtonInvite);

        // Button for open document invite
        Button openButtonInvite = new Button();
        openButtonInvite.getStyleClass().add("open-document-button");
        openButtonInvite.setOnAction(e -> {
            toolbarController.openButtonAction(e);
        });
        WelcomeInvite openInvite = new WelcomeInvite(
                new Label("Open a MYPE document."), openButtonInvite);

        // Add invites
        welcomeGrid.add(newInvite, 0, 0);
        welcomeGrid.add(openInvite, 1, 0);

        // Set visual parameters
        welcomeGrid.setHgap(15);
        welcomeGrid.setVgap(20);
        welcomeGrid.setMaxSize(460, 460);

        // Add grid to the container
        welcomeContainer.getChildren().add(welcomeGrid);
        welcomeTab.getChildren().add(welcomeContainer);

        showWelcome();

        // Register scroll event for zoom
        workspaces.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                Workspace workspace = getCurrentWorkspace();
                if (workspace != null) {
                    if (event.isControlDown()) {
                        if (event.getDeltaY() > 0) {
                            workspace.zoom(1.05);
                        } else {
                            workspace.zoom(0.95);
                        }
                    }
                }
            }
        });

        EventHandler dragEventHandler = new EventHandler<MouseEvent>() {
            private double x;
            private double y;

            @Override
            public void handle(MouseEvent event) {
                Workspace workspace = getCurrentWorkspace();
                if (workspace != null) {
                    if (event.isShiftDown()) {
                        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                            x = event.getX();
                            y = event.getY();
                        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                            workspace.move(event.getX() - x, event.getY() - y);
                            x = event.getX();
                            y = event.getY();
                        }
                        event.consume();
                    } else {
                        x = event.getX();
                        y = event.getY();
                    }
                }
            }
        };
        workspaces.addEventFilter(MouseEvent.ANY, dragEventHandler);
        workspaces.addEventHandler(MouseEvent.ANY, dragEventHandler);

        // Tab changed action
        workspaces.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
            Workspace w = getCurrentWorkspace();
            if (w != null) {
                rightController.clearLayerBox();
                rightController.addLayerController(w.getWorkspaceController());

                rightController.clearHistoryBox();
                rightController.addHistoryController(w.getHistoryList());

                w.setCurrentTool(null);
            } // Suppress tab
            else {

                // Get workspace
                w = (Workspace) t.getContent();

                // Research document with workspace
                Document d = getDocument(w);
                documents.remove(d);

                // Clear
                rightController.clearLayerBox();
                rightController.clearHistoryBox();
            }
            toolboxController.clearSelectedButtons();
        });

        //Support of the shortcut
        mainAnchorPane.setOnKeyPressed(keyEvent -> {
            Workspace w = getCurrentWorkspace();

            if (Constants.ESCAPE.match(keyEvent)) {
                // ************* ESCAPE *************
                // Disable current tool
                if (w != null) {
                    w.setCurrentTool(null);
                    toolboxController.clearSelectedButtons();
                    w.getLayerTool().setCursor(javafx.scene.Cursor.DEFAULT);
                }

            } else if (Constants.DELETE.match(keyEvent)) {
                // ************* DELETE *************

                if (w != null && w.getLayerTool() != null &&
                        w.getCurrentTool() instanceof Selection) {

                    // Get the selection
                    Selection selection = (Selection) getCurrentWorkspace()
                            .getCurrentTool();
                    Rectangle rec = selection.getRectangle();

                    for (Node n : w.getCurrentLayers()) {
                        if (n instanceof CanvasN) {

                            try {
                                CanvasN canvas = (CanvasN) n;

                                GraphicsContext gc = canvas.getGraphicsContext2D();
                                gc.setTransform(new Affine(
                                        canvas.localToParentTransformProperty()
                                                .get().createInverse()));
                                gc.clearRect(rec.getBoundsInParent().getMinX(),
                                        rec.getBoundsInParent().getMinY(),
                                        rec.getWidth(), rec.getHeight());
                            } catch (NonInvertibleTransformException ex) {

                                Logger.getLogger(MainController.class.getName())
                                        .log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    w.notifyHistory();
                } else if (w != null && w.getLayerTool() != null &&
                        w.getCurrentTool() instanceof CircleSelection) {

                    // Get the selection
                    CircleSelection selection = (CircleSelection) getCurrentWorkspace()
                            .getCurrentTool();
                    Circle circle = selection.getCircle();

                    for (Node n : w.getCurrentLayers()) {
                        if (n instanceof CanvasN) {

                            try {
                                CanvasN canvas = (CanvasN) n;

                                GraphicsContext gc = canvas.getGraphicsContext2D();
                                gc.setTransform(new Affine(
                                        canvas.localToParentTransformProperty()
                                                .get().createInverse()));
                               /* gc.clearRect(circle.getBoundsInParent().getMinX(),
                                        circle.getBoundsInParent().getMinY(),
                                        circle.getCenterX(), circle.getCenterY());*/
                                gc.setFill(Color.TRANSPARENT);
                                gc.fillOval(circle.getBoundsInParent().getMinX(),
                                        circle.getBoundsInParent().getMinY(),2*circle.getRadius(),2*circle.getRadius());
                            } catch (NonInvertibleTransformException ex) {

                                Logger.getLogger(MainController.class.getName())
                                        .log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    w.notifyHistory();
                }else {
                    // Drop the current selected layers
                    w.getCurrentLayers().forEach(n -> w.removeLayer(n));
                }

            } else if (w != null && Constants.CTRL_Z.match(keyEvent)) {
                // ************* CTRL + Z *************
                w.getHistory().undo();
            } else if (w != null && Constants.CTRL_Y.match(keyEvent)) {
                // ************* CTRL + Y *************
                w.getHistory().redo();
            } else if (Constants.CTRL_C.match(keyEvent)) {
                // ************* CTRL + C *************

                // In case there is a selection
                if (w != null && w.getLayerTool() != null &&
                        w.getCurrentTool() instanceof Selection) {

                    // Get the selection
                    Selection selection = (Selection) getCurrentWorkspace().getCurrentTool();
                    int selectionWidth = (int) (selection.getRectangle().getWidth());
                    int selectionHeight = (int) (selection.getRectangle().getHeight());

                    // Prepare the canvas to save the selection
                    CanvasN canvas = new CanvasN(getCurrentWorkspace()
                            .width(), getCurrentWorkspace().height());
                    SnapshotParameters param = new SnapshotParameters();
                    param.setFill(Color.TRANSPARENT);

                    PixelWriter pixelWriter = canvas.getGraphicsContext2D()
                            .getPixelWriter();

                    BufferedImage image = new BufferedImage(selectionWidth,
                            selectionHeight, BufferedImage.TYPE_INT_ARGB);

                    // Snapshot each node selected
                    for (Node n : w.getCurrentLayers()) {

                        double posXWCoord = selection.getRectangle()
                                .getBoundsInParent().getMinX();
                        double posYWCoord = selection.getRectangle()
                                .getBoundsInParent().getMinY();

                        param.setViewport(new Rectangle2D(
                                posXWCoord,
                                posYWCoord,
                                selectionWidth,
                                selectionHeight));

                        BufferedImage newImage = SwingFXUtils.fromFXImage(
                                n.snapshot(param, null), null);

                        Graphics2D graphics = (Graphics2D) image.getGraphics();

                        graphics.setBackground(java.awt.Color.WHITE);
                        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER, 1f));

                        graphics.drawImage(newImage, 0, 0, null);
                    }

                    // Get a pixel reader
                    PixelReader pixelReader = SwingFXUtils.toFXImage(image, null)
                            .getPixelReader();

                    // Write the color of every pixel
                    for (int y = 0; y < selectionHeight; ++y) {
                        for (int x = 0; x < selectionWidth; ++x) {
                            Color c = pixelReader.getColor(x, y);
                            pixelWriter.setColor(
                                    x + (int) Math.round(selection.getRectangle()
                                            .getBoundsInParent().getMinX()),
                                    y + (int) Math.round(selection.getRectangle()
                                            .getBoundsInParent().getMinY()), c);
                        }
                    }

                    // Save the canvas to clipboard
                    saveNodesToClipboard(Arrays.asList(canvas));

                    // No selection then copy the current layers
                }else if (w != null && w.getLayerTool() != null &&
                        w.getCurrentTool() instanceof CircleSelection) {
                    // Get the selection
                    CircleSelection selection = (CircleSelection) getCurrentWorkspace().getCurrentTool();
                    int selectionX = (int) (selection.getCircle().getCenterX());
                    int selectionY = (int) (selection.getCircle().getCenterY());
                    int radius = (int) (selection.getCircle().getRadius());
                    // Prepare the canvas to save the selection
                    CanvasN canvas = new CanvasN(getCurrentWorkspace()
                            .width(), getCurrentWorkspace().height());
                    SnapshotParameters param = new SnapshotParameters();
                    param.setFill(Color.TRANSPARENT);

                    PixelWriter pixelWriter = canvas.getGraphicsContext2D()
                            .getPixelWriter();

                    BufferedImage image = new BufferedImage(selectionX,
                            selectionY, BufferedImage.TYPE_INT_ARGB);

                    // Snapshot each node selected
                    for (Node n : w.getCurrentLayers()) {

                        double posXWCoord = selection.getCircle()
                                .getBoundsInParent().getMinX();
                        double posYWCoord = selection.getCircle()
                                .getBoundsInParent().getMinY();

                        param.setViewport(new Rectangle2D(
                                posXWCoord,
                                posYWCoord,
                                selectionX,
                                selectionY));

                        BufferedImage newImage = SwingFXUtils.fromFXImage(
                                n.snapshot(param, null), null);

                        Graphics2D graphics = (Graphics2D) image.getGraphics();

                        graphics.setBackground(java.awt.Color.WHITE);
                        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER, 1f));

                        graphics.drawImage(newImage, 0, 0, null);
                    }

                    // Get a pixel reader
                    PixelReader pixelReader = SwingFXUtils.toFXImage(image, null)
                            .getPixelReader();

                    // Write the color of every pixel
                    for (int y = 0; y < selectionX; ++y) {
                        for (int x = 0; x < selectionY; ++x) {
                            Color c = pixelReader.getColor(x, y);
                            pixelWriter.setColor(
                                    x + (int) Math.round(selection.getCircle()
                                            .getBoundsInParent().getMinX()),
                                    y + (int) Math.round(selection.getCircle()
                                            .getBoundsInParent().getMinY()), c);
                        }
                    }

                    // Save the canvas to clipboard
                    saveNodesToClipboard(Arrays.asList(canvas));

                    // No selection then copy the current layers
                }else if (w != null && w.getCurrentLayers() != null) {
                    saveNodesToClipboard(w.getCurrentLayers());
                }

            } else if (w != null && Constants.CTRL_V.match(keyEvent)) {
                // ************* CTRL + V *************
                for (Node n : getNodesFromClipboard()) {
                    w.addLayer(n);
                }
            } else if (Constants.CTRL_N.match(keyEvent)) {
                // ************* CTRL + N *************
                toolbarController.newButtonAction(null);
            } else if (Constants.CTRL_O.match(keyEvent)) {
                // ************* CTRL + O *************
                toolbarController.openButtonAction(null);
            } else if (Constants.CTRL_S.match(keyEvent)) {
                // ************* CTRL + S *************
                toolbarController.saveButtonAction(null);
            } else if (Constants.CTRL_E.match(keyEvent)) {
                // ************* CTRL + E *************
                toolbarController.exportButtonAction(null);
            } else if (Constants.CTRL_R.match(keyEvent)) {
                // ************* CTRL + R *************
                toolbarController.resizeButtonAction(null);
            }
        });

        toolbarController.init(this);
        toolboxController.init(this, toolbarController);
        rightController.init(this);
    }

    //Copy a list of nodes in the clipboard

    private void saveNodesToClipboard(List<Node> nodes) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent cc = new ClipboardContent();

        // Serialize each node
        try {
            cc.putString(Utils.serializeNodeList(nodes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        clipboard.setContent(cc);
    }

    //Retrieve a list of nodes from the clipboard

    private List<Node> getNodesFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String serializedObject = clipboard.getString();

        // Deserialize the clipboard's content
        try {
            return Utils.deserializeNodeList(serializedObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Create a new tab

    public void createTab(Document document) {
        Workspace w = document.workspace();

        // Create tab
        Tab tab = new Tab(document.name(), w);
        workspaces.getTabs().add(tab);
        workspaces.getSelectionModel().select(tab);
    }

    //Show and set anchors for the welcome panel.
    private void showWelcome() {
        mainAnchorPane.getChildren().add(welcomeTab);
        AnchorPane.setTopAnchor(welcomeTab, 93.0);
        AnchorPane.setRightAnchor(welcomeTab, 164.0);
        AnchorPane.setBottomAnchor(welcomeTab, 0.0);
        AnchorPane.setLeftAnchor(welcomeTab, 0.0);
    }

    //Hide the welcome panel.

    public void hideWelcome() {
        mainAnchorPane.getChildren().remove(welcomeTab);
    }

    void setState(enums.State state) {
        MainController.state = state;
    }
    //Add document that contains workspace

    public void addDocument(Document document) {
        documents.add(document);
    }

    //Get the current workspace displayed
    public Workspace getCurrentWorkspace() {
        if (workspaces.getTabs().size() > 0) {
            return (Workspace) workspaces.getSelectionModel().getSelectedItem()
                    .getContent();
        }

        return null;
    }

    // Get document that contain the workspace
    public Document getDocument(Workspace w) {
        for (Document d : documents) {
            if (d.workspace() == w) {
                return d;
            }
        }

        return null;
    }

    //Get current tab

    public Tab getCurrentTab() {
        return workspaces.getSelectionModel().getSelectedItem();
    }

    // Get the main pane

    public AnchorPane getMainPane() {
        return mainAnchorPane;
    }

    //Set main stage

    public void setStage(Stage s) {
        this.stage = s;
    }

    //Get main stage

    public Stage getStage() {
        return stage;
    }
}

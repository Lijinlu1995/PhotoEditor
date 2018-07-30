package myApp.workspace;

import myApp.tool.Tool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;

import javafx.scene.transform.NonInvertibleTransformException;

public class Workspace extends StackPane implements Serializable {

    // Workspace that displays layers
    private AnchorPane workspace;

    // Layer for object's tool
    private AnchorPane layerTools;

    // Clip of workspace
    private Rectangle clip;

    // Size of workspace
    private int height;
    private int width;

    // Contains layers
    private LayerList<Node> layerList;
    private VBox layersController;

    // Current selected tool
    private Tool currentTool;

    private History history;

    private HistoryNotifier historyNotifier;

    private ListView historyListView;

    /**
     * Constructor for a new instance of Workspace. The Workspace extends a Pane
     * which represents the working area of the document. It sets its initial
     * position at the center of the containing pane.
     *
     * @param width the width of the Workspace (according to the document needs)
     * @param height the height of the Workspace (according to the document needs)
     */
    public Workspace(int width, int height) {
        init(width, height);
    }

    public void init(int width, int height) {
        workspace = new AnchorPane();
        getChildren().add(workspace);

        layerTools = new AnchorPane();
        getChildren().add(layerTools);

        historyNotifier = new HistoryNotifier();

        clip = new Rectangle(width, height);

        // Define the canvas size
        resizeCanvas(width, height, 0, 0);

        // Set id for CSS styling
        setId("workspaceAnchorPane");
        workspace.setId("workspacePane");

        layerList = new LayerList<>(workspace.getChildren());

        currentTool = null;

        // Add a mouse event to manage the current tool actions
        layerTools.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (currentTool != null) {

                    // Get mouse position
                    Point3D p = new Point3D(event.getX(), event.getY(), 0);

                    if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                        currentTool.mousePressed(p.getX(), p.getY());
                    } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                        currentTool.mouseDragged(p.getX(), p.getY());
                    } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                        currentTool.mouseReleased(p.getX(), p.getY());
                    } else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
                        currentTool.mouseMoved(p.getX(), p.getY());
                    }
                }
            }
        });

        // History
        this.history = new History(this);
        historyNotifier.addObserver(history);

        this.historyListView = new ListView<WritableImage>();

        // Load the ListView with the thumbnails from history
        historyListView.setItems(history.getImagesHistory());

        // When selecting an element in the ListView, go to the corresponding state in history
        historyListView.setOnMouseClicked(e -> {
            getHistory().restoreToIndex(historyListView.getSelectionModel()
                    .getSelectedIndex());
        });

        // Display an ImageView in the ListView
        historyListView.setCellFactory(listView -> new ListCell<Image>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(Image image, boolean empty) {
                super.updateItem(image, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    imageView.setFitHeight(image.getHeight());
                    imageView.setFitWidth(image.getWidth());
                    imageView.setImage(image);
                    setGraphic(imageView);
                }
            }
        });
        // Once the workspace is loaded, save it's state
        notifyHistory();
    }

    @Override
    public void layoutChildren() {
        super.layoutChildren();

        // Center the clip
        clip.setLayoutX(Math.round((getWidth() - width) / 2));
        clip.setLayoutY(Math.round((getHeight() - height) / 2));
        setClip(clip);
    }

    @Override
    public WritableImage snapshot(SnapshotParameters params, WritableImage image) {
        if (params == null) {
            params = new SnapshotParameters();
        }

        params.setFill(Color.TRANSPARENT);
        params.setViewport(new Rectangle2D(clip.getLayoutX(), clip.getLayoutY(),
                clip.getWidth(), clip.getHeight()));
        try {
            // If there is already a transformation applied to the transform, concatenate
            Transform transform = params.getTransform();
            Transform newTransform = clip.getLocalToParentTransform().createInverse();
            if (transform != null) {
                newTransform = transform.createConcatenation(newTransform);
            }
            params.setTransform(newTransform);
        } catch (NonInvertibleTransformException ex) {
            Logger.getLogger(Workspace.class.getName()).log(Level.SEVERE, null, ex);
        }
        params.setViewport(new Rectangle2D(
                clip.getBoundsInLocal().getMinX(), clip.getBoundsInLocal()
                        .getMinY(), clip.getWidth(), clip.getHeight()));

        return workspace.snapshot(params, image);
    }

    /**
     * @param node
     */
    public void addLayer(Node node) {
        workspace.getChildren().add(node);
        layerList.clearSelection();
        layerList.selectTopLayer();

        //layerList.getItems().add(node);
        //layerList.getSelectionModel().clearSelection();
        //layerList.getSelectionModel().selectLast();
    }

    /**
     * @param node
     */
    public void removeLayer(Node node) {
        workspace.getChildren().remove(node);
        layerList.selectTopLayer();
        historyNotifier.notifyHistory();
    }

    public List<Node> getCurrentLayers() {
        return layerList.getSelectedItems();
    }

    /**
     * @return
     */
    public List<Node> getLayers() {
        return workspace.getChildren();
    }

    /**
     * @param factor
     */
    public void zoom(double factor) {
        workspace.setScaleX(workspace.getScaleX() * factor);
        workspace.setScaleY(workspace.getScaleY() * factor);
        layerTools.setScaleX(layerTools.getScaleX() * factor);
        layerTools.setScaleY(layerTools.getScaleY() * factor);
        clip.setScaleX(clip.getScaleX() * factor);
        clip.setScaleY(clip.getScaleY() * factor);
    }

    /**
     * Translates the Workspace Pane container by a (x, y) translation vector.
     *
     * @param x the x coordinate of the translation
     * @param y the y coordinate of the translation
     */
    public void move(double x, double y) {
        workspace.setTranslateX(workspace.getTranslateX() + x);
        workspace.setTranslateY(workspace.getTranslateY() + y);
        layerTools.setTranslateX(layerTools.getTranslateX() + x);
        layerTools.setTranslateY(layerTools.getTranslateY() + y);
        clip.setTranslateX(clip.getTranslateX() + x);
        clip.setTranslateY(clip.getTranslateY() + y);
    }

    public void resizeCanvas(int width, int height, int offsetX, int offsetY) {
        this.width = width;
        this.height = height;

        // Set preferredSize
        workspace.setPrefSize(width, height);
        workspace.setMaxSize(width, height);
        workspace.setMinSize(width, height);

        // Stack the layer tool on workspace
        layerTools.setPrefSize(width, height);
        layerTools.setMaxSize(width, height);
        layerTools.setMinSize(width, height);

        clip.setWidth(width);
        clip.setHeight(height);

        for (Node n : workspace.getChildren()) {
            n.setTranslateX(n.getTranslateX() + offsetX);
            n.setTranslateY(n.getTranslateY() + offsetY);
        }
    }

    public VBox getWorkspaceController() {
        if (layersController == null) {

            // Create the controller instance
            layersController = new VBox();

            // Add the LayerList
            layersController.getChildren().add(layerList);
        }
        return layersController;
    }

    public void setCurrentTool(Tool tool) {
        layerTools.getChildren().clear();
        this.currentTool = tool;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double getWorkspaceScaleX() {
        return workspace.getScaleX();
    }

    public double getWorkspaceScaleY() {
        return workspace.getScaleY();
    }

    public AnchorPane getLayerTool() {
        return layerTools;
    }

    /**
     * Adds given items to the list of selected layers. If the target list doesn't
     * contain any of the given elements, this particular element won't be added nor
     * selected.
     *
     * @param layers the layers to add to the selection
     */
    public void selectLayers(Node... layers) {
        layerList.selectLayers(layers);
    }

    /**
     * Add the given layer to the selection. If the layer is not present in the
     * target list, it will not be added nor selected.
     *
     * @param layer to add to the selection
     */
    public void selectLayer(Node layer) {
        layerList.selectLayer(layer);
    }

    /**
     * Adds a specific layer by index to the selection. The method won't do anything
     * if the index is out of bounds.
     *
     * @param i the index of the layer to add to the selection
     */
    public void selectLayerByIndex(int i) {
        layerList.selectLayerByIndex(i);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        // Write size of workspace
        s.writeInt(height);
        s.writeInt(width);

        // Number of layer
        s.writeInt(workspace.getChildren().size());

        for (Object n : workspace.getChildren()) {
            if (Serializable.class.isInstance(n)) {
                s.writeObject(n);
            }
        }

    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        int h = s.readInt();
        int w = s.readInt();

        init(w, h);

        int nbLayers = s.readInt();

        for (int i = 0; i < nbLayers; ++i) {
            addLayer((Node) s.readObject());
        }
    }

    public History getHistory() {
        return history;
    }

    public void notifyHistory() {
        historyNotifier.notifyHistory();
    }

    public ListView getHistoryList() {
        return historyListView;
    }
}

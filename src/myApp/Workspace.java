package myApp;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import myApp.model.layer.CanvasN;
import myApp.model.layer.NodeN;
import myApp.model.tool.Tool;
import myApp.model.workspace.History;
import myApp.model.workspace.HistoryNotifier;
import myApp.model.workspace.LayerList;
import myApp.model.workspace.LayerListable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Workspace extends StackPane implements Serializable {
    // Workspace that displays layers
    private AnchorPane workspace;
    // Contains layers
    private LayerList<Node> layerList;

    public Workspace(int width , int height ) {
        // Create the LayerList to manage the drawing area children
        layerList = new LayerList <>( workspace . getChildren ());
        /* ... */
    }
    /* ... */
    //Return selected layers
    public List<Node> getCurrentLayers() {
        // Let the LayerList handle the request
        return layerList.getSelectedItems();
    }

    public List<Node> getLayers() {
        return workspace.getChildren();
    }
}

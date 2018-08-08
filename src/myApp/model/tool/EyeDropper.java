package myApp.model.tool;

import myApp.model.layer.CanvasN;
import myApp.model.layer.ImageN;
import myApp.model.layer.TextN;
import myApp.model.workspace.Workspace;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/*  EyeDropper objects read color information from GEMMSCanvas, GEMMSImage and
 * GEMMSText objects, and when a Color is available, sets the current ColorSet
 * instance value to this new Color.
 */
public class EyeDropper extends AbstractTool {

    // Picked color
    private Paint pickedColor;

    public EyeDropper(Workspace workspace) {
        super(workspace);
        this.pickedColor = null;
        workspace.getLayerTool().setCursor(Cursor.CROSSHAIR);
    }

    private Node getTopLayer() {
        List<Node> layers = workspace.getCurrentLayers();
        if (layers.isEmpty()) {
            return null;
        } else {
            return layers.get(layers.size() - 1);
        }
    }

    //Pick the Color from the top layer, at the (x, y) position.

    private Paint pickColor(int x, int y) {
        // Get the top layer selected
        Node layer = getTopLayer();

        // If it is a GEMMSText, simply return the color of the Text
        if (TextN.class.isInstance(layer)) {
            return ((TextN) layer).getFill();
        } else if (CanvasN.class.isInstance(layer) ||
                ImageN.class.isInstance(layer)) {

            // Write a snapshot of the canvas or image to be able to look up pixels
            WritableImage wi = new WritableImage(
                    (int) layer.getBoundsInParent().getWidth(),
                    (int) layer.getBoundsInParent().getHeight());
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            WritableImage snapshot = layer.snapshot(sp, wi);

            // Map to the beginning of the image
            double pickX = x - layer.getBoundsInParent().getMinX();
            double pickY = y - layer.getBoundsInParent().getMinY();

            // If inside, look up pixel colors
            if (pickX >= 0 && pickX <= wi.getWidth() && pickY >= 0 &&
                    pickY <= wi.getHeight()) {
                PixelReader pr = snapshot.getPixelReader();
                return pr.getColor((int) pickX, (int) pickY);
            }
        }

        // else return null
        return null;
    }

    //On mouse pressed, the current picked color is updated.

    @Override
    public void mousePressed(double x, double y) {
        pickedColor = pickColor((int) x, (int) y);
    }

    //On mouse dragged, the current picked color is updated.

    @Override
    public void mouseDragged(double x, double y) {
        pickedColor = pickColor((int) x, (int) y);
    }

    //On mouse released, the current picked color is updated and the ColorSet Color instance is updated.

    @Override
    public void mouseReleased(double x, double y) {
        pickedColor = pickColor((int) x, (int) y);
        if (pickedColor != null && Color.class.isInstance(pickedColor)) {
            ColorSet.getInstance().setColor((Color) pickedColor);
        }
    }

    @Override
    public void mouseMoved(double x, double y) {
    }

}

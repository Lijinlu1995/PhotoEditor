package myApp.tool;

import myApp.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;

import java.util.List;

public class Resize extends AbstractTool {

    //The old x coordonate
    private double mouseX;
    //The factor of resizing
    private final double FACTOR = 0.001;
    //The direction of resizing (left -> right)
    private final int DIRECTION = 1;
    //The list of selected Nodes
    private List<Node> layers;

    /**
     * Constructor of Resize Tool
     *
     * @param w workspace to crop
     */
    public Resize(Workspace w) {
        super(w);
        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        layers = workspace.getCurrentLayers();
        workspace.getLayerTool().setCursor(Cursor.NE_RESIZE);
    }

    @Override
    public void mouseDragged(double x, double y) {

        double newX = x - mouseX;

        double newScale;
        for (Node node : layers) {
            newScale = node.getScaleX() + (newX * FACTOR) * DIRECTION;
            if (newScale >= 0) {
                node.setScaleX(newScale);
                node.setScaleY(newScale);
                node.setScaleZ(newScale);
            }
        }

        mouseX = x;
    }

    @Override
    public void mouseReleased(double x, double y) {
        notifier.notifyHistory();
        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void mouseMoved(double x, double y) {
    }
}

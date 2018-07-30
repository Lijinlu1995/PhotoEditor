package myApp.tool;

import myApp.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.List;

public class Drag extends AbstractTool {

    //The old coordinates
    private double lastX;
    private double lastY;
    //The list of selected Nodes
    private List<Node> layers;
    //Boolean to know if the alignement is active or not
    private boolean isAlignementActive;
    //An AnchorPane to draw line on top of workspace's layers
    private AnchorPane anchorPane;
    //Workspace dimension
    private double workspaceHeight;
    private double workspaceWidth;
    //Delta for automatic alignement
    private final double DELTA = 30;

    public Drag(Workspace w) {
        super(w);
        this.isAlignementActive = false;
        this.anchorPane = workspace.getLayerTool();

        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void mousePressed(double x, double y) {

        lastX = x;
        lastY = y;
        layers = workspace.getCurrentLayers();
        workspaceWidth = workspace.width();
        workspaceHeight = workspace.height();
        workspace.getLayerTool().setCursor(Cursor.MOVE);
    }

    @Override
    public void mouseDragged(double x, double y) {
        if (isAlignementActive && layers.size() == 1) { //automatic alignement only on one Node at the time
            dragWithAlignement(x, y);
        } else {
            dragWithoutAlignement(x, y);
        }
    }

    private void dragWithoutAlignement(double x, double y) {

        //offsets to change coordonates
        double offsetX = x - lastX;
        double offsetY = y - lastY;

        for (Node n : layers) {
            n.setTranslateX(n.getTranslateX() + offsetX);
            n.setTranslateY(n.getTranslateY() + offsetY);
        }

        lastX = x;
        lastY = y;
    }

    private void dragWithAlignement(double x, double y) {

        //offsets to change coordonates
        double offsetX = x - lastX;
        double offsetY = y - lastY;

        double nodeCenterX;
        double nodeCenterY;
        boolean isAlignOnX;
        boolean isAlignOnY;
        for (Node n : layers) {
            nodeCenterX = n.getBoundsInParent().getWidth() / 2;
            nodeCenterY = n.getBoundsInParent().getHeight() / 2;
            isAlignOnX = Math.abs(x - workspaceWidth / 2) < DELTA;
            isAlignOnY = Math.abs(y - workspaceHeight / 2) < DELTA;

            double toMoveX;
            double toMoveY;

            // Find by how much we need to move the node
            if (isAlignOnX) {
                toMoveX = workspaceWidth / 2 - (n.getBoundsInParent().getMinX() + nodeCenterX);
            } else {
                toMoveX = offsetX;
            }

            if (isAlignOnY) {
                toMoveY = workspaceHeight / 2 - (n.getBoundsInParent().getMinY() + nodeCenterY);
            } else {
                toMoveY = offsetY;
            }

            n.setTranslateX(n.getTranslateX() + toMoveX);
            n.setTranslateY(n.getTranslateY() + toMoveY);
        }
        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
        notifier.notifyHistory();
    }

    public void turnAlignementOnOff() {
        isAlignementActive = !isAlignementActive;
        if (isAlignementActive) {
            printAlignement();
        } else {
            anchorPane.getChildren().clear();
        }
    }

    private void printAlignement() {
        Canvas alignement = new Canvas(workspace.width(), workspace.height());
        GraphicsContext gc = alignement.getGraphicsContext2D();
        gc.setStroke(Color.GREEN);
        //Lignes principales (Axes)
        gc.setLineWidth(2);
        gc.strokeLine(workspace.width() / 2, 0, workspace.width() / 2, workspace.height());
        gc.strokeLine(0, workspace.height() / 2, workspace.width(), workspace.height() / 2);

        anchorPane.getChildren().add(alignement);
    }

    public boolean isAlignementActive() {
        return isAlignementActive;
    }

    @Override
    public void mouseMoved(double x, double y) {
    }
}

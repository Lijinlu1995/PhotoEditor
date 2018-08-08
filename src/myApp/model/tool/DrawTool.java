package myApp.model.tool;

import myApp.model.tool.settings.SizeConfigurableTool;
import myApp.model.workspace.Workspace;
import java.util.List;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract class DrawTool extends AbstractTool implements SizeConfigurableTool {

    // The size of the tool [px]
    protected int size;

    // Last registered coordinates of the tool
    protected int x;
    protected int y;

    // Fix to draw even if the pressed event wasn't registered
    protected boolean started = false;

    // Context to draw on
    Circle blackCircleCursor;
    Circle whiteCircleCursor;

    boolean hasCursor = false;

    public DrawTool(Workspace workspace, int size) {
        super(workspace);
        this.size = size;
        workspace.getLayerTool().setCursor(Cursor.NONE);

        // Create fake cursors
        blackCircleCursor = createCursor(Color.web("#333"));
        whiteCircleCursor = createCursor(Color.web("#ccc"));
    }

    private Circle createCursor(Color color) {
        Circle c = new Circle(size / 2);
        c.setStroke(color);
        c.setStrokeWidth(2);
        c.setFill(Color.TRANSPARENT);
        return c;
    }

    public void line(int x0, int y0, int x1, int y1, GraphicsContext gc, Canvas canvas) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2;

        while (true) {

            Point3D point = PositionMapper.convert(canvas, x0, y0, 0);

            drawPixel((int) point.getX(), (int) point.getY(), gc);
            if (x0 == x1 && y0 == y1) {
                break;
            }
            e2 = 2 * err;
            if (e2 > dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    @Override
    public void mousePressed(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;

        started = true;
    }

    @Override
    public void mouseDragged(double x, double y) {

        mouseMoved(x, y);

        if (started) {
            // Get the selected layers of the workspace
            List<Node> layers = workspace.getCurrentLayers();

            // For each node, draw on it
            for (Node node : layers) {
                if (Canvas.class.isInstance(node)) {
                    Canvas canvas = (Canvas) node;
                    GraphicsContext gc = canvas.getGraphicsContext2D();

                    line((int) this.x, (int) this.y, (int) x, (int) y, gc, canvas);

                }

            }
        } else {
            started = true;
        }

        // Update the position
        this.x = (int) x;
        this.y = (int) y;
    }

    @Override
    public void mouseReleased(double x, double y) {
        // Get the selected layers of the workspace
        List<Node> layers = workspace.getCurrentLayers();

        // For each node, draw on it
        for (Node node : layers) {
            if (Canvas.class.isInstance(node)) {
                Canvas canvas = (Canvas) node;
                GraphicsContext gc = canvas.getGraphicsContext2D();

                Point3D point = PositionMapper.convert(canvas, x, y, 0);

                drawPixel((int) point.getX(), (int) point.getY(), gc);

            }
        }
        notifier.notifyHistory();
    }

    @Override
    public void mouseMoved(double x, double y) {

        whiteCircleCursor.setTranslateX(x - 1);
        whiteCircleCursor.setTranslateY(y - 1);
        blackCircleCursor.setTranslateX(x);
        blackCircleCursor.setTranslateY(y);

        if (!hasCursor) {
            workspace.getLayerTool().getChildren().add(whiteCircleCursor);
            workspace.getLayerTool().getChildren().add(blackCircleCursor);
            hasCursor = true;
        }
    }

    @Override
    public void setSize(int size) {
        this.size = size;
        blackCircleCursor.setRadius(size / 2);
        whiteCircleCursor.setRadius(size / 2);
    }

    @Override
    public int getSize() {
        return size;
    }

    protected abstract void drawPixel(int x, int y, GraphicsContext gc);
}

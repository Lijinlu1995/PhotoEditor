package myApp.tool;

import myApp.tool.settings.SizeConfigurableTool;
import myApp.workspace.Workspace;
import java.util.List;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * <h1>LineTool</h1>
 *
 * The LineTool class represents objects that need to take actions following the
 * mouse such as a paint brush, or an eraser. The LineTool is in charge of "drawing"
 * lines (using the Bresenham algorithm), calling the drawPixel method each time it
 * should construct a part of the line.
 */
public abstract class LineTool extends AbstractTool implements SizeConfigurableTool {

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

    /**
     * Constructor.
     *
     * @param workspace the Workspace to work on
     * @param size the size of the tool in pixels
     */
    public LineTool(Workspace workspace, int size) {
        super(workspace);
        this.size = size;
        workspace.getLayerTool().setCursor(Cursor.NONE);

        // Create fake cursors
        blackCircleCursor = createCursor(Color.web("#333"));
        whiteCircleCursor = createCursor(Color.web("#ccc"));
    }

    /**
     * Create a Circle to represent a cursor
     *
     * @param color the color of the cursor
     * @return a new Circle
     */
    private Circle createCursor(Color color) {
        Circle c = new Circle(size / 2);
        c.setStroke(color);
        c.setStrokeWidth(2);
        c.setFill(Color.TRANSPARENT);
        return c;
    }

    /**
     * The method line implements the Bresenham algorithm in order to draw smooth
     * lines on every mouse drag movement.
     *
     * This compact version of the algorithm comes from this website:
     *
     * https://de.wikipedia.org/wiki/Bresenham-Algorithmus
     *
     * on 16.05.2017
     *
     * The method calls the abstract method drawPixel for each pixel it needs to
     * apply a action on.
     *
     * @param x0 the x coordinate of the first point
     * @param y0 the y coordinate of the first point
     * @param x1 the x coordinate of the second point
     * @param y1 the y coordinate of the second point
     * @param gc the GraphicsContext from the canvas to apply the tool on
     */
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

    /**
     * Method to call on the start of the dragging motion, when the mouse is pressed
     * for the first time.
     *
     * @param x the x coordinate of the event.
     * @param y the y coordinate of the event.
     */
    @Override
    public void mousePressed(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;

        started = true;
    }

    /**
     * Method to call during the dragging motion.
     *
     * @param x the event x coordinate
     * @param y the event y coordinate
     */
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

    /**
     * Method to call at the end of the drag movement. Draws a single point.
     *
     * @param x the x coordinate of the event
     * @param y the y coordinate of the event
     */
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

    /**
     * Draw the fake cursors on mose moved
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
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

    /**
     * Set the new size of the tool
     *
     * @param size the new size
     */
    @Override
    public void setSize(int size) {
        this.size = size;
        blackCircleCursor.setRadius(size / 2);
        whiteCircleCursor.setRadius(size / 2);
    }

    /**
     * Get the size of the tool in pixels
     *
     * @return the size of the tool
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Method to define the action to apply to each painted pixel by the Bresenham
     * algorithm
     *
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @param gc the GraphicsContext of the canvas
     */
    protected abstract void drawPixel(int x, int y, GraphicsContext gc);
}

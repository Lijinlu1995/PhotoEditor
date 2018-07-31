package myApp.tool;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import myApp.workspace.Workspace;

public abstract class Shapes extends AbstractTool{

    /* Horizontal - X coordinate.*/
    private double xCoordinate;

    /* Vertical - Y coordinate.*/
    private double yCoordinate;

    public Shapes(Workspace workspace) {
        super(workspace);
    }

    /* The operation history instance which is responsible for undo and redo operations.*/
    //OperationHistory operationHistory;

    /* Draws a shape with the user choice for the stroke color and line width and then adds it to the pane.
       @param pane The pane where the shape will be added.
       @param pickColor The color of the stroke used to draw the shape.
       @param lineWidth The width od the stroke used to draw the shape.*/
    public abstract void drawShape(Pane pane, ColorPicker pickColor, Slider lineWidth);

    /* Resize a shape drawn on a pane.
       @param node The shape which is required to be resized.
       @param pane The pane which contains this shape.*/
    public abstract void resizeShape(Node node, Pane pane);

    /* Sets the cursor on the shape depending on the current operation.
       @param node The shape which is required to set its cursor.
       @param stateX A double value represents the current horizontal-x coordinate of the mouse pointer(cursor).
       @param stateY A double value represents the current vertical-y coordinate of the mouse pointer(cursor).*/
    public abstract void setCursor(Node node, double stateX, double stateY);

    /* Drags/Moves a shape on a pane.
       @param node The shape which is required to be moved/dragged.
       @param pane The pane which contains the shape.*/
    public void moveShape(Node node, Pane pane) {
        //resetMouseEvents(pane);
        EventHandler<MouseEvent> mouseHandler = mouseEvent -> {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                node.setCursor(Cursor.MOVE);
                setShapeStyleOnDrag(node);
                xCoordinate = mouseEvent.getX();
                yCoordinate = mouseEvent.getY();
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                node.setLayoutX(node.getLayoutX() + mouseEvent.getX() - xCoordinate);
                node.setLayoutY(node.getLayoutY() + mouseEvent.getY() - yCoordinate);
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                setShapeStyleOnRelease(node);
                //operationHistory.shapeDrawn(pane);
            }};
        node.setOnMousePressed(mouseHandler);
        node.setOnMouseDragged(mouseHandler);
        node.setOnMouseReleased(mouseHandler);
    }

    /* Fills a shape with a certain color.
       @param pane The pane which contains the shape.
       @param color The color which is required to fill the shape with.
       @param node The shape which is required to be filled with a certain color.*/
    public void fillShape(Pane pane, Color color, Node node) {
        //resetMouseEvents(pane);
        EventHandler<MouseEvent> mouseHandler = mouseEvent -> {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                if (node instanceof Polygon) {
                    Polygon polygon = (Polygon) node;
                    polygon.setFill(color);
                } else if (node instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle) node;
                    rectangle.setFill(color);
                } else if (node instanceof Ellipse) {
                    Ellipse ellipse = (Ellipse) node;
                    ellipse.setFill(color);
                }
                if (!(node instanceof Line)) {
                    //operationHistory.shapeDrawn(pane);
                }
            }};
        node.setOnMousePressed(mouseHandler);
    }

    /* Deletes a shape from a pane.
       @param pane The pane which contains the shape.
       @param node The shape which is required to be deleted.*/
    public void deleteShape(Pane pane, Node node) {
        //resetMouseEvents(pane);
        if (node instanceof Line || node instanceof Polygon || node instanceof Rectangle
                || node instanceof Ellipse) {
            EventHandler<MouseEvent> mouseHandler = mouseEvent -> {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    pane.getChildren().remove(node);
                    //operationHistory.shapeDrawn(pane);
                }};
            node.setOnMousePressed(mouseHandler);
        }
    }

    /* Set the stroke style of the selected shape on dragging/moving.
       @param node The shape whose stroke style will be altered on selection on dragging/moving.
    */
    void setShapeStyleOnDrag(Node node) {
        if (node instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) node;
            rectangle.setStyle("-fx-stroke-dash-offset: 15;-fx-stroke-dash-array: 12 2 4 2; -fx-stroke: darkblue; ");
        } else if (node instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) node;
            ellipse.setStyle("-fx-stroke-dash-offset: 15;-fx-stroke-dash-array: 12 2 4 2; -fx-stroke: darkblue;");
        } else if (node instanceof Polygon) {
            Polygon polygon = (Polygon) node;
            polygon.setStyle("-fx-stroke-dash-offset: 15;-fx-stroke-dash-array: 12 2 4 2; -fx-stroke: darkblue;");
        } else {
            Line line = (Line) node;
            line.setStyle("-fx-stroke-dash-offset: 15;-fx-stroke-dash-array: 12 2 4 2; -fx-stroke: darkblue;");
        }
    }

    /* Set the stroke style of the selected shape on release.
       @param node The shape whose stroke style will be altered on selection on release.*/
    void setShapeStyleOnRelease(Node node) {
        if (node instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) node;
            rectangle.setStyle("-fx-stroke-dash-offset: 0;");
        } else if (node instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) node;
            ellipse.setStyle("-fx-stroke-dash-offset: 0;");
        } else if (node instanceof Polygon) {
            Polygon polygon = (Polygon) node;
            polygon.setStyle("-fx-stroke-dash-offset: 0;");
        } else {
            Line line = (Line) node;
            line.setStyle("-fx-stroke-dash-offset: 0;");
        }
    }

    /* Resets the mouse events on a pane.
       @param pane The pane whose mouse events are required to be reset.*/
    void resetMouseEvents(Pane pane) {
        EventHandler<MouseEvent> mouseHandler = mouseEvent -> {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                // Do Nothing...
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                // Do Nothing...
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // Do Nothing...
            }};
        pane.setOnMousePressed(mouseHandler);
        pane.setOnMouseDragged(mouseHandler);
        pane.setOnMouseReleased(mouseHandler);
    }
    /*
     * Configures the cursor on the shape corresponding to the current operation.
     * @param initialX  A double value represents the horizontal-x coordinate of the shape.
     * @param initialY  A double value represents the vertical-y coordinate of the shape.
     * @param initialW  A double value represents the width of the shape.
     * @param initialH  A double value represents the height of the shape.
     * @param stateX  A double value represents the horizontal-x coordinate of the mouse pointer(cursor).
     * @param stateY  A double value represents the vertical-y coordinate of the mouse pointer(cursor).
     * @param node  The shape whose cursor wanted to be altered depending on the current operation.
     */
    void configureCursor(double initialX, double initialY,
                         double initialW, double initialH,
                         double stateX, double stateY,
                         Node node) {
        if (stateX > initialX - 3 && stateX < initialX + 3) {
            node.setCursor(Cursor.E_RESIZE);
        }
        // Left
        else if (stateX > (initialX + initialW - 3) && stateX < (initialX + initialW + 3)) {
            node.setCursor(Cursor.W_RESIZE);
        }
        // Up
        else if (stateY > initialY - 3 && stateY < initialY + 3) {
            node.setCursor(Cursor.N_RESIZE);
        }
        // Down
        else if (stateY > (initialY + initialH - 3) && stateY < (initialY + initialH + 3)) {
            node.setCursor(Cursor.S_RESIZE);
        } else {
            node.setCursor(Cursor.DEFAULT);
        }
    }
}

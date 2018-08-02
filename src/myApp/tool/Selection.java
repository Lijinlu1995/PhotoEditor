package myApp.tool;

import myApp.workspace.Workspace;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

//The Selection tool class. Allow users to select an area of workspace with a box.
public class Selection extends AbstractTool {

    // Selection box
    private final Rectangle rectangle;

    // Last mouse position
    private double lastX;
    private double lastY;

    // Flag
    private boolean isMoved;
    private boolean isDragged;

    public Selection(Workspace w) {
        super(w);

        // Set the cursor
        workspace.getLayerTool().setCursor(Cursor.CROSSHAIR);

        // Create selection box
        rectangle = new Rectangle(0, 0, 0, 0);
        rectangle.setVisible(true);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1.8);
        rectangle.getStrokeDashArray().addAll(4d, 12d);

        // Stroke animation
        final double maxOffset
                = rectangle.getStrokeDashArray().stream()
                .reduce(
                        0d,
                        (a, b) -> a + b
                );

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                rectangle.strokeDashOffsetProperty(),
                                0,
                                Interpolator.LINEAR
                        )
                ),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(
                                rectangle.strokeDashOffsetProperty(),
                                maxOffset,
                                Interpolator.LINEAR
                        )
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

    }

    @Override
    public void mousePressed(double x, double y) {

        Point3D p = PositionMapper.convert(rectangle, new Point3D(x, y, 0));

        // Create a selection box
        if (!rectangle.contains(new Point2D(p.getX(), p.getY()))) {

            workspace.getLayerTool().getChildren().remove(rectangle);
            workspace.getLayerTool().getChildren().add(rectangle);
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setX(x);
            rectangle.setY(y);

            workspace.getLayerTool().setCursor(Cursor.NE_RESIZE);

            isDragged = true;
        } // When pressed on selection box, set the flag isMoving
        else {
            workspace.getLayerTool().setCursor(Cursor.MOVE);

            isMoved = true;
        }

        // Save last mouse position
        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseDragged(double x, double y) {

        // Resize the selection box
        if (isDragged) {
            double width = x - rectangle.getX();
            double height = y - rectangle.getY();

            if (width < 0) {
                rectangle.setTranslateX(x - rectangle.getX());
            } else {
                rectangle.setTranslateX(0);
            }

            if (height < 0) {
                rectangle.setTranslateY(y - rectangle.getY());
            } else {
                rectangle.setTranslateY(0);
            }

            rectangle.setWidth(Math.abs(width));
            rectangle.setHeight(Math.abs(height));
        } // Moving the selection box
        else if (isMoved) {
            double addX = x - lastX;
            double addY = y - lastY;

            rectangle.setTranslateX(rectangle.getTranslateX() + addX);
            rectangle.setTranslateY(rectangle.getTranslateY() + addY);

            lastX = x;
            lastY = y;
        }
    }

    @Override
    public void mouseReleased(double x, double y) {
        isDragged = false;
        isMoved = false;

        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
    }

    //Get selection box
    public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public void mouseMoved(double x, double y) {
    }
}

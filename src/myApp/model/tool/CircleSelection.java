package myApp.model.tool;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import myApp.model.workspace.Workspace;

public class CircleSelection extends AbstractTool {
    // Selection box
    private final Circle circle;
    // Last mouse position
    private double lastX;
    private double lastY;

    // Flag
    private boolean isMoved;
    private boolean isDragged;

    public CircleSelection(Workspace w) {
        super(w);

        // Set the cursor
        workspace.getLayerTool().setCursor(Cursor.CROSSHAIR);

        // Create selection box
        circle = new Circle(0, 0, 0, Color.TRANSPARENT);
        circle.setVisible(true);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1.8);
        circle.getStrokeDashArray().addAll(4d, 12d);

        // Stroke animation
        final double maxOffset
                = circle.getStrokeDashArray().stream()
                .reduce(
                        0d,
                        (a, b) -> a + b
                );

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                circle.strokeDashOffsetProperty(),
                                0,
                                Interpolator.LINEAR
                        )
                ),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(
                                circle.strokeDashOffsetProperty(),
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

        Point3D p = PositionMapper.convert(circle, new Point3D(x, y, 0));

        // Create a selection box
        if (!circle.contains(new Point2D(p.getX(), p.getY()))) {

            workspace.getLayerTool().getChildren().remove(circle);
            workspace.getLayerTool().getChildren().add(circle);
            circle.setCenterX(x);
            circle.setCenterY(y);
            circle.setRadius(0);
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

            double width = x - circle.getCenterX();
            double height = y - circle.getCenterY();

            if (width < 0) {
                circle.setTranslateX(x - circle.getCenterX());
            } else {
                circle.setTranslateX(0);
            }

            if (height < 0) {
                circle.setTranslateY(y - circle.getCenterY());
            } else {
                circle.setTranslateY(0);
            }

            circle.setRadius(Math.sqrt(width*width + height*height));
          /*  circle.setWidth(Math.abs(width));
            circle.setHeight(Math.abs(height));*/
        } // Moving the selection box
        else if (isMoved) {
            double addX = x - lastX;
            double addY = y - lastY;

            circle.setTranslateX(circle.getTranslateX() + addX);
            circle.setTranslateY(circle.getTranslateY() + addY);

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

    public Circle getCircle() {
        return circle;
    }

    @Override
    public void mouseMoved(double x, double y) {
    }
}

package myApp.model.tool.selection;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import myApp.model.tool.AbstractTool;
import myApp.model.tool.PositionMapper;
import myApp.model.workspace.Workspace;

import java.util.ArrayList;
import java.util.List;

public class PolygonSelection extends AbstractTool {

    // Selection box

    private final Polygon polygon;

    Line line = new Line();
    // Last mouse position
    private double lastX;
    private double lastY;

    // Flag
    private boolean isMoved;
    private boolean isDragged;

    boolean drawShape = true;
    double a[] = new double[3];
    double b[] = new double[3];
    int count = 0;
    List<Double> values = new ArrayList<>();

    public PolygonSelection(Workspace w) {
        super(w);

        // Set the cursor
        workspace.getLayerTool().setCursor(Cursor.CROSSHAIR);

        // Create selection box
        polygon = new Polygon();
        polygon.getPoints().addAll(values);
        polygon.setVisible(true);
        polygon.setFill(Color.TRANSPARENT);
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(1.8);
        polygon.getStrokeDashArray().addAll(4d, 12d);

        // Stroke animation
        final double maxOffset
                = polygon.getStrokeDashArray().stream()
                .reduce(
                        0d,
                        (a, b) -> a + b
                );

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                polygon.strokeDashOffsetProperty(),
                                0,
                                Interpolator.LINEAR
                        )
                ),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(
                                polygon.strokeDashOffsetProperty(),
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

        Point3D p = PositionMapper.convert(polygon, new Point3D(x, y, 0));

        // Create a selection box
        if (!polygon.contains(new Point2D(p.getX(), p.getY()))) {

            workspace.getLayerTool().getChildren().remove(polygon);
            workspace.getLayerTool().getChildren().add(polygon);
            if (drawShape) {
                a[count] = x;
                b[count] = y;
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(1.8);
            }

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
            double width = x - polygon.getLayoutX();
            double height = y - polygon.getLayoutY();

            if (width < 0) {
                polygon.setTranslateX(x - polygon.getLayoutX());
            } else {
                polygon.setTranslateX(0);
            }

            if (height < 0) {
                polygon.setTranslateY(y - polygon.getLayoutY());
            } else {
                polygon.setTranslateY(0);
            }

            polygon.setLayoutX(x);
            polygon.setLayoutY(y);
        } // Moving the selection box
        else if (isMoved) {
            double addX = x - lastX;
            double addY = y - lastY;

            polygon.setTranslateX(polygon.getTranslateX() + addX);
            polygon.setTranslateY(polygon.getTranslateY() + addY);

            lastX = x;
            lastY = y;
        }
    }

    @Override
    public void mouseReleased(double x, double y) {
        isDragged = false;
        isMoved = false;
        if (drawShape) {
            values.add(a[count]);
            values.add(b[count]);
            if (count == 0) {
                line.setStartX(a[0]);
                line.setStartY(b[0]);
                line.setEndX(a[0]);
                line.setEndY(b[0]);
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(1.8);
                //pane.getChildren().add(line);
            } else if (count == 1) {
                //pane.getChildren().remove(line);
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(1.8);
                line.setStartX(a[0]);
                line.setStartY(b[0]);
                line.setEndX(a[1]);
                line.setEndY(b[1]);
                //pane.getChildren().add(line);
            } else if (count == 2) {
                //pane.getChildren().remove(line);
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(values);
                triangle.setFill(Color.TRANSPARENT);
                triangle.setStroke(Color.BLACK);
                triangle.setStrokeWidth(1.8);
                //pane.getChildren().add(triangle);
                //operationHistory.shapeDrawn(pane);
            }
            count++;
            if (count == 3) {
                count = 0;
                values.clear();
            }
        }

        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
    }

    //Get selection box
    public Polygon getPolygon() {
        return polygon;
    }

    @Override
    public void mouseMoved(double x, double y) {
    }
}

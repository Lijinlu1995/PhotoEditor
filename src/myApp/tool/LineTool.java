package myApp.tool;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import myApp.tool.settings.SizeConfigurableTool;
import myApp.workspace.Workspace;

public class LineTool extends Shapes {

    private Line line;
    private double initialX;
    private double initialY;
    private double finalX;
    private double finalY;
    private double stateX;
    private double stateY;

    public LineTool(Workspace workspace) {
        super(workspace);
    }


    @Override
    public void drawShape(Pane pane, ColorPicker pickColor, Slider lineWidth) {
        SimpleDoubleProperty x = new SimpleDoubleProperty();
        SimpleDoubleProperty y = new SimpleDoubleProperty();
        Line initialFinal = getNewLine();
        EventHandler<MouseEvent> mouseHandler = mouseEvent -> {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                initialFinal.endXProperty().bind(x);
                initialFinal.endYProperty().bind(y);
                pane.getChildren().add(initialFinal);
                initialFinal.setStroke(pickColor.getValue());
                initialFinal.setStrokeWidth(lineWidth.getValue());
                initialFinal.setStartX(mouseEvent.getX());
                initialFinal.setStartY(mouseEvent.getY());
                x.set(mouseEvent.getX());
                y.set(mouseEvent.getY());
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                x.set(mouseEvent.getX());
                y.set(mouseEvent.getY());
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                Line finalLine = getNewLine();
                finalLine.setStroke(pickColor.getValue());
                finalLine.setStrokeWidth(lineWidth.getValue());
                finalLine.setStartX(initialFinal.getStartX());
                finalLine.setStartY(initialFinal.getStartY());
                finalLine.setEndX(initialFinal.getEndX());
                finalLine.setEndY(initialFinal.getEndY());
                pane.getChildren().add(finalLine);
                pane.getChildren().remove(initialFinal);
                initialFinal.setStartX(0);
                initialFinal.setStartY(0);
                x.set(0);
                y.set(0);
            }
        };
        pane.setOnMousePressed(mouseHandler);
        pane.setOnMouseDragged(mouseHandler);
        pane.setOnMouseReleased(mouseHandler);
    }

    private Line getNewLine() {
        return new Line();
    }

    @Override
    public void resizeShape(Node node, Pane pane) {
        resetMouseEvents(pane);
        line = (Line) node;
        initialX = line.getStartX();
        initialY = line.getStartY();
        finalX = line.getEndX();
        finalY = line.getEndY();
        EventHandler<MouseEvent> mouseHandler = mouseEvent -> {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
                setCursor(node, mouseEvent.getX(), mouseEvent.getY());
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                setShapeStyleOnDrag(node);
                initialX = line.getStartX();
                initialY = line.getStartY();
                finalX = line.getEndX();
                finalY = line.getEndY();
                stateX = mouseEvent.getX();
                stateY = mouseEvent.getY();
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                // Start
                if ((stateX > initialX - 10 && stateX < initialX + 10)
                        || (stateY > initialY - 10 && stateY < initialY + 10)) {
                    line.setStartX(mouseEvent.getX());
                    line.setStartY(mouseEvent.getY());
                }
                // End
                if ((stateX > finalX - 10 && stateX < finalX + 10)
                        || (stateY > finalY - 10 && stateY < finalY + 10)) {
                    line.setEndX(mouseEvent.getX());
                    line.setEndY(mouseEvent.getY());
                }
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                setShapeStyleOnRelease(node);
            }};
        node.setOnMousePressed(mouseHandler);
        node.setOnMouseDragged(mouseHandler);
        node.setOnMouseReleased(mouseHandler);
        node.setOnMouseMoved(mouseHandler);
    }

    @Override
    public void setCursor(Node node, double stateX, double stateY) {
        Line line = (Line) node;
        initialX = line.getStartX();
        initialY = line.getStartY();
        finalX = line.getEndX();
        finalY = line.getEndY();
        // Right
        if ((stateX > initialX - 3 && stateX < initialX + 3)
                || (stateY > initialY - 3 && stateY < initialY + 3)) {
            node.setCursor(Cursor.N_RESIZE);
        }
        // Left
        else if ((stateX > finalX - 3 && stateX < finalX + 3)
                || (stateY > finalY - 3 && stateY < finalY + 3)) {
            node.setCursor(Cursor.N_RESIZE);
        } else {
            node.setCursor(Cursor.DEFAULT);
        }
    }

    @Override
    public void mousePressed(double x, double y) {

    }

    @Override
    public void mouseDragged(double x, double y) {

    }

    @Override
    public void mouseReleased(double x, double y) {

    }

    @Override
    public void mouseMoved(double x, double y) {

    }
}

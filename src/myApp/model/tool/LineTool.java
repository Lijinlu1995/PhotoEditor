package myApp.model.tool;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import myApp.model.tool.settings.SizeConfigurableTool;
import myApp.model.workspace.Workspace;

public class LineTool extends DrawTool implements SizeConfigurableTool {
    private Color color;

    public LineTool(Workspace workspace) {
        this(workspace, ColorSet.getInstance().getColor(), 5);
    }

    public LineTool(Workspace workspace, Color color, int size) {
        super(workspace, size);
        this.color = color;
    }

    Line line = new Line();


    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }


    public void mousePressed(double x, double y,GraphicsContext gc) {
        setColor(ColorSet.getInstance().getColor());
        super.mousePressed(x, y);
    }

    @Override
    public void mouseDragged(double x, double y) {
        if (!started) {
            setColor(ColorSet.getInstance().getColor());
        }
        super.mouseDragged(x, y);
    }

    @Override
    protected void drawPixel(int x, int y, GraphicsContext gc) {

    }
    public void line(int x0, int y0, int x1, int y1, GraphicsContext gc, Canvas canvas){
            gc.beginPath();
            gc.setFill(color);
            gc.setLineWidth(5);
            gc.strokeLine(x0, y0, x1, y1);
            gc.closePath();
      /*  gc.moveTo(x0,y0);
        gc.lineTo(x1,y1);*/
        /*line.setStartX(x0);
        line.setStartY(y0);
        line.setEndX(x1);
        line.setEndX(y1);*/

    }

    public void mouseReleased(double x, double y) {
        super.mouseReleased(x, y);
    }
}

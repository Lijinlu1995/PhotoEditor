package myApp.tool;

import myApp.tool.settings.SizeConfigurableTool;
import myApp.workspace.Workspace;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brush extends DrawTool implements SizeConfigurableTool {

    // Color of the brush
    private Color color;

    public Brush(Workspace workspace) {
        this(workspace, ColorSet.getInstance().getColor(), 5);
    }

    public Brush(Workspace workspace, Color color, int size) {
        super(workspace, size);
        this.color = color;
    }

    @Override
    protected void drawPixel(int x, int y, GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x - size / 2.0, y - size / 2.0, size, size);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void mousePressed(double x, double y) {
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
    public void mouseReleased(double x, double y) {
        super.mouseReleased(x, y);
    }
}

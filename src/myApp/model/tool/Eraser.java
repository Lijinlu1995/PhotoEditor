package myApp.model.tool;

import myApp.model.tool.settings.SizeConfigurableTool;
import myApp.model.workspace.Workspace;
import javafx.scene.canvas.GraphicsContext;


public class Eraser extends DrawTool implements SizeConfigurableTool {


    public Eraser(Workspace workspace) {
        this(workspace, 5);
    }

    public Eraser(Workspace workspace, int size) {
        super(workspace, size);
    }

    @Override
    public void drawPixel(int x, int y, GraphicsContext gc) {
        gc.clearRect(x - (size / 2.0), y - (size / 2.0), size, size);
    }
}

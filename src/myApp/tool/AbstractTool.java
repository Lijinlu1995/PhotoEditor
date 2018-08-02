package myApp.tool;

import myApp.controller.ToolBarController;
import myApp.workspace.HistoryNotifier;
import myApp.workspace.Workspace;

public abstract class AbstractTool implements Tool {

    protected final Workspace workspace;
    protected HistoryNotifier notifier;
    protected ToolBarController controller;

    public AbstractTool(Workspace workspace) {
        this.workspace = workspace;
        notifier = new HistoryNotifier();
        notifier.addObserver(workspace.getHistory());
    }

    @Override
    public abstract void mousePressed(double x, double y);

    @Override
    public abstract void mouseDragged(double x, double y);

    @Override
    public abstract void mouseReleased(double x, double y);
}

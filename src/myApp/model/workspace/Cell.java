package myApp.model.workspace;

import javafx.scene.layout.HBox;

/**
 * Cell object are used to represent elements in a LayerList.
 */
public abstract class Cell<T> extends HBox {
    // The cell index in the displayed LayerList panel

    private int index;
    private boolean selected = false;
    private final T target;

    public Cell(T target) {
        this.target = target;
    }

    /**
     * Set the Cell current index inside the displayed LayerList the panel
     */
    public void setIndex(int i) {
        index = i;
    }

    /**
     * Get the current index of the cell inside the displayed LayerList Panel
     */
    public int getIndex() {
        return index;
    }

    /**
     * Select the Cell
     */
    public void select() {
        selected = true;
    }

    /**
     * deselect the Cell
     */
    public void deSelect() {
        selected = false;
    }

    /**
     * Check if the Cell is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Get the T element targeted by the Cell
     */
    public T getTarget() {
        return target;
    }

    /**
     * Set the name of the layer.
     */
    public abstract void setLayerName(String name);
}

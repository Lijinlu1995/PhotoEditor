package myApp.model.workspace;

import javafx.scene.layout.HBox;

/**
 * <h1>CELL</h1>
 * Cell object are used to represent elements in a LayerList.
 *
 * @param <T> the type of element represented
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
     *
     * @param i the index
     */
    public void setIndex(int i) {
        index = i;
    }

    /**
     * Get the current index of the cell inside the displayed LayerList Panel
     *
     * @return the index
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
     *
     * @return whether the cell is selected or not
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Get the T element targeted by the Cell
     *
     * @return the T target
     */
    public T getTarget() {
        return target;
    }

    /**
     * Set the name of the layer.
     *
     * @param name
     */
    public abstract void setLayerName(String name);
}

package myApp.model.workspace;

import javafx.collections.ObservableList;

/**
 * Interface to represent Cell factories.
 */
public interface ICellFactory<T> {

    /**
     * The method to create Cells using the factory.
     */
    public Cell createCell(T element, ObservableList<T> listElements);
}

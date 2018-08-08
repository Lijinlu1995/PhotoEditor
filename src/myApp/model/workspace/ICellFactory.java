package myApp.model.workspace;

import javafx.collections.ObservableList;

/**
 * <h1>ICellFactory</h1>
 *
 * Interface to represent Cell factories.
 *
 * @param <T> the type of elements represented
 */
public interface ICellFactory<T> {

    /**
     * The method to create Cells using the factory.
     *
     * @param element the element we are creating the Cell for
     * @param listElements the list containing said element
     * @return a new Cell targeting the element
     */
    public Cell createCell(T element, ObservableList<T> listElements);
}

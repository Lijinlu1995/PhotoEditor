package myApp.model.workspace;

import javafx.collections.ObservableList;

/**
 * <h1>DefaultCellFactory</h1>
 *
 * ICellFactory implementation to create DefaultCell objects.
 */
public class DefaultCellFactory<T> implements ICellFactory<T> {

    @Override
    public Cell createCell(T element, ObservableList<T> listElements) {
        return new DefaultCell(element);
    }

}

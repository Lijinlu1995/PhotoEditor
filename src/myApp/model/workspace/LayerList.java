package myApp.model.workspace;

import myApp.model.base.CSSIcons;
import myApp.model.base.Constants;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A LayerList object is a UI component that displays the content of a targeted
 * ObservableList as a list of layers, each element of the list acting as a layer.
 *
 * It's purpose is for a user to be able to control the order appearance of Nodes in
 * a given graph tree. One can for example construct a LayerList by passing it the
 * list of children of a Parent node.
 *
 * Each Node in the given list is represented by an element which must inherit from
 * the Cell class in the same package. For the user to be able to change the type of
 * Cells that are used by the LayerList, it is possible to set a cell factory for the
 * LayerList to used. It is done by implementing the ICellFactory interface and by
 * passing it in the constructor, or using the appropriate method.
 */
public class LayerList<T> extends VBox {

    // List the the LayerList must observe
    private ObservableList<T> targetList;
    // The cell factory used to create cells in the LayerList panel
    private ICellFactory factory;
    // Panel in which to display the layer list and the controls
    private VBox panel;
    // The container of the list of layers
    private ScrollPane layerContainer;
    // Wrapper to put inside the layersContainer
    private VBox wrapper;
    // List of current used cell
    private LinkedList<Cell<T>> cellList;
    // List of selected items in the targetList
    private LinkedList<Cell> selectedCells;

    /**
     * Constructor. Sets the ObservableList to target. The default cell factory will
     * be of the DefaultCellFactory class.
     */
    public LayerList(ObservableList<T> targetList) {
        this(targetList, new DefaultCellFactory<T>());
    }

    /**
     * Constructor. Sets the ObservableList to target and the cell factory to use.
     */
    public LayerList(ObservableList<T> targetList, ICellFactory<T> factory) {
        // Set attributes
        this.targetList = targetList;
        this.factory = factory;

        // Create visual containers
        // The exterior container for the controls 
        panel = new VBox();
        // The ScrollPane to contain the layer list wrapper
        layerContainer = new ScrollPane();
        layerContainer.setPrefHeight(400);
        layerContainer.setPrefWidth(200);
        layerContainer.setContent(wrapper);
        layerContainer.setFitToWidth(true);
        // The wrapper that will contains the list of Cells representing the layers
        wrapper = new VBox();
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setFillWidth(true);
        wrapper.setSpacing(3);
        layerContainer.setContent(wrapper);

        // Add the ScrollPane to the exterior container. The wrapper is for later
        panel.getChildren().add(layerContainer);

        // Create the contianer for control buttons
        HBox controlButtons = new HBox();
        controlButtons.setAlignment(Pos.CENTER);
        controlButtons.setMinHeight(Constants.BUTTONS_HEIGHT);
        controlButtons.setSpacing(3);

        // Create a delete button to delete layers
        Button delete = new Button("");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            int count = 0;

            @Override
            public void handle(ActionEvent t) {

                // Get currently selected items and delete them
                List<T> items = LayerList.this.getSelectedItems();
                for (T item : items) {
                    LayerList.this.targetList.remove(item);
                }
                selectedCells.clear();
                selectTopLayer();

                // Once every 10 delete, make a clean of unused old Cells 
                if (++count == 10) {
                    cleanCells();
                    count = 0;
                }
            }
        });
        delete.getStyleClass().addAll(CSSIcons.TRASH, "tool-button");
        delete.setPrefSize(Constants.BUTTONS_HEIGHT, Constants.BUTTONS_HEIGHT);

        // Create a button to move layers up in the list, aka place them further in the list towards the end
        Button moveUp = new Button("");
        moveUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                // Declaration of the list to store items from the targetList to move
                LinkedList<T> itemsToMove = new LinkedList();

                // Go through the items from end to start, in order to move them in the right order
                int i = 0;
                ListIterator<T> it = LayerList.this.targetList.listIterator(
                        LayerList.this.targetList.size());
                while (it.hasPrevious()) {
                    T item = it.previous();
                    if (getSelectedItems().contains(item)) {
                        itemsToMove.add(item);
                    }
                }

                /*
             * Maximum boundary past which it is not allowed to move items further
             * because we are either at the end of the list, or there is only 
             * items that are being moved as well (thus blocking the movement).
                 */
                int indexBoundary = LayerList.this.targetList.size() - 1;

                // For each element to move, remove them and insert them again one position further
                for (T element : itemsToMove) {
                    int index = LayerList.this.targetList.indexOf(element);
                    if (index < indexBoundary) {
                        LayerList.this.targetList.remove(element);
                        LayerList.this.targetList.add(index + 1, element);
                    } else {
                        indexBoundary--;
                    }
                }
            }
        });
        moveUp.getStyleClass().addAll(CSSIcons.UP_ARROW, "tool-button");
        moveUp.setPrefSize(Constants.BUTTONS_HEIGHT, Constants.BUTTONS_HEIGHT);

        // Create a button to move element down the list, aka place them closer from the beginning
        Button moveDown = new Button("");
        moveDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                // Declaration of the list to store items from the targetList to move
                LinkedList<T> itemsToMove = new LinkedList();

                // Go throught the list from start to end
                int i = 0;
                ListIterator<T> it = LayerList.this.targetList.listIterator();
                while (it.hasNext()) {
                    T item = it.next();
                    if (getSelectedItems().contains(item)) {
                        itemsToMove.add(0, item);
                    }
                }

                /*
             * Boundary before which it is not possible to move elements any more
             * because we are either at the beginning of the list, or ther is only 
             * element that are also being moved before.
                 */
                int indexBoundary = 0;

                // For each item, move them down one step if it is possible
                ListIterator<T> it2 = itemsToMove.listIterator(itemsToMove.size());
                while (it2.hasPrevious()) {
                    T element = it2.previous();
                    int index = LayerList.this.targetList.indexOf(element);
                    if (index > indexBoundary) {
                        LayerList.this.targetList.remove(element);
                        LayerList.this.targetList.add(index - 1, element);
                    } else {
                        indexBoundary++;
                    }
                }
            }
        });
        moveDown.getStyleClass().addAll(CSSIcons.DOWN_ARROW, "tool-button");
        moveDown.setPrefSize(Constants.BUTTONS_HEIGHT, Constants.BUTTONS_HEIGHT);

        // Add the buttons to the control panel
        controlButtons.getChildren().addAll(moveUp, moveDown, delete);

        // Add it to the main container
        panel.getChildren().add(controlButtons);

        // Add the container to the VBox
        getChildren().add(panel);

        // List of currently selected cells
        selectedCells = new LinkedList<>();
        // List of used cells for displaying items from the target list
        cellList = new LinkedList<>();

        // Add a listener to display layers again when the list has been modified
        targetList.addListener(new ListChangeListener<T>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends T> change) {
                displayChildren();
            }

        });

        // Display the children at the beginning
        displayChildren();
    }

    /**
     * Update and display the cells in the LayerList panel.
     */
    private void displayChildren() {
        // Clear the current display of cells
        wrapper.getChildren().clear();
        selectedCells.clear();
        int i = 0;

        // Display each element from the target list
        for (final T element : targetList) {

            // Get the cell representation of the element
            final Cell cell = getCell(element);
            // Set its index in the list
            cell.setIndex(i++);

            if (cell.isSelected()) {
                addSelectedCellSorted(cell);
            }

            // Add it to the wrapper
            wrapper.getChildren().add(0, cell);
        }
    }

    /**
     * Returns the Cell that targets this element in the targeList if it already has
     * been created. If there is none, for example when a new layer has been added,
     * it create a new Cell, adds it ti the list of cells, and return it.
     */
    private Cell<T> getCell(T element) {

        // Search in existing cells
        for (Cell<T> cell : cellList) {
            if (cell.getTarget() == element) {
                return cell;
            }
        }

        // Else create it and return it
        final Cell<T> cell = factory.createCell(element, targetList);
        cell.deSelect();
        cell.setIndex(targetList.indexOf(element));
        cellList.add(cell);
        // Add an event listner on the mouse pressed to manage selection
        cell.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                // If Ctrl is not pressed, the selection is cleared
                if (!t.isControlDown()) {
                    for (Cell c : selectedCells) {
                        c.deSelect();
                    }
                    selectedCells.clear();
                }
                // Select the cell if not already selected
                if (!cell.isSelected()) {
                    addSelectedCellSorted(cell);
                } else if (t.isControlDown() && cell.isSelected()) {
                    cell.deSelect();
                    selectedCells.remove(cell);
                }

                // If we are in the case of a double click, show a dialog to rename
                if (t.getButton().equals(MouseButton.PRIMARY)) {
                    if (t.getClickCount() == 2) {

                        // Create Dialog
                        Dialog dialog = new Dialog<>();

                        // Set the title
                        dialog.setTitle("Rename the layer.");

                        // Set button
                        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                        // Set text field
                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(10, 10, 10, 10));

                        // Set the text area
                        final TextField text = new TextField();

                        // Add elements
                        grid.add(text, 0, 1);

                        // Add to the dialog
                        dialog.getDialogPane().setContent(grid);

                        // Request focus
                        Platform.runLater(() -> text.requestFocus());

                        // Return result
                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == ButtonType.OK) {
                                return text.getText();
                            }

                            return null;
                        });

                        // If the user entered something, set the layer name
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            cell.setLayerName(result.get());
                        }

                    }
                }
            }

        });
        return cell;
    }

    /**
     * Set the ICellFactory implementation to use
     *
     * @param factory the cell factory to use
     */
    public void setCellFactory(ICellFactory factory) {
        this.factory = factory;
    }

    /**
     * Get a List of currently selected items.
     *
     * @return a List of currently selected items
     */
    public List<T> getSelectedItems() {
        LinkedList<T> selectedItems = new LinkedList<>();

        for (Cell<T> cell : selectedCells) {
            selectedItems.add(cell.getTarget());
        }

        return selectedItems;
    }

    /**
     * Add a cell to the list of select cells keeping the list sorted from the bottom
     * layer at the beginning to the top layer at the end. It also set said cell to
     * select calling it select method.
     */
    private void addSelectedCellSorted(Cell<T> cell) {
        cell.select();
        int i = 0;
        ListIterator<Cell> it = selectedCells.listIterator();
        while (it.hasNext()) {
            Cell<T> c = it.next();
            if (cell.getIndex() > c.getIndex()) {
                ++i;
            } else {
                break;
            }
        }
        selectedCells.add(i, cell);
    }

    /**
     * Clean unused Cells still present in the cellList List.
     */
    private void cleanCells() {
        LinkedList<Cell> toRemove = new LinkedList<>();
        for (Cell<T> cell : cellList) {
            // Wether this Xell target element is still present in the target list
            boolean stillRelevant = false;
            for (T element : targetList) {
                if (cell.getTarget() == element) {
                    stillRelevant = true;
                    break;
                }
            }
            // If not, add it to the list of Cell to remove
            if (!stillRelevant) {
                toRemove.add(cell);
            }
        }

        // Delete all cells to remove
        for (Cell<T> cell : toRemove) {
            cellList.remove(cell);
        }
    }

    /**
     * Clear the current selection
     */
    public void clearSelection() {
        for (Cell cell : selectedCells) {
            cell.deSelect();
        }
        selectedCells.clear();
    }

    /**
     * Select the top layer, AKA the last element in the target list.
     */
    public void selectTopLayer() {
        if (targetList.size() > 0) {
            Cell cell = getCell(targetList.get(targetList.size() - 1));
            addSelectedCellSorted(cell);
        }
    }

    /**
     * Select the bottom layer, AKA the first element in the target list.
     */
    public void selectBottomLayer() {
        if (targetList.size() > 0) {
            Cell cell = getCell(targetList.get(0));
            addSelectedCellSorted(cell);
        }
    }

    /**
     * Adds given items to the list of selected layers. If the target list doesn't
     * contain any of the given elements, this particular element won't be added nor
     * selected.
     *
     * @param layers the layers to add to the selection
     */
    public void selectLayers(T... layers) {
        for (T layer : layers) {
            selectLayer(layer);
        }
    }

    /**
     * Add the given layer to the selection. If the layer is not present in the
     * target list, it will not be added nor selected.
     *
     * @param layer to add to the selection
     */
    public void selectLayer(T layer) {
        if (targetList.contains(layer)) {
            Cell<T> cell = getCell(layer);
            if (!cell.isSelected()) {
                addSelectedCellSorted(cell);
            }
        }
    }

    /**
     * Adds a specific layer by index to the selection. The method won't do anything
     * if the index is out of bounds. The index 0 is the bottom layer and the index
     * targetList.size() -1 is the top layer.
     *
     * @param i the index of the layer to add to the selection
     */
    public void selectLayerByIndex(int i) {
        if (i < 0 || i < targetList.size()) {
            selectLayer(targetList.get(i));
        }
    }

}

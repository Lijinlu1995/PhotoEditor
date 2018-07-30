package myApp.workspace;

import myApp.base.Constants;
import myApp.base.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import java.io.IOException;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Guillaume Milani
 * @date 23 May 2017
 * @brief Manage history for a GEMMS workspace
 *
 * Each time one of this class instance is notified it saves the workspace layer's
 * state. It is then possible to undo() (restore previous state) or redo() (restore
 * state before undo())
 */
public class History implements Observer {

    /**
     * Lists to save the history, thumbnails and selected layers
     */
    List<String> history;
    List<List<Integer>> selectedHistory;

    ObservableList<Image> imagesHistory;

    /**
     * Current index in the history lists
     */
    private int currentIndex;

    private Workspace workspace;

    public History(Workspace workspace) {
        currentIndex = 0;

        this.history = new LinkedList<>();
        this.selectedHistory = new LinkedList<>();
        this.imagesHistory = FXCollections.observableArrayList();

        this.workspace = workspace;
    }

    @Override
    public void update(Observable observable, Object o) {
        save();
    }

    /**
     * Save the current state to the history
     */
    private void save() {
        Platform.runLater(() -> {
            // If a modification is done, a new "branch" begins. No action to redo anymore
            history.subList(0, currentIndex).clear();
            selectedHistory.subList(0, currentIndex).clear();
            imagesHistory.remove(0, currentIndex);

            currentIndex = 0;

            try {
                // Get the selected layers indexes
                List<Integer> indexes = new LinkedList<>();
                workspace.getCurrentLayers().forEach(
                        n -> indexes.add(workspace.getLayers().indexOf(n)));

                // Get the thumbnail for visual history
                final SnapshotParameters sp = new SnapshotParameters();

                double scale = Constants.HISTORY_THUMB_WIDTH / workspace.width();
                sp.setTransform(Transform.scale(scale, scale));

                Image snapshot = workspace.snapshot(sp, null);
                PixelReader reader = snapshot.getPixelReader();
                WritableImage newImage = new WritableImage(reader, 0, 0,
                        (int) Constants.HISTORY_THUMB_WIDTH,
                        (int) (workspace.height() * scale));

                // Save the current states
                history.add(0, Utils.serializeNodeList(workspace.getLayers()));
                selectedHistory.add(0, indexes);
                imagesHistory.add(0, newImage);

                workspace.getHistoryList().getSelectionModel().select(currentIndex);
            } catch (Exception e) {
                Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }

    /**
     * Cancel the last action done
     */
    public void undo() {
        if (currentIndex < history.size() - 1) {
            restoreToIndex(currentIndex + 1);
        }
    }

    /**
     * Redo the last canceled action
     */
    public void redo() {
        if (currentIndex > 0) {
            restoreToIndex(currentIndex - 1);
        }
    }

    /**
     * Restore the workspace at the state in parameter
     *
     * @param index index in the history list to restore the workspace state from
     */
    public void restoreToIndex(int index) {
        if (index < 0 || index > history.size()) {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE,
                    "Trying to restore a state at index out of bounds");
        } else {
            // Select the new current state in the list
            currentIndex = index;
            workspace.getHistoryList().getSelectionModel().select(currentIndex);
            try {
                workspace.getLayers().clear();
                // Selected layers
                workspace.getCurrentLayers().clear();

                workspace.getLayers().addAll(Utils.deserializeNodeList(
                        history.get(currentIndex)));
                selectedHistory.get(currentIndex).forEach(
                        i -> workspace.selectLayerByIndex(i));

            } catch (IOException | ClassNotFoundException e) {
                Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * @return an observable list of history's thumbnails
     */
    public ObservableList<Image> getImagesHistory() {
        return imagesHistory;
    }
}

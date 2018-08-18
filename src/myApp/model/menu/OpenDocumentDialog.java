package myApp.model.menu;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Shows a dialog that allow users to open a project file. (*.mype)
 */
public class OpenDocumentDialog {

    private final FileChooser fileChooser;
    private final Stage stage;

    /**
     * Constructor
     *
     * @param s stage for FileChooser
     */
    public OpenDocumentDialog(Stage s) {
        stage = s;

        // Init file chooser
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("MYPE", "*.mype"));
    }

    /**
     * Shows a new file open dialog
     *
     * @return File
     */
    public File showAndWait() {
        return fileChooser.showOpenDialog(stage);
    }
}

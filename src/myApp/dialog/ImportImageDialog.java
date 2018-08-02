package myApp.dialog;

import myApp.controller.MainController;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

/*Display a new file open dialog that allow users to open an image. Only png and jpg
 format are allowed (*.png or .jpg)*/
public class ImportImageDialog {

    private final FileChooser fileChooser;
    private final Stage stage;

    public ImportImageDialog(Stage s) {
        stage = s;

        // Init file chooser
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.setTitle("Open image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg"),
                new FileChooser.ExtensionFilter("gif files (*.gif)", "*.gif"));
    }

    // Shows a new file open dialog

    public Image showAndWait() {
        File file = fileChooser.showOpenDialog(stage);

        // Get name and extension
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        Image image = null;

        // Convert loaded file to image
        if (file != null) {
            if (ext.equals("jpg") || ext.equals("png") || ext.equals("gif")) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    image = SwingFXUtils.toFXImage(bufferedImage, null);
                } catch (IOException ex) {
                    // TODO : manage exceptions
                    Logger.getLogger(MainController.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }

        return image;
    }
}

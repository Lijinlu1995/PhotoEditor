package myApp.model.base;

import myApp.model.workspace.Workspace;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Document {

    // Workspace
    private Workspace workspace;

    // Stage for fileChooser
    private Stage stage;

    // File that contains the workspace
    private File currentFile;

    // Contains document's name
    private String name;

    public Document(Stage s, int width, int height) {
        init(s);

        name = "untitled";
        workspace = new Workspace(width, height);
    }

    public Document(Stage s, File f) throws FileNotFoundException, IOException,
            ClassNotFoundException {
        init(s);

        currentFile = f;

        name = currentFile.getName();

        if (currentFile != null) {
            try (ObjectInputStream in = new ObjectInputStream(
                    new GZIPInputStream(
                            new FileInputStream(currentFile)
                    )
            )) {
                workspace = (Workspace) in.readObject();
            }
        }
    }

    //Init for constructor

    private void init(Stage s) {
        stage = s;
    }

    //Save the workspace

    public void save() throws FileNotFoundException, IOException {

        // Check if there is already a loaded file
        if (currentFile != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new GZIPOutputStream(
                            new FileOutputStream(currentFile)
                    )
            )) {
                out.writeObject(workspace);
            }
        } else {
            saveAs();
        }
    }

    //SaveAs the workspace MYPE
    public void saveAs() throws FileNotFoundException, IOException {
        // Set FileChooser
        FileChooser fileChooser;
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Save as");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("MYPE", "*.mype"));
        fileChooser.setInitialFileName("*.mype");
        // Shows the dialog and waits for the user response
        currentFile = fileChooser.showSaveDialog(stage);
        if (currentFile != null) {
            if (currentFile.getName().endsWith(".mype")) {
                try (ObjectOutputStream out = new ObjectOutputStream(
                        new GZIPOutputStream(new FileOutputStream(currentFile))
                )) {
                    out.writeObject(workspace);
                }
                name = currentFile.getName();
            }
        }
    }

    //Export the workspace as an image

    public void export() throws IOException {
        // Set fileChooser
        FileChooser fileChooser;
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Export");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"));
        // Shows the dialog and waits for the user response
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            // Get name and extension
            String fileName = file.getName();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            // Take a snapshot
            WritableImage writableImage = new WritableImage((int) workspace.width(),
                    (int) workspace.height());
            workspace.snapshot(null, writableImage);
            BufferedImage image = SwingFXUtils.fromFXImage(writableImage, null);
            // Write image
            switch (ext) {
                case "png":
                    ImageIO.write(image, "png", file); break;
                case "jpg":
                    BufferedImage convertedImg = new BufferedImage(image.getWidth(),
                            image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    convertedImg.getGraphics().setColor(Color.white);
                    convertedImg.getGraphics().fillRect(0, 0, image.getWidth(),
                            image.getHeight());
                    convertedImg.getGraphics().drawImage(image, 0, 0, null);

                    ImageIO.write(convertedImg, "jpg", file);
                    break;
                case "gif":
                    ImageIO.write(image, "gif", file); break;
                default:
                    // Manage exceptions
                    System.out.println("This is not a supported extension"); break;
            }
        }
    }

    //Return the workspace

    public Workspace workspace() {
        return workspace;
    }

    //Return name of the document

    public String name() {
        return name;
    }
}

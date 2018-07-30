package myApp.dialog;

import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.converter.IntegerStringConverter;

/**
 * <h1>NewDocumentDialog</h1>
 *
 * Display a dialog that allow users to set a size and a backgoud color for the
 * document (Workspace).
 */
public class NewDocumentDialog {

    // Dialog
    private Dialog<NewDocument> dialog;

    // Color picker
    private ColorPicker colorPicker;

    /**
     * Constructor
     */
    public NewDocumentDialog() {
        dialog = new Dialog<>();
        colorPicker = new ColorPicker(Color.color(0, 0, 0, 0));

        dialog.setTitle("Create a new file");

        // Set button
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL);

        // Set grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Set text field
        TextField widthText = new TextField();
        widthText.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter()));
        TextField heightText = new TextField();
        heightText.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter()));

        // Display label and text field
        Label description = new Label("Document size");
        description.setFont(Font.font(null, FontWeight.BOLD, 13));
        grid.add(description, 0, 0);
        grid.add(new Label("Width:"), 0, 1);
        grid.add(widthText, 1, 1);
        grid.add(new Label("Height:"), 0, 2);
        grid.add(heightText, 1, 2);
        grid.add(new Label("Background color:"), 0, 3);
        grid.add(colorPicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus
        Platform.runLater(() -> widthText.requestFocus());

        Node loginButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setDisable(true);

        // Field validation
        ChangeListener<String> listener = 
                (ObservableValue<? extends String> observable,
                        String oldValue, String newValue) -> {
            boolean isError = false;

            try {
                // Get user input
                int width = Integer.valueOf(widthText.textProperty().get());
                int height = Integer.valueOf(heightText.textProperty().get());

                // Check size
                if (width <= 0 || width > 3000 || height <= 0 || height > 3000) {
                    isError = true;
                }
            } catch (NumberFormatException e) {
                isError = true;
            }

            // Disable button if inputs are incorrect
            loginButton.setDisable(isError);
        };

        widthText.textProperty().addListener(listener);
        heightText.textProperty().addListener(listener);

        // Return result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new NewDocument(
                        Integer.valueOf(widthText.getText()),
                        Integer.valueOf(heightText.getText()),
                        colorPicker.getValue());
            }

            return null;
        });
    }

    /**
     * Shows the dialog and waits for the user response
     *
     * @return an optional NewDocument
     */
    public Optional<NewDocument> showAndWait() {
        return dialog.showAndWait();
    }
}

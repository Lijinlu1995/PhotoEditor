package myApp.model.menu;

import myApp.model.workspace.Workspace;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.converter.IntegerStringConverter;

public class ResizeDialog {

    // Dialog
    private Dialog<Rectangle> dialog;

    public ResizeDialog(Workspace workspace) {
        dialog = new Dialog<>();

        dialog.setTitle("Resize workspace");

        // Set button
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,
                ButtonType.CANCEL);

        // Set GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Set text field
        TextField widthText = new TextField();
        widthText.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter()));
        widthText.setText(String.valueOf(workspace.width()));

        TextField heightText = new TextField();
        heightText.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter()));
        heightText.setText(String.valueOf(workspace.height()));

        TextField offsetXText = new TextField();
        offsetXText.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter()));
        offsetXText.setText("0");

        TextField offsetYText = new TextField();
        offsetYText.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter()));
        offsetYText.setText("0");

        // Shows label and text field
        Label description = new Label("Document size");
        description.setFont(Font.font(null, FontWeight.BOLD, 13));
        grid.add(description, 0, 0);
        grid.add(new Label("Width:"), 0, 1);
        grid.add(widthText, 1, 1);
        grid.add(new Label("Height:"), 0, 2);
        grid.add(heightText, 1, 2);

        grid.add(new Label("Offset X:"), 0, 3);
        grid.add(offsetXText, 1, 3);
        grid.add(new Label("Offset Y:"), 0, 4);
        grid.add(offsetYText, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus
        Platform.runLater(() -> widthText.requestFocus());

        Node loginButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setDisable(true);

        // Field validation
        ChangeListener<String> listener = (
                ObservableValue<? extends String> observable, String oldValue,
                String newValue) -> {
            boolean isError = false;

            try {
                // Get user input
                int width = Integer.valueOf(widthText.textProperty().get());
                int height = Integer.valueOf(heightText.textProperty().get());
                int offsetX = Integer.valueOf(offsetXText.textProperty().get());
                int offsetY = Integer.valueOf(offsetYText.textProperty().get());

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

        // Field validation
        widthText.textProperty().addListener(listener);
        heightText.textProperty().addListener(listener);
        offsetXText.textProperty().addListener(listener);
        offsetYText.textProperty().addListener(listener);

        // Return result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Rectangle(Integer.valueOf(offsetXText.getText()),
                        Integer.valueOf(offsetYText.getText()),
                        Integer.valueOf(widthText.getText()),
                        Integer.valueOf(heightText.getText()));
            }

            return null;
        });
    }

    public Optional<Rectangle> showAndWait() {
        return dialog.showAndWait();
    }
}

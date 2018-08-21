package myApp.controller;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import myApp.model.menu.ImportImageDialog;
import myApp.model.base.Constants;
import myApp.model.layer.CanvasN;
import myApp.model.layer.ImageN;
import myApp.model.layer.TextN;
import myApp.model.tool.*;
import myApp.model.tool.Eraser;
import myApp.model.tool.selection.CircleSelection;
import myApp.model.tool.selection.PolygonSelection;
import myApp.model.tool.selection.Selection;
import myApp.model.tool.settings.ToolColorSettings;
import myApp.model.tool.settings.ToolFontSettings;
import myApp.model.tool.settings.ToolSettingsContainer;
import myApp.model.tool.settings.ToolSizeSettings;
import myApp.model.workspace.Workspace;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class ToolBarController {

    // Main controller
    private MainController mainController;

    // Toolbar controller
    private MenuBarController toolbarController;

    // List of created tool buttons
    private Button selectedButton;

    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Slider slider;

    /* A pane instance.*/
    public Pane pane;

    /* A canvas instance.*/
    private Canvas canvas;


    @FXML
    public GridPane gridFilterTools;

    final ToolColorSettings textColor = new ToolColorSettings(
            ColorSet.getInstance().getColor());
    final ToolFontSettings textFont = new ToolFontSettings(
            6, 300, TextN.DEFAULT_SIZE);

    final ToolSizeSettings brushSizer = new ToolSizeSettings(
            1, 150, 5);
    final ToolSettingsContainer brushSettings = new ToolSettingsContainer(
            brushSizer);

    final ToolSizeSettings eraserSizer = new ToolSizeSettings(1, 150, 5);
    final ToolSettingsContainer eraserSettings = new ToolSettingsContainer(
            eraserSizer);

    final ToolSettingsContainer textSettings = new ToolSettingsContainer(
            textColor, textFont);

    // Init this controller
    public void init(MainController mainController, MenuBarController toolbarController) {
        this.mainController = mainController;
        this.toolbarController = toolbarController;

        //Create various sliders
        final Slider opacity = new Slider(0, 1, 1);
        final Slider sepia = new Slider(0, 1, 0);
        final Slider saturation = new Slider(-1, 1, 0);
        final Slider contrast = new Slider(-1, 1, 0);
        final Slider brightness = new Slider(-1, 1, 0);
        final Slider blur = new Slider(0, 100, 0);

        //Create labels to show current slider value
        final Label opacityValue = new Label(
                Double.toString(opacity.getValue()));
        final Label sepiaValue = new Label(
                Double.toString(sepia.getValue()));
        final Label saturationValue = new Label(
                Double.toString(saturation.getValue()));
        final Label contrastValue = new Label(
                Double.toString(contrast.getValue()));
        final Label brightnessValue = new Label(
                Double.toString(brightness.getValue()));
        final Label blurValue = new Label(
                Double.toString(blur.getValue()));

        //Add a ChangeListener to each slider so that they may modify current layers
        opacity.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {

                Workspace w = mainController.getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        n.setOpacity(new_val.doubleValue());
                    }
                }
                opacityValue.setText(String.format("%.2f", new_val));

            }
        });

        sepia.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = mainController.getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        ((SepiaTone) getColorAdjust(n).getInput())
                                .setLevel(new_val.doubleValue());
                    }
                }
                sepiaValue.setText(String.format("%.2f", new_val));
            }
        });

        saturation.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = mainController.getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        getColorAdjust(n).setSaturation(new_val.doubleValue());
                    }
                }
                saturationValue.setText(String.format("%.2f", new_val));
            }
        });

        contrast.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = mainController.getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        getColorAdjust(n).setContrast(new_val.doubleValue());
                    }
                }
                contrastValue.setText(String.format("%.2f", new_val));
            }
        });

        brightness.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = mainController.getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        getColorAdjust(n).setBrightness(new_val.doubleValue());
                    }
                }
                brightnessValue.setText(String.format("%.2f", new_val));
            }
        });

        blur.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = mainController.getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        setBlurRadius(n, new_val.intValue());
                    }
                }
                blurValue.setText(String.format("%d", new_val.intValue()));
            }
        });

        //
        EventHandler eh = new EventHandler() {
            @Override
            public void handle(Event event) {
                Workspace w = mainController.getCurrentWorkspace();
                if (w != null) {
                    mainController.getCurrentWorkspace().notifyHistory();
                }
            }
        };

        opacity.setOnMouseReleased(eh);
        sepia.setOnMouseReleased(eh);
        saturation.setOnMouseReleased(eh);
        contrast.setOnMouseReleased(eh);
        brightness.setOnMouseReleased(eh);
        blur.setOnMouseReleased(eh);

        // Container for effect buttons and sliders
        GridPane effectsContainer = new GridPane();
        effectsContainer.setPadding(new Insets(15));
        effectsContainer.setBackground(new Background(new BackgroundFill(
                Color.web("#ededed"), CornerRadii.EMPTY, Insets.EMPTY)));
        effectsContainer.setStyle(
                "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.15) , 3 ,0 , 3 , 3 );");
        effectsContainer.setHgap(5);
        effectsContainer.setVgap(10);
        effectsContainer.setLayoutX(-10000);
        effectsContainer.setLayoutY(0);
        // Add it to the mainAnchorPane
        mainController.getMainPane().getChildren().add(effectsContainer);

        // GridPane to contain the effects buttons, not the sliders
        GridPane effectButtonsContainer = new GridPane();
        effectButtonsContainer.setPrefWidth(153);
        effectButtonsContainer.setMaxWidth(153);
        effectButtonsContainer.setHgap(10);
        // Add column constraints
        for (int i = 0; i < 3; ++i) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(100 / 3.0);
            c.setHgrow(Priority.SOMETIMES);
            c.setMinWidth(10);
            c.setMaxWidth(100);
            effectButtonsContainer.getColumnConstraints().add(c);
        }

        // Create filter button
        Button BW = createToolButton("B&W", effectButtonsContainer);
        BW.setTooltip(new Tooltip("Apply a black & white filter"));
        BW.setOnAction((ActionEvent e) -> {
            Workspace w = mainController.getCurrentWorkspace();
            if (w != null) {
                for (Node n : w.getCurrentLayers()) {
                    getColorAdjust(n).setSaturation(-1);
                    saturation.setValue(-1);
                }
                w.notifyHistory();

                toolbarController.displayToolSetting(null);
            }
        });

        // Create filter button
        Button tint = createToolButton("Tint", effectButtonsContainer);
        tint.setTooltip(new Tooltip("Apply a color filter of the current color"));
        tint.setOnAction((ActionEvent e) -> {
            Workspace w = mainController.getCurrentWorkspace();
            if (w != null) {
                for (Node n : w.getCurrentLayers()) {
                    //Algorithm to convert color to hue:
                    //https://stackoverflow.com/questions/31587092
                    //Get hue between 0-360
                    double hue = ColorSet.getInstance().getColor().getHue();
                    //Add 180 and modulo 360 to get target colour
                    hue = (hue + 180) % 360;
                    //Map hue between -1 and 1
                    hue = -1 + 2 * (hue / 360);

                    //Finally, set the hue to node
                    getColorAdjust(n).setHue(hue);
                }
                w.notifyHistory();

                toolbarController.displayToolSetting(null);
            }
        });

        // Create filter button
        Button reset = createToolButton("Reset", effectButtonsContainer);
        reset.setTooltip(new Tooltip("Reset all color effects"));
        reset.setOnAction((ActionEvent e) -> {
            Workspace w = mainController.getCurrentWorkspace();
            if (w != null) {
                for (Node n : w.getCurrentLayers()) {
                    ColorAdjust c = getColorAdjust(n);
                    c.setHue(0);
                    setBlurRadius(n, 0);
                }
                w.notifyHistory();

                opacity.setValue(1);
                saturation.setValue(0);
                sepia.setValue(0);
                contrast.setValue(0);
                brightness.setValue(0);

                toolbarController.displayToolSetting(null);
            }
        });

        // Add the sliders to the effects container
        createSlider(effectsContainer, "Opacity:", opacity, opacityValue, 1);
        createSlider(effectsContainer, "Sepia:", sepia, sepiaValue, 2);
        createSlider(effectsContainer, "Saturation:", saturation, saturationValue, 3);
        createSlider(effectsContainer, "Contrast:", contrast, contrastValue, 4);
        createSlider(effectsContainer, "Brightness:", brightness, brightnessValue, 5);
        createSlider(effectsContainer, "Blur", blur, blurValue, 6);

        // Add the buttons on the first row
        effectsContainer.add(effectButtonsContainer, 0, 0);
        GridPane.setColumnSpan(effectButtonsContainer, 3);

        // Create a button to toggle the effect panel
        Button effectsToggl = createToolButton("Effects", gridFilterTools);
        effectsToggl.setTooltip(new Tooltip("Open/Close effects panel"));
        effectsToggl.setPrefWidth(160);
        effectsToggl.setPrefHeight(45);
        // Set the toggle action
        effectsToggl.setOnAction((ActionEvent e) -> {
            // If the layoutX property >= 0, then we assume the container is visible
            if (effectsContainer.getLayoutX() >= 0) {
                //Hide the container
                effectsToggl.getStyleClass().remove("selected");
                effectsContainer.setLayoutX(-10000);
                effectsContainer.setLayoutY(0);
            } else { // The container is not visible
                effectsToggl.getStyleClass().add("selected");

                // Get height of the window
                double windowHeight = mainController.getMainPane()
                        .getBoundsInParent().getHeight();

                // Get the height of the container  
                double containerHeight = effectsContainer.getBoundsInParent()
                        .getHeight();

                // Get the ideal position of the panel
                double posX = effectsToggl.localToScene(effectsToggl.
                        getBoundsInLocal()).getMinX();
                double posY = effectsToggl.localToScene(effectsToggl.
                        getBoundsInLocal()).getMaxY();

                // If the container would overflow from the window
                if (posY + containerHeight > windowHeight) {
                    posX = 180;
                    posY = windowHeight - containerHeight;
                }

                // Set the container position
                effectsContainer.setLayoutX(posX);
                effectsContainer.setLayoutY(posY);
            }
        });

    }

    //Create a tool button and add it in the corresponding grid pane
    private Button createToolButton(String text, GridPane pane) {
        Button button = new Button(text);

        // Calculate the button's position in the grid
        int row = pane.getChildren().size() / 3;
        int col = pane.getChildren().size() % 3;

        // Add a buttons row if needed
        if (row > pane.getRowConstraints().size() - 1) {
            pane.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        }

        button.setPrefHeight(Double.MAX_VALUE);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setPadding(new Insets(0, 0, 0, 0));
        button.getStyleClass().add("tool-button");

        pane.add(button, col, row);

        return button;
    }

    /*Creates a slider in a pane at a certain position. Used to create opacity,
     sepia, saturation and contrast sliders.*/
    private void createSlider(GridPane pane, String string, Slider slider,
            Label value, int position) {
        Label label = new Label(string);
        label.setMinWidth(50);
        value.setMinWidth(30);
        GridPane.setConstraints(label, 0, position);
        pane.getChildren().add(label);
        GridPane.setConstraints(slider, 1, position);
        pane.getChildren().add(slider);
        GridPane.setConstraints(value, 2, position);
        pane.getChildren().add(value);
    }


    /* Returns node ColorAdjust effect. If it has none, creates one with SepiaTone as
     input, which itself has a GaussianBlur as input.*/

    private ColorAdjust getColorAdjust(Node n) {
        if (!(n.getEffect() instanceof ColorAdjust)) {
            ColorAdjust c = new ColorAdjust();
            SepiaTone s = new SepiaTone(0);
            GaussianBlur g = new GaussianBlur(0);
            s.setInput(g);
            c.setInput(s);
            n.setEffect(c);
        }
        return ((ColorAdjust) n.getEffect());
    }

    //Sets GaussianBlur radius to desired amount for a given node.
    private void setBlurRadius(Node n, int i) {
        ((GaussianBlur) (((SepiaTone) getColorAdjust(n).getInput()))
                .getInput()).setRadius(i);
    }

    public void clearSelectedButtons() {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected");
            selectedButton = null;
        }
    }

    private void selectButton(Button b) {
        clearSelectedButtons();
        selectedButton = b;
        b.getStyleClass().add("selected");
    }

    /*Action when clicked on new canvas button. Create a canvas and add it to the
     current workspace's layer list*/

    @FXML
    private void newCanvasButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            w.addLayer(new CanvasN(w.width(), w.height()));
            toolbarController.displayToolSetting(null);
            w.notifyHistory();
        }
    }


     /*Action when clicked on new image button. Import an image and add it to the
     current workspace's layer list*/

    @FXML
    private void newImageButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            ImportImageDialog dialog = new ImportImageDialog(
                    mainController.getStage());
            Image image = dialog.showAndWait();
            if (image != null) {
                ImageN i = new ImageN(image);
                i.setViewport(new Rectangle2D(0, 0, image.getWidth(),
                        image.getHeight()));
                w.addLayer(i);
                toolbarController.displayToolSetting(null);
                w.notifyHistory();
            }
        }
    }

    /*Action when clicked on new text button. Create a text and add it to the
     current workspace's layer list*/
    @FXML
    private void newTextButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            Optional<String> result = TextTool.getDialogText(null);
            if (result.isPresent()) {
                TextN t = new TextN(w.width() / 2, w.height() / 2,
                        result.get());
                t.setFill(textColor.getColor());
                t.setFont(textFont.getFont());
                t.setTranslateX(-t.getBoundsInParent().getWidth() / 2);
                w.addLayer(t);
                toolbarController.displayToolSetting(null);
                w.notifyHistory();
            }
        }
    }

    //Action when clicked on brush button. Set the current tool with a brush tool

    @FXML
    private void brushButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            Brush b = new Brush(w);
            w.setCurrentTool(b);
            brushSizer.setTarget(b);
            toolbarController.displayToolSetting(brushSettings);
        }
    }

    @FXML
    private void lineAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            LineTool b = new LineTool(w);
            w.setCurrentTool(b);
            brushSizer.setTarget(b);
            toolbarController.displayToolSetting(brushSettings);
        }
    }
    //Action when clicked on eraser button. Set the current tool with an eraser tool

    @FXML
    private void eraserButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            Eraser er = new Eraser(w);
            w.setCurrentTool(er);
            eraserSizer.setTarget(er);
            toolbarController.displayToolSetting(eraserSettings);
        }
    }

    //Action when clicked on eye dropper button. Set the current tool with an eyedropper tool

    @FXML
    private void eyeDropperButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            w.setCurrentTool(new EyeDropper(w));
            toolbarController.displayToolSetting(null);
        }
    }

    @FXML
    private void textButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            TextTool t = new TextTool(w);
            w.setCurrentTool(t);
            textColor.setTarget(t);
            textFont.setTarget(t);
            toolbarController.displayToolSetting(textSettings);
        }
    }


     /*Action when clicked on horizontal symmetry button. Tranform all selected
     layers with horizontal symmetry*/

    @FXML
    private void hSymmetryButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            for (Node node : w.getCurrentLayers()) {
                // If the node is a text, use the special formula for GEMMSTexts
                if (node instanceof TextN) {
                    TextN t = (TextN) node;
                    t.getTransforms().add(new Rotate(180, t.getX() +
                            t.getBoundsInLocal().getWidth() / 2, t.getY() +
                                    t.getBoundsInLocal().getHeight() / 2, 0, Rotate.Y_AXIS));
                } else {
                    node.getTransforms().add(new Rotate(180,
                            node.getBoundsInLocal().getWidth() / 2,
                            node.getBoundsInLocal().getHeight() / 2,
                            0, Rotate.Y_AXIS));
                }
            }
            toolbarController.displayToolSetting(null);
            w.notifyHistory();
        }
    }


     /*Action when clicked on vertical symmetry button. Tranform all selected layers
     with vertical symmetry*/

    @FXML
    private void vSymmetryButtonAction(ActionEvent e) {
        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            // If the node is a text, use the special formula for GEMMSTexts
            for (Node node : w.getCurrentLayers()) {

                if (node instanceof TextN) {
                    TextN t = (TextN) node;
                    t.getTransforms().add(new Rotate(180, t.getX()
                            + t.getBoundsInLocal().getWidth() / 2, t.getY()
                            + t.getBoundsInLocal().getHeight() / 2, 0,
                            Rotate.X_AXIS));

                } else {
                    node.getTransforms().add(new Rotate(180,
                            node.getBoundsInLocal().getWidth() / 2,
                            node.getBoundsInLocal().getHeight() / 2,
                            0, Rotate.X_AXIS));
                }
            }
            toolbarController.displayToolSetting(null);
            w.notifyHistory();
        }
    }

    //Action when clicked on drag button. Set the current tool with a drag tool
    @FXML
    private void dragButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            Drag dragTool = new Drag(w);
            w.setCurrentTool(dragTool);

            //add setting alignement lines for drag
            HBox hBox = new HBox();
            Button activeAlignement = new Button("Alignement: Off");
            activeAlignement.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dragTool.turnAlignementOnOff();
                    activeAlignement.setText("Alignement: " +
                            (dragTool.isAlignementActive() ? "On" : "Off"));
                }
            });
            hBox.getChildren().addAll(activeAlignement);

            toolbarController.displayToolSetting(hBox);
        }
    }

    // Action when clicked on rotate button. Set the current tool with a rotate tool
    @FXML
    private void rotateButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            w.setCurrentTool(new myApp.model.tool.Rotate(w));
            toolbarController.displayToolSetting(null);
        }
    }

    //Action when clicked on scale button. Set the current tool with a scale tool
    @FXML
    private void scaleButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            w.setCurrentTool(new Resize(w));
            toolbarController.displayToolSetting(null);
        }
    }


    @FXML
    private void selectionButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            w.setCurrentTool(new Selection(w));
            toolbarController.displayToolSetting(null);
        }
    }

    @FXML
    private void trangleSelectionAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            w.setCurrentTool(new PolygonSelection(w));
            toolbarController.displayToolSetting(null);
        }
    }

    @FXML
    private void circlrSelectionButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            w.setCurrentTool(new CircleSelection(w));
            toolbarController.displayToolSetting(null);
        }
    }

    // Action when clicked on crop button. Set the current tool with a crop tool
    @FXML
    private void cropButtonAction(ActionEvent e) {
        Button source = (Button) e.getSource();

        Workspace w = mainController.getCurrentWorkspace();
        if (w != null) {
            selectButton(source);
            w.setCurrentTool(new Crop(w));
            toolbarController.displayToolSetting(null);
        }
    }

}

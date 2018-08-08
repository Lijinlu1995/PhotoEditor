package myApp.model.tool.settings;

import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/* ToolFontSettings manage FontConfigurableTools. Its purpose is to offer JavaFx
 * control elements to change the size and the family of the targeted tool.*/
public class ToolFontSettings extends ToolSettings {

    // Tool to manage
    private FontConfigurableTool target = null;

    // List of available font for the user
    private ComboBox<String> cb;

    // JavaFx Slider to choose the size of the font
    private Slider slider;

    /* Constructor. It creates the ComboBox to list available fonts, the slider to
     * choose font size.*/
    public ToolFontSettings(int min, int max, int value) {
        // Center elements vertically
        setAlignment(Pos.CENTER_LEFT);

        // Create list of fonts
        cb = new ComboBox<>();
        List<String> fontFamilies = javafx.scene.text.Font.getFamilies();
        for (String font : javafx.scene.text.Font.getFamilies()) {
            cb.getItems().add(font);
        }
        // Choose a font by default
        cb.setValue(fontFamilies.get(0));
        // Add listener on change of selected property
        cb.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue value, String old_value, String new_value) {
                if (target != null) {
                    target.setFont(Font.font(cb.getValue(), slider.getValue()));
                }
            }
        });

        // Create the slider
        slider = new Slider(min, max, value);

        // Text to display the current value of the slider
        final Text textValue = new Text(String.valueOf(value + "px"));

        // Create the event on slider change
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                if (target != null) {
                    textValue.setText(String.format("%dpx", new_val.intValue()));
                    target.setFont(Font.font(cb.getValue(), new_val.intValue()));
                }
            }
        });

        getChildren().add(slider);
        getChildren().add(textValue);
        getChildren().add(cb);
    }

    /* Set the current targeted tool. The ToolFonSettings instance checks if the
     * newly targeted tool has a Font, in which case it updates its own information
     * accordingly. If not, it sets the tool font information using its current
     * settings.*/
    public void setTarget(FontConfigurableTool target) {
        this.target = target;

        // Check if the targeted tool already has a valid font
        Font font = target.getFont();
        if (font == null) {
            // Set the target font
            target.setFont(Font.font(cb.getValue(), slider.getValue()));
        } else {
            // Update properties to represent the tool
            cb.setValue(font.getFamily());
            slider.setValue(font.getSize());
        }
    }

    //Get the current ToolFontSettings font parameters.s
    public Font getFont() {
        return Font.font(cb.getValue(), (int) slider.getValue());
    }
}

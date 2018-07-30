package myApp.tool.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

/**
 * <h1>ToolSizeSettings</h1>
 *
 * ToolSizeSettings are object that manage the size of a targeted tool. They use a
 * Slider for the user to choose a size, and update the tool accordingly.
 */
public class ToolSizeSettings extends ToolSettings {
    // The target to configure

    private SizeConfigurableTool target = null;
    // The slider to set the size
    private final Slider slider;

    /**
     * Constructor. It creates the Slider and adds a text to display the Slider
     * current value.
     *
     * @param min the min value of the Slider
     * @param max the may value of the Slider
     * @param value the default value of the Slider
     */
    public ToolSizeSettings(int min, int max, int value) {
        // Create the slider
        slider = new Slider(min, max, value);

        // Text to display the Slider current value
        final Text textValue = new Text(String.valueOf(value + "px"));

        // Create the event on slider change
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                if (target != null) {
                    // Update the text and set the size of the target
                    textValue.setText(String.format("%dpx", new_val.intValue()));
                    target.setSize(new_val.intValue());
                }
            }
        });

        getChildren().add(slider);
        getChildren().add(textValue);
    }

    /**
     * Set the target of the ToolSettings. It updates the target size accordingly to
     * the Slider current value.
     *
     * @param target
     */
    public void setTarget(SizeConfigurableTool target) {
        this.target = target;
        target.setSize((int) slider.getValue());
    }

    /**
     * Get the current size of the ToolSizeSettings instance.
     *
     * @return the current size
     */
    public int getSize() {
        return (int) slider.getValue();
    }
}

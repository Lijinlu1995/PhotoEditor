package myApp.base;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class Constants {

    //define the basic size and shortcut
    public static double BUTTONS_HEIGHT = 33;
    public static double HISTORY_THUMB_WIDTH = 120.;

    public static KeyCodeCombination DELETE = new KeyCodeCombination(KeyCode.DELETE);
    public static KeyCodeCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
    public static KeyCodeCombination CTRL_C = new KeyCodeCombination(KeyCode.C,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_V = new KeyCodeCombination(KeyCode.V,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_Z = new KeyCodeCombination(KeyCode.Z,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_Y = new KeyCodeCombination(KeyCode.Y,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_N = new KeyCodeCombination(KeyCode.N,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_O = new KeyCodeCombination(KeyCode.O,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_S = new KeyCodeCombination(KeyCode.S,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_E = new KeyCodeCombination(KeyCode.E,
            KeyCombination.CONTROL_ANY);
    public static KeyCodeCombination CTRL_R = new KeyCodeCombination(KeyCode.R,
            KeyCombination.CONTROL_ANY);
}

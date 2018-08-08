package myApp.model.layer;

import myApp.model.base.CSSIcons;
import myApp.model.workspace.LayerListable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.geometry.Point3D;
import javafx.geometry.VPos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;


public class TextN extends javafx.scene.text.Text implements NodeN,
        LayerListable {

    private static int layerCount = 0;
    private String name = "Text " + ++layerCount;
    public static final int DEFAULT_SIZE = 12;

    public TextN() {
        super();
    }

    public TextN(String text) {
        this(0, 0, text);
    }

    public TextN(double x, double y, String text) {
        super(x, y, text);
        setTextOrigin(VPos.CENTER);
        setTextAlignment(TextAlignment.CENTER);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // Write the text
        s.writeObject(getText());

        // Write the position
        s.writeDouble(getX());
        s.writeDouble(getY());

        // Write align property
        s.writeObject(getTextOrigin());
        s.writeObject(getTextAlignment());

        // Write font
        s.writeObject(getFont().getFamily());
        s.writeDouble(getFont().getSize());

        // Write fill info
        s.writeObject(getFill().toString());

        // Write translate info
        s.writeDouble(getTranslateX());
        s.writeDouble(getTranslateY());
        s.writeDouble(getTranslateZ());

        // Write scale info
        s.writeDouble(getScaleX());
        s.writeDouble(getScaleY());
        s.writeDouble(getScaleZ());

        // Write rotate info
        s.writeDouble(getRotate());
        s.writeDouble(getRotationAxis().getX());
        s.writeDouble(getRotationAxis().getY());
        s.writeDouble(getRotationAxis().getZ());

        //Write effect info
        ColorAdjust c;
        if (getEffect() instanceof ColorAdjust) {
            s.writeBoolean(true);
            c = ((ColorAdjust) getEffect());
            s.writeDouble(c.getContrast());
            s.writeDouble(c.getHue());
            s.writeDouble(c.getSaturation());
            s.writeDouble(c.getBrightness());
            s.writeDouble(((SepiaTone) c.getInput()).getLevel());
            s.writeDouble(((GaussianBlur) ((SepiaTone) c.getInput()).getInput())
                    .getRadius());
        } else {
            s.writeBoolean(false);
        }

        // Write Transformation
        s.writeInt(getTransforms().size());
        for (Transform t : getTransforms()) {
            // Rotate
            if (t instanceof javafx.scene.transform.Rotate) {
                s.writeObject(t.getClass().getSimpleName());
                Rotate rotate = (Rotate) t;
                s.writeDouble(rotate.getAngle());
                s.writeDouble(rotate.getPivotX());
                s.writeDouble(rotate.getPivotY());
                s.writeDouble(rotate.getPivotZ());
                s.writeDouble(rotate.getAxis().getX());
                s.writeDouble(rotate.getAxis().getY());
                s.writeDouble(rotate.getAxis().getZ());
            }
        }
    }

    //Read all informations for serialization

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();

        // Set the test
        setText((String) s.readObject());

        // Set the position
        setX(s.readDouble());
        setY(s.readDouble());

        // Set align
        setTextOrigin((VPos) s.readObject());
        setTextAlignment((TextAlignment) s.readObject());

        // Set the font
        setFont(Font.font((String) s.readObject(), s.readDouble()));

        // Set fill info
        setFill(Paint.valueOf((String) s.readObject()));

        // Set translate info
        setTranslateX(s.readDouble());
        setTranslateY(s.readDouble());
        setTranslateZ(s.readDouble());

        // Set scale info
        setScaleX(s.readDouble());
        setScaleY(s.readDouble());
        setScaleZ(s.readDouble());

        // Set rotate info
        setRotate(s.readDouble());
        setRotationAxis(new Point3D(s.readDouble(), s.readDouble(), s.readDouble()));

        //Boolean to notify if effects are on their way, if so read them and apply
        if (s.readBoolean()) {
            ColorAdjust c = new ColorAdjust();
            c.setContrast(s.readDouble());
            c.setHue(s.readDouble());
            c.setSaturation(s.readDouble());
            c.setBrightness(s.readDouble());
            SepiaTone st = new SepiaTone(s.readDouble());
            st.setInput(new GaussianBlur(s.readDouble()));
            c.setInput(st);
            setEffect(c);
        }

        // Set Transformation
        int sizeTransformation = s.readInt();
        for (int i = 0; i < sizeTransformation; i++) {
            String classOfTransformation = (String) s.readObject();
            switch (classOfTransformation) {
                case "Rotate":
                    double angle = s.readDouble();
                    double pivotX = s.readDouble();
                    double pivotY = s.readDouble();
                    double pivotZ = s.readDouble();
                    double pAxisX = s.readDouble();
                    double pAxisY = s.readDouble();
                    double pAxisZ = s.readDouble();
                    Point3D axis = new Point3D(pAxisX, pAxisY, pAxisZ);
                    getTransforms().add(new Rotate(angle, pivotX, pivotY,
                            pivotZ, axis));
                    break;
            }
        }

    }

    @Override
    public String getLayerName() {
        return name;
    }

    @Override
    public String getThumbnailClass() {
        return CSSIcons.TEXT;
    }

    @Override
    public NodeN clone() {
        return null;
    }

    @Override
    public void setLayerName(String name) {
        this.name = name;
    }
}

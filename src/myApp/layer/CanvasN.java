package myApp.layer;

import myApp.base.CSSIcons;
import myApp.workspace.LayerListable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

/**
 * <h1>GEMMSCanvas</h1>
 *
 * This class was created to implement Serializable
 */
public class CanvasN extends javafx.scene.canvas.Canvas implements NodeN,
        LayerListable {

    private static int layerCount = 0;
    private String name = "Canvas " + ++layerCount;

    /**
     * Constructor
     */
    public CanvasN() {
        super();
    }

    /**
     * Constructor
     *
     * @param width this is the width of this canvas
     * @param height this is the height of this canvas
     */
    public CanvasN(double width, double height) {
        super(width, height);
    }

    /**
     * Write all informations for serialization
     *
     * @param s output stream
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // Get the size
        double width = getWidth();
        double height = getHeight();

        // Write the size
        s.writeDouble(width);
        s.writeDouble(height);

        // Get an image 
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        try {
            // Cancel all tranformation before taking a snapshot
            sp.setTransform(getLocalToParentTransform().createInverse());
        } catch (NonInvertibleTransformException ex) {
            // TODO : Manage exceptions
            Logger.getLogger(CanvasN.class.getName()).log(Level.SEVERE, null, ex);
        }

        WritableImage writableImage = new WritableImage((int) width, (int) height);
        snapshot(sp, writableImage);

        // Get a pixel reader
        PixelReader pixelReader = writableImage.getPixelReader();

        // Write the color of every pixel
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color c = pixelReader.getColor(x, y);

                s.writeDouble(c.getRed());
                s.writeDouble(c.getGreen());
                s.writeDouble(c.getBlue());
                s.writeDouble(c.getOpacity());
            }
        }

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

    /**
     * Read all informations for serialization
     *
     * @param s input stream
     * @throws IOException
     */
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();

        // Get the size of the canvas
        double width = s.readDouble();
        double height = s.readDouble();

        // Set the size of this canvas
        setWidth(width);
        setHeight(height);

        GraphicsContext gc = getGraphicsContext2D();

        // Get the pixel writer
        PixelWriter pixelWriter = gc.getPixelWriter();

        // Set the color every pixel of this canvas
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color c = new Color(s.readDouble(), s.readDouble(), s.readDouble(),
                        s.readDouble());
                pixelWriter.setColor(x, y, c);
            }
        }

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
        return CSSIcons.CANVAS;
    }

    @Override
    public void setLayerName(String name) {
        this.name = name;
    }
}

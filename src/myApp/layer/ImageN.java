package myApp.layer;

import myApp.base.CSSIcons;
import myApp.workspace.LayerListable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

/**
 * <h1>GEMMSImage</h1>
 *
 * This class was created to implement Serializable
 */
public class ImageN extends javafx.scene.image.ImageView implements NodeN,
        LayerListable {

    private static int layerCount = 0;
    private String name = "Image " + ++layerCount;

    /**
     * Constructor
     *
     * Allocates a new ImageView object.
     */
    public ImageN() {
        super();
    }

    /**
     * Constructor
     *
     * Allocates a new ImageView object using the given image.
     *
     * @param image load this image
     */
    public ImageN(Image image) {
        super(image);
    }

    /**
     * Constructor
     *
     * Allocates a new ImageView object with image loaded from the specified URL.
     *
     * @param url load an image with url
     */
    public ImageN(String url) {
        super(url);
    }

    /**
     * Write all informations for serialization
     *
     * @param s output stream
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // Get image
        Image image = getImage();

        // Write the size
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        s.writeInt(width);
        s.writeInt(height);

        // Get a pixel reader
        PixelReader pixelReader = image.getPixelReader();

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

        // Write viewport
        Rectangle2D r = getViewport();
        s.writeDouble(r.getMinX());
        s.writeDouble(r.getMinY());
        s.writeDouble(r.getWidth());
        s.writeDouble(r.getHeight());

        // Write translate info
        s.writeDouble(getTranslateX());
        s.writeDouble(getTranslateY());
        s.writeDouble(getTranslateZ());

        // Write scale info
        s.writeDouble(getScaleX());
        s.writeDouble(getScaleY());
        s.writeDouble(getScaleZ());

        // Wrtie rotate info
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

        // Get image size
        int width = s.readInt();
        int height = s.readInt();

        // Create an empty image
        WritableImage w = new WritableImage(width, height);

        // Get the pixel writer
        PixelWriter pixelWriter = w.getPixelWriter();

        // Set the color every pixel of this image
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color c = new Color(s.readDouble(), s.readDouble(), s.readDouble(),
                        s.readDouble());
                pixelWriter.setColor(x, y, c);
            }
        }

        setImage(w);

        setViewport(new Rectangle2D(s.readDouble(), s.readDouble(), s.readDouble(),
                s.readDouble()));

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
                    getTransforms().add(new Rotate(angle, pivotX,
                            pivotY, pivotZ, axis));
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
        return CSSIcons.IMAGE;
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

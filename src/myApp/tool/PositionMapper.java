package myApp.tool;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * <h1>PositionMapper</h1>
 *
 * Provides function to convert coordinates to Node coordinates
 */
public class PositionMapper {

    /**
     * Convert mouse coordinates to Node coordinates (Apply node transformation)
     *
     * @param node Node as reference
     * @param x x position
     * @param y y position
     * @param z z position
     *
     * @return converted coordinates
     */
    public static Point3D convert(Node node, double x, double y, double z) {
        // Bounds
        Bounds bounds = node.getBoundsInLocal();

        // Mouse position
        Point3D point = new Point3D(x, y, z);

        // Translate
        Translate translate = new Translate(-node.getTranslateX(), -node.getTranslateY(), -node.getTranslateZ());
        point = translate.transform(point);

        // Rotate
        Rotate rotate = new Rotate(-node.getRotate());
        rotate.setPivotX(bounds.getWidth() / 2);
        rotate.setPivotY(bounds.getHeight() / 2);
        rotate.setPivotZ(bounds.getDepth() / 2);
        rotate.setAxis(node.getRotationAxis());
        point = rotate.transform(point);

        // Scale
        Scale scale = new Scale(1 / node.getScaleX(), 1 / node.getScaleY());
        scale.setPivotX(bounds.getWidth() / 2);
        scale.setPivotY(bounds.getHeight() / 2);
        scale.setPivotZ(bounds.getDepth() / 2);
        point = scale.transform(point);

        // Transform
        for (Transform t : node.getTransforms()) {
            try {
                Transform inverseTranform = t.createInverse();
                point = inverseTranform.transform(point);
            } catch (NonInvertibleTransformException e) {
                // TODO : Manage exceptions
                e.printStackTrace();
            }
        }

        return point;
    }

    /**
     * Convert mouse coordinates to Node coordinates (Apply node transformation)
     *
     * @param node Node as reference
     * @param point coordinates to convert
     *
     * @return converted coordinates
     */
    public static Point3D convert(Node node, Point3D point) {
        return convert(node, point.getX(), point.getY(), point.getZ());
    }
}

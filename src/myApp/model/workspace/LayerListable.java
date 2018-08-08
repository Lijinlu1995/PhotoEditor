package myApp.model.workspace;

/**
 * <h1>LayerListable</h1>
 *
 * This interface reprsents objects that can be listed inside the workspace Listview.
 */
public interface LayerListable {

    /**
     * This method should return the name of the layer that will be displayed in the
     * ListView.
     *
     * @return The name of the layer
     */
    public String getLayerName();

    /**
     * This method should save the name of the layer in a permanent way so that
     * saving a document would preserve the name for example.
     *
     * @param name the new name of the layer
     */
    public void setLayerName(String name);

    /**
     * This method should return the CSS class to apply to the thumbnail in the
     * ListView. Typically it could be a class that adds an icon to the thumbnail or
     * that changes its background color.
     *
     * @return the class name as a String.
     */
    public String getThumbnailClass();
}

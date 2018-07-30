package myApp.tool.settings;

/**
 * <h1>SizeConfigurableTool</h1>
 *
 * SizeConfigurableTools represent tools that can be resized.
 */
public interface SizeConfigurableTool {

    /**
     * Set the size of the object
     *
     * @param size
     */
    public void setSize(int size);

    /**
     * Return the current size of the tool
     *
     * @return
     */
    public int getSize();
}

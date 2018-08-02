package myApp.tool.settings;

//SizeConfigurableTools represent tools that can be resized.

public interface SizeConfigurableTool {

    //Set the size of the object
    public void setSize(int size);

    // Return the current size of the tool
    public int getSize();
}

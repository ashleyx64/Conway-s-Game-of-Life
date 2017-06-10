package gameoflife;

/**
 * Stores information about a machine
 * @author Ashley Allen
 */
public class Machine {
    private final String name;
    private final int width;
    private final int height;
    private final boolean[][] template;
    
    /**
     * Constructs a new machine with the specified parameters
     * @param name the name of the machine
     * @param width the width of the machine
     * @param height the height of the machine
     * @param template the template for constructing the machine
     */
    public Machine(String name, int width, int height, boolean[][] template) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.template = template;
    }

    /**
     * Gets the name of the machine
     * @return the name of the machine
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the width of the machine
     * @return the width of the machine
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the machine
     * @return the height of the machine
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Gets the template for constructing the machine
     * @return the template for constructing the machine
     */
    public boolean[][] getTemplate() {
        return template;
    }
    
    
}

package gameoflife;

/**
 * The class used to represent an alive cell in the game. It only stores its
 * x and y position for reference
 * @author Ashley
 */
public class Cell {
    private int x, y;
    
    /**
     * Default constructor
     * @param x the x coordinate of the new cell
     * @param y the y coordinate of the new cell
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Set the value of x
     * @param x the x value to set
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Get the value of x
     * @return the value of x
     */
    public int getX() {
        return x;
    }
    
    /**
     * Set the value of y
     * @param y the y value to set
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Get the value of y
     * @return the value of y
     */
    public int getY() {
        return y;
    }
}

package gameoflife;

/**
 * Stores x and y coordinates
 * @author Ashley Allen
 */
public class Coordinates {
    private final int x;
    private final int y;
    
    /**
     * Creates a new coordinate pair given x and y integer values
     * @param x the x coordinate of the coordinate pair
     * @param y the y coordinate of the coordinate pair
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Gets the x coordinate
     * @return x
     */
    public int getX() {
        return x;
    }
    
    /**
     * Gets the y coordinate
     * @return y
     */
    public int getY() {
        return y;
    }
    
    /**
     * Checks whether two coordinate objects are equal
     * @param coords the coordinates to check against
     * @return true if their x and y values are equal
     */
    public boolean equals(Coordinates coords) {
        if (coords == null) {
            return false;
        }
        return this.x == coords.x && this.y == coords.y;
    }
    
    /**
     * Returns coordinates after adding the specified values to the x and y
     * coordinates
     * @param x the value to add to the x coordinate
     * @param y the value to add to the y coordinate
     * @return a new Coordinates object after addition
     */
    public Coordinates add(int x, int y) {
        return new Coordinates(this.x + x, this.y + y);
    }
}

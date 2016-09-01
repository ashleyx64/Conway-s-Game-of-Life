package gameoflife;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The class that handles all the game logic including: drawing the game to the
 * canvas, updating the state of the cells after each generation and processing
 * the zoom level of the grid
 * @author Ashley
 */
public class GameController {
    private int gridWidth, gridHeight;
    private final double gameWidth, gameHeight;
    private double hFactor, vFactor;
    private final Map<String, Cell> cells = new HashMap<>();
    private final List<Integer[]> marks = new ArrayList<>(), checks = new ArrayList<>();
    private final GraphicsContext gc;
    
    /**
     * Default constructor
     * @param gridWidth the initial width of the game grid
     * @param gridHeight the initial height of the game grid
     * @param gc the GraphicsContext that used to display the game
     * @param example true if an example should be generated on startup
     */
    public GameController(int gridWidth, int gridHeight, GraphicsContext gc, boolean example) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        gameWidth = gc.getCanvas().getWidth();
        gameHeight = gc.getCanvas().getHeight();
        this.gc = gc;
        hFactor = gameWidth / gridWidth;
        vFactor = gameHeight / gridHeight;
        if (example) {
            placeExample();
        }
    }
    
    private void placeExample() {
        //Reads an example machine from exampleTemplate.txt and then writes it
        //to the game
        Scanner scanner;
        try {
            scanner = new Scanner(new File("src\\gameoflife\\exampleTemplate.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        int startX = gridWidth / 2 - 7, startY = gridHeight / 2 - 7;
        for (int y = 0; y < 13; y++) {
            String line = scanner.next();
            for (int x = 0; x < 13; x++) {
                if (line.charAt(x) == 'X') {
                    int cx = x + startX, cy = y + startY;
                    cells.put(getHashKey(cx, cy), new Cell(cx, cy));
                }
            }
        }
    }
    
    /**
     * Draws the game to the specified GraphicsContext given in the constructor
     */
    public void drawGame() {
        //For the size of the grid determine whether there is a cell at each
        //coordinate and draw the corresponding colour
        gc.setStroke(Color.SILVER);
        gc.setLineWidth(2);
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (cells.containsKey(getHashKey(x, y))) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.fillRect(x * hFactor, y * vFactor, hFactor, vFactor);
                gc.strokeRect(x * hFactor, y * vFactor, hFactor, vFactor);
            }
        }
    }
    
    /**
     * Updates the state of all alive cells and their neighbour cells,
     * essentially it applies the rules of Conway's Game of Life to each cell
     * to be checked and then marks it for toggling before toggling all
     * marked cells
     */
    public void updateStates() {
        //Add all alive cells and their surrounding cells to a check list
        cells.entrySet().stream().forEach((entry) -> {
            Cell cell = entry.getValue();
            int x = cell.getX(), y = cell.getY();
            for (int cx = x - 1; cx < x + 2; cx++) {
                for (int cy = y - 1; cy < y + 2; cy++) {
                    Integer[] coords = {cx, cy};
                    if (!contains(checks, coords)) {
                        checks.add(coords);
                    }
                }
            }
        });
        
//        checks.stream().forEach((coords) -> System.out.print("[" + coords[0] + ", " + coords[1] + "]"));
        
        //For each pair of coordinate in checks compute whether the state
        //of the cell at those coordinates should be toggled or not and mark
        //those coordinates if so
        checks.stream().forEach((coords) -> {
            int x = coords[0], y = coords[1];
            String hashKey = getHashKey(x, y);
            int adjCells = getAdjCells(x, y);
//            System.out.println("Cell at " + x + ", " + y + " has " + adjCells + " adjacent cells");
            if (cells.containsKey(hashKey)) {
                if (adjCells < 2 || adjCells > 3) {
                    marks.add(coords);
                }                
            } else {
                if (adjCells == 3) {
                    marks.add(coords);
                }
            }
        });
        checks.clear();
        
        //For each pair of coordinates in marks toggle the cell at those
        //coordinates
        marks.stream().forEach((coords) -> {
            toggleCell(coords[0], coords[1]);
        });
        marks.clear();
    }
    
    private boolean contains(List<Integer[]> list, Integer[] elem) {
        return list.stream().anyMatch((listElem) -> Arrays.equals(listElem, elem));
    }
    
    private int getAdjCells(int x, int y) {
        int sum = 0;
        for (int cx = x - 1; cx < x + 2; cx++) {
            for (int cy = y - 1; cy < y + 2; cy++) {
                if (!(cx == x && cy == y)) {
                    if (cells.containsKey(getHashKey(cx, cy))) {
                        sum++;
                    }
                }
            }
        }
        return sum;
    }
    
    /**
     * Toggles a cell at the specified coordinates, more precisely it creates
     * a cell at the specified coordinates if none exists or removes a cell
     * from the specified coordinates if one exists
     * @param x the x coordinate of the cell to toggle
     * @param y the y coordinate of the cell to toggle
     */
    public void toggleCell(int x, int y) {
        String hashKey = getHashKey(x, y);
        if (cells.containsKey(hashKey)) {
            cells.remove(hashKey);
        } else {
            cells.put(hashKey, new Cell(x, y));
        }
    }
    
    /**
     * Converts a given x window coordinate relative to the game grid into a
     * x grid coordinate
     * @param x the window coordinate to be converted
     * @return the converted coordinate
     */    
    public int convertX(double x) {
        return (int) (x / hFactor);
    }
    
    /**
     * Converts a given y window coordinate relative to the game grid into a
     * y grid coordinate
     * @param y the window coordinate to be converted
     * @return the converted coordinate
     */
    public int convertY(double y) {
        return (int) (y / vFactor);
    }
    
    /**
     * Clears the cell storage
     */
    public void clear() {
        cells.clear();
    }
    
    /**
     * Changes to size of the active game grid viewed on the canvas by 1 in
     * each dimension
     * @param increase true if the game should be zoomed in and false otherwise
     */
        
    public void changeZoom(boolean increase) {
        if (increase) {
            if (gridWidth < 100 && gridHeight < 100) {
                gridWidth++;
                gridHeight++;
            }
        } else {
            if (gridWidth > 5 && gridHeight > 5) {
                gridWidth--;
                gridHeight--;
            }
        }
        hFactor = gameWidth / gridWidth;
        vFactor = gameHeight / gridHeight;
    }
    
    private String getHashKey(int x, int y) {
        return "" + x + "_" + y;
    }
    
    /**
     * Returns the number of currently alive cells
     * @return the number of currently alive cells
     */
    public int getNumCells() {
        return cells.size();
    }
}

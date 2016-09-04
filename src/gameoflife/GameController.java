package gameoflife;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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
    private Map<String, Integer[]> cells = new HashMap<>(), newCells = new HashMap<>(), checks = new HashMap<>();
    private final GraphicsContext gc;
    private final List<Object[]> machines = new ArrayList<>();
    
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
        
        try (Scanner sc = new Scanner(this.getClass().getResourceAsStream("exampleMachines.txt"))) {
            while (sc.hasNext()) {
                String machineName = sc.next();
                int x = sc.nextInt(), y = sc.nextInt();
                boolean[][] machineTemplate = new boolean[y][x];
                for (int i = 0; i < y; i++) {
                    for (int j = 0; j < x; j++) {
                        machineTemplate[i][j] = sc.next().charAt(0) == 'X';
                    }
                }
                machines.add(new Object[] {machineName, x, y, machineTemplate});
            }
        }
        
        drawGrid();
    }
    
    /**
     * Draws the game to the specified GraphicsContext given in the constructor
     */
    public final void drawGrid() {
        //For the size of the grid determine whether there is a cell at each
        //coordinate and draw the corresponding colour
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (cells.get(getHashKey(x, y)) == null) {
                    drawSquare(x, y, Color.WHITE, Color.SILVER);
                } else {
                    drawSquare(x, y, Color.BLACK, Color.SILVER);
                }
            }
        }
    }
    
    private void drawSquare(int x, int y, Paint fill, Paint stroke) {
        gc.setLineWidth(2);
        gc.setStroke(stroke);
        gc.setFill(fill);
        gc.fillRect(x * hFactor, y * vFactor, hFactor, vFactor);
        gc.strokeRect(x * hFactor, y * vFactor, hFactor, vFactor);
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
            Integer[] coords = entry.getValue();
            int x = coords[0], y = coords[1];
            for (int cx = x - 1; cx < x + 2; cx++) {
                for (int cy = y - 1; cy < y + 2; cy++) {
                    checks.putIfAbsent(getHashKey(cx, cy), new Integer[] {cx, cy});
                }
            }
        });
        
//        checks.stream().forEach((coords) -> System.out.print("[" + coords[0] + ", " + coords[1] + "]"));
        
        //For each pair of coordinate in checks compute whether the state
        //of the cell at those coordinates should be toggled or not and mark
        //those coordinates if so
        checks.entrySet().stream().forEach((entry) -> {
            Integer[] coords = entry.getValue();
            int x = coords[0], y = coords[1];
            String hashKey = getHashKey(x, y);
            int adjCells = getAdjCells(x, y);
//            System.out.println("Cell at " + x + ", " + y + " has " + adjCells + " adjacent cells");  
            if (cells.get(hashKey) == null) {
                if (adjCells == 3) {
                    newCells.putIfAbsent(hashKey, coords);
                }
            } else {
                if (adjCells >= 2 && adjCells <= 3) {
                    newCells.putIfAbsent(hashKey, coords);
                }
            }            
        });
        checks.clear();
        
        //Draw any cell changes
        drawChanges(newCells, cells, Color.BLACK);
        drawChanges(cells, newCells, Color.WHITE);
        
        //Copy the new map into the old map and clear the new map
        cells = new HashMap<>(newCells);
        newCells.clear();
    }
    
    private int getAdjCells(int x, int y) {
        int sum = 0;
        for (int cx = x - 1; cx < x + 2; cx++) {
            for (int cy = y - 1; cy < y + 2; cy++) {
                if (!(cx == x && cy == y)) {
                    if (cells.get(getHashKey(cx, cy)) != null) {
                        sum++;
                    }
                }
            }
        }
        return sum;
    }
    
    private void drawChanges(Map<String, Integer[]> map1, Map<String, Integer[]> map2, Paint fill) {
        map1.entrySet().stream().filter((entry) -> {
            Integer[] coords = entry.getValue();
            return map2.get(entry.getKey()) == null && coords[0] >= 0 && coords[0] < gridWidth && coords[1] >= 0 && coords[1] < gridHeight;
        }).forEach((entry) -> {
            Integer[] coords = entry.getValue();
            drawSquare(coords[0], coords[1], fill, Color.SILVER);
        });        
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
        if (cells.get(hashKey) == null) {
            cells.put(hashKey, new Integer[] {x, y});
            drawSquare(x, y, Color.BLACK, Color.SILVER);
        } else {
            cells.remove(hashKey);
            drawSquare(x, y, Color.WHITE, Color.SILVER);
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
        cells.entrySet().stream().forEach((entry) -> {
            Integer[] coords = entry.getValue();
            drawSquare(coords[0], coords[1], Color.WHITE, Color.SILVER);
        });
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
        drawGrid();
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
    
    public void drawMachine(Object[] machine) {
        int machX = (int) machine[1], machY = (int) machine[2];
        int startX = gridWidth / 2 - machX / 2, startY = gridHeight / 2 - machY / 2;
        boolean[][] machineTemplate = (boolean[][]) machine[3];
        for (int i = 0; i < machY; i++) {
            for (int j = 0; j < machX; j++) {
                if (machineTemplate[i][j]) {
                    int x = j + startX, y = i + startY;
                    cells.put(getHashKey(x, y), new Integer[] {x, y});
                    drawSquare(x, y, Color.BLACK, Color.SILVER);
                }
            }
        }
    }
    
    public List<Object[]> getMachines() {
        return machines;
    }
}

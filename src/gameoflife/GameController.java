package gameoflife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * The class that handles all the game logic including: drawing the game to the
 * canvas, updating the state of the cells after each generation and processing
 * the zoom level of the grid
 * @author Ashley Allen
 */
public class GameController {
    private int gridWidth, gridHeight;
    private final double gameWidth, gameHeight;
    private double hFactor, vFactor;
    private Map<String, Coordinates> cells = new HashMap<>();
    private final Map<String, Coordinates> newCells = new HashMap<>();
    private final ArrayList<Coordinates> checks = new ArrayList<>();
    private final GraphicsContext gc;
    private final List<Object[]> machines = new ArrayList<>();
    
    /**
     * Sets all initial variables and scans in the example machines
     * before drawing the game grid.
     * @param gc the GraphicsContext used to display the game
     * @param gridWidth the initial width of the game grid
     * @param gridHeight the initial height of the game grid
     */
    public GameController(GraphicsContext gc, int gridWidth, int gridHeight) {
        this.gc = gc;        
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        gameWidth = gc.getCanvas().getWidth();
        gameHeight = gc.getCanvas().getHeight();
        hFactor = gameWidth / gridWidth;
        vFactor = gameHeight / gridHeight;
        
        importExampleMachines();        
        drawGrid();
    }
    
    /**
     * Imports the example machines from the 'exampleMachines.txt' file and
     * saves them into a custom object array for later use
     */
    private void importExampleMachines() {
        Scanner scannner = new Scanner(this.getClass().getResourceAsStream("exampleMachines.txt"));
        while (scannner.hasNext()) {
            String machineName = scannner.next();
            int x = scannner.nextInt(), y = scannner.nextInt();
            boolean[][] machineTemplate = new boolean[y][x];
            for (int i = 0; i < y; i++) {
                for (int j = 0; j < x; j++) {
                    machineTemplate[i][j] = scannner.next().charAt(0) == 'X';
                }
            }
            machines.add(new Object[] {machineName, x, y, machineTemplate});
        }
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
     * Updates the state of all alive cells and their neighbour cells
     */
    public void updateStates() {
        //Add all alive cells and their surrounding cells to the checks array
        cells.values().stream().forEach((pos) -> {
            int x = pos.getX(), y = pos.getY();
            for (int cx = x - 1; cx < x + 2; cx++) {
                for (int cy = y - 1; cy < y + 2; cy++) {
                    checks.add(pos);
                }
            }
        });
        
        //For each pair of coordinates in checks compute whether the state
        //of the cell at those coordinates should be toggled or not and mark
        //those coordinates if so
        checks.stream().forEach((pos) -> {
            int x = pos.getX(), y = pos.getY();
            String hashKey = getHashKey(x, y);
            int adjCells = getAdjCells(x, y);
            if (cells.get(hashKey) == null) {
                if (adjCells == 3) {
                    newCells.putIfAbsent(hashKey, pos);
                }
            } else {
                if (adjCells >= 2 && adjCells <= 3) {
                    newCells.putIfAbsent(hashKey, pos);
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
    
    private void drawChanges(Map<String, Coordinates> map1, Map<String, Coordinates> map2, Paint fill) {
        map1.entrySet().stream().filter((entry) -> {
            Coordinates pos = entry.getValue();
            return map2.get(entry.getKey()) == null && pos.getX() >= 0 && pos.getX() < gridWidth && pos.getY() >= 0 && pos.getY() < gridHeight;
        }).forEach((entry) -> {
            Coordinates pos = entry.getValue();
            drawSquare(pos.getX(), pos.getY(), fill, Color.SILVER);
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
            cells.put(hashKey, new Coordinates(x, y));
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
     * Clears the cell storage and game grid
     */
    public void clear() {
        cells.entrySet().stream().forEach((entry) -> {
            Coordinates pos = entry.getValue();
            drawSquare(pos.getX(), pos.getY(), Color.WHITE, Color.SILVER);
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
    
    /**
     * Draws the specified machine to the game grid
     * @param machine the machine that is to be written
     */
    public void drawMachine(Object[] machine) {
        int machX = (int) machine[1], machY = (int) machine[2];
        int startX = gridWidth / 2 - machX / 2, startY = gridHeight / 2 - machY / 2;
        boolean[][] machineTemplate = (boolean[][]) machine[3];
        for (int i = 0; i < machY; i++) {
            for (int j = 0; j < machX; j++) {
                if (machineTemplate[i][j]) {
                    int x = j + startX, y = i + startY;
                    cells.put(getHashKey(x, y), new Coordinates(x, y));
                    drawSquare(x, y, Color.BLACK, Color.SILVER);
                }
            }
        }
    }
    
    /**
     * Returns the ArrayList that stores the machines
     * @return the list of machines
     */
    public List<Object[]> getMachines() {
        return machines;
    }
}

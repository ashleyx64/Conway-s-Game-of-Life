package gameoflife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Handles all the game logic including: drawing the game to the
 * canvas, updating the state of the cells after each generation and processing
 * the zoom level of the grid
 *
 * @author Ashley Allen
 */
public class GameController extends AnimationTimer {

    private int gridWidth, gridHeight;
    private final double gameWidth, gameHeight;
    private double hFactor, vFactor;
    private Map<String, Coordinates> cells = new HashMap<>();
    private Map<String, Coordinates> checks = new HashMap<>();
    private final GraphicsContext gc;
    private final List<Machine> machines = new ArrayList<>();

    private boolean paused = true;
    private boolean skipFrame = false;

    private long updateNanoTime = System.nanoTime();
    private long secondsNanoTime = System.nanoTime();
    private int generations = 0;
    private int frameDelay = 0;
    private int frameCounter = 0;
    private int FPS = 0;
    private int timeElapsed = 0;

    /**
     * Sets all initial variables and scans in the example machines before
     * drawing the game grid.
     *
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
     * saves them into the Machine array
     */
    private void importExampleMachines() {
        Scanner scannner = new Scanner(this.getClass().getResourceAsStream("exampleMachines.txt"));
        while (scannner.hasNext()) {
            String machineName = scannner.next();
            int machineWidth = scannner.nextInt(), machineHeight = scannner.nextInt();
            boolean[][] machineTemplate = new boolean[machineHeight][machineWidth];
            for (int i = 0; i < machineHeight; i++) {
                for (int j = 0; j < machineWidth; j++) {
                    machineTemplate[i][j] = scannner.next().charAt(0) == 'X';
                }
            }
            machines.add(new Machine(machineName, machineWidth, machineHeight, machineTemplate));
        }
    }

    /**
     * Draws the initial game grid to the specified GraphicsContext given in
     * the constructor
     */
    private void drawGrid() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                drawSquare(x, y, Color.WHITE, Color.SILVER);
            }
        }
    }
    
    /**
     * Draws the changes to the game grid since the last cell states
     */
    private void drawChanges() {
        checks.values().forEach((pos) -> {
            int x = pos.getX(), y = pos.getY();
            if (isOnGrid(x, y)) {
                if (cells.containsKey(getHashKey(x, y))) {
                    drawSquare(x, y, Color.BLACK, Color.SILVER);
                } else {
                    drawSquare(x, y, Color.WHITE, Color.SILVER);
                }
            }
        });
    }
    
    /**
     * Checks whether a given coordinate is on the current game grid
     * 
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @return true if the given coordinate is on the game grid
     */
    private boolean isOnGrid(int x, int y) {
        return x >= 0 && y >= 0 && x < gridWidth && y < gridHeight;
    }

    /**
     * Draws a square on the specified GraphicsContext
     *
     * @param x the x coordinate to draw to
     * @param y the y coordinate to draw to
     * @param fill the fill color to apply to the square
     * @param stroke the line color to appy to the square
     */
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
    public void updateCellStates() {
        //Add all alive cells and their surrounding cells to the checks array
        checks = new HashMap<>();
        cells.values().stream().forEach((pos) -> {
            int x = pos.getX(), y = pos.getY();
            for (int cx = x - 1; cx < x + 2; cx++) {
                for (int cy = y - 1; cy < y + 2; cy++) {
                    checks.putIfAbsent(getHashKey(cx, cy), new Coordinates(cx, cy));
                }
            }
        });

        //For each pair of coordinates in checks compute whether the state
        //of the cell at those coordinates should be changed or not
        Map<String, Coordinates> tempCells = new HashMap<>(cells);
        checks.values().stream().forEach((pos) -> {
            int x = pos.getX(), y = pos.getY();
            String hashKey = getHashKey(x, y);
            int adjCells = getNumAdjCells(x, y);
            if (cells.get(hashKey) == null) {
                if (adjCells == 3) {
                    tempCells.put(hashKey, pos);
                }
            } else {
                if (adjCells < 2 || adjCells > 3) {
                    tempCells.remove(hashKey);
                }
            }
        });
        cells = tempCells;

        drawChanges();

        generations++;
    }

    /**
     * Gets the number of adjacent alive cells next to a cell
     *
     * @param x the x coordinate of the cell to check
     * @param y the y coordinate of the cell to check
     * @return the number of adjacent alive cells
     */
    private int getNumAdjCells(int x, int y) {
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

    /**
     * Toggles a cell at the specified coordinates, more precisely it creates a
     * cell at the specified coordinates if none exists or removes a cell from
     * the specified coordinates if one exists
     *
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
     * Converts a given x window coordinate relative to the game grid into a x
     * grid coordinate
     *
     * @param x the window coordinate to be converted
     * @return the converted coordinate
     */
    public int convertX(double x) {
        return (int) (x / hFactor);
    }

    /**
     * Converts a given y window coordinate relative to the game grid into a y
     * grid coordinate
     *
     * @param y the window coordinate to be converted
     * @return the converted coordinate
     */
    public int convertY(double y) {
        return (int) (y / vFactor);
    }

    /**
     * Changes to size of the active game grid viewed on the canvas by 1 in each
     * dimension
     *
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

    /**
     * Constructs a hashkey from the specified x and y coordinates
     *
     * @param x the x coordinate to construct from
     * @param y the y coordinate to construct from
     * @return the constructed hashkey
     */
    private String getHashKey(int x, int y) {
        return x + "_" + y;
    }
    
    /**
     * Inherited from AnimationTimer. Runs in the background and calculates
     * various attributes such as time, frames and generations 
     * 
     * @param currentNanoTime the current system time in nanoseconds
     */
    @Override
    public void handle(long currentNanoTime) {
        //If sufficient time has passed between frames and the game is not
        //paused update cell states and the frame counter
        if (currentNanoTime - updateNanoTime >= frameDelay && !paused || skipFrame) {
            updateCellStates();
            frameCounter++;
            skipFrame = false;
            updateNanoTime = currentNanoTime;
        }

        //Every time a second passes update the time and FPS counters
        if (currentNanoTime - secondsNanoTime >= 1_000_000_000) {
            if (!paused) timeElapsed++;
            FPS = frameCounter;
            frameCounter = 0;
            secondsNanoTime = currentNanoTime;
        }
    }

    /**
     * Returns the number of currently alive cells
     *
     * @return the number of currently alive cells
     */
    public int getNumCells() {
        return cells.size();
    }

    /**
     * Draws the specified machine to the game grid
     *
     * @param index the index of the machine to retrieve
     */
    public void drawMachine(int index) {
        Machine machine = machines.get(index);
        int machineWidth = machine.getWidth(), machineHeight = machine.getHeight();
        int startX = gridWidth / 2 - machineWidth / 2, startY = gridHeight / 2 - machineHeight / 2;
        boolean[][] machineTemplate = machine.getTemplate();
        for (int i = 0; i < machineHeight; i++) {
            for (int j = 0; j < machineWidth; j++) {
                if (machineTemplate[i][j]) {
                    int x = j + startX, y = i + startY;
                    cells.put(getHashKey(x, y), new Coordinates(x, y));
                    drawSquare(x, y, Color.BLACK, Color.SILVER);
                }
            }
        }
    }

    /**
     * Resets the global variables and clear the game grid
     */
    public void reset() {
        generations = 0;
        paused = true;
        frameDelay = 0;
        timeElapsed = 0;
        clear();
    }

    /**
     * Clears the game grid
     */
    public void clear() {
        cells.clear();
        drawGrid();
    }

    /**
     * Gets the name of the machine at the specified index
     *
     * @param index the index of the machine to get
     * @return the name of the specified machine
     */
    public String getMachineName(int index) {
        return machines.get(index).getName();
    }

    /**
     * Gets the number of machines loaded into the game
     *
     * @return the number of machines
     */
    public int getNumberOfMachines() {
        return machines.size();
    }

    /**
     * Toggles whether the game is paused or not
     */
    public void togglePaused() {
        paused = !paused;
    }

    /**
     * Getswhether the game is paused or not
     *
     * @return whether the game is paused or not
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Tells the game to skip a frame when the game is paused
     */
    public void skipFrame() {
        skipFrame = true;
    }

    /**
     * Sets the delay between frames for the game
     *
     * @param delay the delay in ms between frames
     */
    public void setFrameDelay(int delay) {
        frameDelay = delay * 1_000_000;
    }

    /**
     * Gets the delay between frames
     *
     * @return the delay between frames in ms
     */
    public int getFrameDelay() {
        return frameDelay;
    }

    /**
     * Gets the number of generations that have passed since the game was started
     *
     * @return the number of generations
     */
    public int getNumGenerations() {
        return generations;
    }

    /**
     * Gets the current FPS of the game
     * 
     * @return the current FPS of the game
     */
    public int getFPS() {
        return FPS;
    }

    /**
     * Gets the current time elapsed in the game
     * 
     * @return the current time elapsed in the game
     */
    public int getTimeElapsed() {
        return timeElapsed;
    }
}

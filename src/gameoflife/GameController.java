package gameoflife;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameController {
    private final int width, height, factor;
    private final Cell[][] gameMap;
    private final GraphicsContext gc;
    private boolean wrap;
    
    public GameController(int width, int height, int factor, GraphicsContext gc, boolean example) {
        this.width = width;
        this.height = height;
        this.factor = factor;
        this.gc = gc;
        gameMap = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                gameMap[i][j] = new Cell();
            }
        }
        if (example) {
            placeExample();
        }
    }
    
    private void placeExample() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("src\\gameoflife\\exampleTemplate.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        int startX = width / 2 - 7, startY = height / 2 - 7;
        for (int i = 0; i < 13; i++) {
            String line = scanner.next();
            for (int j = 0; j < 13; j++) {
                if (line.charAt(j) == 'X') {
                    int x = j + startX, y = i + startY;
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        gameMap[y][x].setAlive(true);
                    }
                }
            }
        }
    }
    
    public void drawGame() {
        gc.setStroke(Color.SILVER);
        gc.setLineWidth(2);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (gameMap[i][j].getAlive()) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.fillRect(j * factor, i * factor, factor, factor);
                gc.strokeRect(j * factor, i * factor, factor, factor);
            }
        }
    }
    
    public void updateStates() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int adjPop = getAdjPop(j, i);
                if (gameMap[i][j].getAlive()) {
                    if (adjPop < 2 || adjPop > 3) {
                        gameMap[i][j].setMarked(true);
                    }
                } else {
                    if (adjPop == 3) {
                        gameMap[i][j].setMarked(true);
                    }
                }
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (gameMap[i][j].getMarked()) {
                    gameMap[i][j].toggleAlive();
                    gameMap[i][j].setMarked(false);
                }
            }
        }
    }
    
    private int getAdjPop(int x, int y) {
        int sum = 0;
        for (int i = y - 1; i < y + 2; i++) {
            for (int j = x - 1; j < x + 2; j++) {
                if (!(i == y && j == x)) {
                    if (wrap) {
                        if (gameMap[getWrapped(i, height)][getWrapped(j, width)].getAlive()) {
                            sum++;
                        }
                    } else {
                        if (i >= 0 && i < height && j >= 0 && j < width) {
                            if (gameMap[i][j].getAlive()) {
                                sum++;
                            }
                        }
                    }
                }
            }
        }
        return sum;
    }
    
    private int getWrapped(int c, int len) {
        if (c < 0) {
            return c + len;
        } else if (c >= len) {
            return c - len;
        } else {
            return c;
        }
    }
    
    public void toggleCell(int x, int y) {
        gameMap[y][x].toggleAlive();
    }
    
    public void clear() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                gameMap[i][j].setAlive(false);
            }
        }
    }
    
    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }
}

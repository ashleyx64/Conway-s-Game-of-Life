package gameoflife;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameController {
    private final int width, height, factor;
    private final Cell[][] gameMap;
    
    public GameController(int width, int height, int factor) {
        this.width = width;
        this.height = height;
        this.factor = factor;
        gameMap = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                gameMap[i][j] = new Cell();
            }
        }
    }
    
    public void drawGame(GraphicsContext gc) {
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
                    if (gameMap[getWrapped(i, height)][getWrapped(j, width)].getAlive()) {
                        sum++;
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
}

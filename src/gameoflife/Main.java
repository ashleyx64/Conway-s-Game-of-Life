package gameoflife;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class
 * @author Ashley Allen
 */
public class Main extends Application {
    private static final int gridWidth = 50, gridHeight = 50;
    private static final double gameWidth = 1000, gameHeight = 1000;
    
    @Override
    public void start(Stage unusedStage) {
            new GameStage(gridWidth, gridHeight, gameWidth, gameHeight, true).show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

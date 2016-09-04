package gameoflife;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * The main game stage, used to display the game to the player and to
 * process input such as mouse clicks, button presses, scrolls and key
 * presses. Handles all the GUI elements as well as the main game loop
 * and updating the various counters displayed to the player
 * @author Ashley Allen
 */
public class GameStage extends Stage {
    private boolean updateStates = false, skipFrame = false;
    private int frameDelay = 0;
    private long genCounter = 0;
    private long timeCounter = 0;    
        
    private final GridPane root = new GridPane();
    private final Scene scene = new Scene(root);
            
    private final HBox btnToolbar = new HBox(5);
    private final Button playPauseBtn = new Button("Play");
    private final Button skipFrameBtn = new Button("Skip Frame");
    private final Button resetBtn = new Button("Reset");
    private final Label frameDelayLbl = new Label("Frame delay (ms):");
    private final TextField frameDelayTxtFld = new TextField(String.valueOf(frameDelay));
    private final Button frameDelayBtn = new Button("Update");
    
    private final HBox lblToolbar = new HBox(5);
    private final Label fpsLbl = new Label("FPS: 0");
    private final Label genLbl = new Label("Generations: 0");
    private final Label cellsLbl = new Label("Cells: 0");
    private final Label timeElapsedLbl = new Label("Time Elapsed: 0s");
    
    private final HBox machineToolbar = new HBox(5);
    private final Label machineLbl = new Label("Machines:");
    private final List<Button> machineBtns = new ArrayList<>();
    
    private final Canvas gameCanvas;
    private final GraphicsContext gameGC;
    
    private final GameController gameController;    
        
    /**
     * Default constructor
     * @param gridWidth the width of the game grid
     * @param gridHeight the height of the game grid
     * @param gameWidth the width of the game canvas in pixels
     * @param gameHeight the height of the game canvas in pixels
     * @param example true if an example should be generated on startup
     */
    public GameStage(int gridWidth, int gridHeight, double gameWidth, double gameHeight, boolean example) {
        gameCanvas = new Canvas(gameWidth, gameHeight);
        
        gameGC = gameCanvas.getGraphicsContext2D();
        
        gameController = new GameController(gridWidth, gridHeight, gameGC, example);     
        
        root.add(btnToolbar, 0, 0);
        root.add(lblToolbar, 1, 0);
        root.add(machineToolbar, 0, 1, 2, 1);
        root.add(gameCanvas, 0, 2, 2, 1);
        
        scene.setOnScroll((ScrollEvent t) -> {
            gameController.changeZoom(t.getDeltaY() < 0);
        });
        
        btnToolbar.setPadding(new Insets(5));
        btnToolbar.setAlignment(Pos.CENTER_LEFT);
        btnToolbar.getChildren().addAll(playPauseBtn, skipFrameBtn, resetBtn, frameDelayLbl, frameDelayTxtFld, frameDelayBtn);
        
        playPauseBtn.setPrefWidth(60);
        playPauseBtn.setOnAction((ActionEvent t) -> {
            if (updateStates) {
                updateStates = false;
                playPauseBtn.setText("Play");
            } else {
                updateStates = true;
                playPauseBtn.setText("Pause");
            }
        });
        
        skipFrameBtn.setOnAction((ActionEvent t) -> skipFrame = true);
        
        resetBtn.setOnAction((ActionEvent t) -> {
            gameController.clear();
            genCounter = 0;
            timeCounter = 0;
            updateStates = false;
            playPauseBtn.setText("Play");
        });
        
        frameDelayTxtFld.setPrefWidth(60);
        
        frameDelayBtn.setOnAction((ActionEvent t) -> {
            try {
                frameDelay = Integer.parseInt(frameDelayTxtFld.getText());
            } catch (NumberFormatException ex) {
                new Alert(AlertType.WARNING, "That is not a valid number", ButtonType.OK).showAndWait();
            }
            gameCanvas.requestFocus();
        });
        
        lblToolbar.setPadding(new Insets(5));
        lblToolbar.setAlignment(Pos.CENTER_RIGHT);
        lblToolbar.getChildren().addAll(fpsLbl, genLbl, cellsLbl, timeElapsedLbl);
        
        machineToolbar.setPadding(new Insets(5));
        machineToolbar.setAlignment(Pos.CENTER_LEFT);
        machineToolbar.getChildren().addAll(machineLbl);
        
        gameController.getMachines().stream().forEach((machine) -> {
            Button newBtn = new Button((String) machine[0]);
            newBtn.setOnAction((ActionEvent t) -> {
                gameController.clear();
                gameController.drawMachine(machine);
            });
            machineBtns.add(newBtn);
        });
        machineToolbar.getChildren().addAll(machineBtns);
        
        gameCanvas.setOnMouseClicked((MouseEvent t) -> {
            gameController.toggleCell(gameController.convertX(t.getX()), gameController.convertY(t.getY()));
        });
        
        this.setTitle("Conway's Game of Life");
        this.setScene(scene);        
        
        new AnimationTimer() {
            
            long updateNanoTime = System.nanoTime();
            long secondNanoTime = System.nanoTime();
            long frameCounter = 0;
            
            @Override
            public void handle(long currentNanoTime) {
                if (updateStates && currentNanoTime - updateNanoTime >= frameDelay * 1_000_000 || skipFrame) {
                    gameController.updateStates();
                    frameCounter++;
                    genCounter++;
                    skipFrame = false;
                    updateNanoTime = currentNanoTime;                    
                }
                
                if (currentNanoTime - secondNanoTime >= 1_000_000_000) {
                    if (updateStates) {
                        timeCounter++;
                    }
                    fpsLbl.setText("FPS: " + String.valueOf(frameCounter));                    
                    frameCounter = 0;
                    secondNanoTime = currentNanoTime;
                }
                
                genLbl.setText("Generations: " + String.valueOf(genCounter));
                
                cellsLbl.setText("Cells: " + gameController.getNumCells());
                
                timeElapsedLbl.setText("Time Elapsed: " + timeCounter + "s");
            }
            
        }.start();
    }
    
}

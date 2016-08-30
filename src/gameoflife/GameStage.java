package gameoflife;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 *
 * @author Ashley Allen
 */
public class GameStage extends Stage {
    private final BorderPane root = new BorderPane();;
    private final Canvas gameDisplay;
    private final GraphicsContext gc;
    private final GameController game;
    private final FlowPane toolbar = new FlowPane(5, 5);;
    private final Button playPauseBtn = new Button("Play");
    private final Button skipFrameBtn = new Button("Skip Frame");
    private final Button resetBtn = new Button("Reset");
    private final Label refreshRateLbl = new Label("Refresh rate:");
    private final TextField refreshRateTxtFld;
    private final Button refreshRateUpdateBtn = new Button("Update");
    private final Label fpsLbl = new Label("FPS: 0");
    private final Label timeLbl = new Label("Time: 0");
    private final Scene scene = new Scene(root);
    
    private boolean updateStates = false, skipFrame = false;
    private int refreshRate = 240;
    private long timeCounter = 0;
    
    public GameStage(int gridWidth, int gridHeight, double gameWidth, double gameHeight, boolean example) {
 
        
        gameDisplay = new Canvas(gameWidth, gameHeight);
        gameDisplay.setFocusTraversable(true);
        
        gc = gameDisplay.getGraphicsContext2D();
        
        game = new GameController(gridWidth, gridHeight, gc, example);         
        
        gameDisplay.setOnMouseClicked((MouseEvent t) -> {
            game.toggleCell(game.convertX(t.getX()), game.convertY(t.getY()));
        });        
        
        toolbar.setPadding(new Insets(5));
        
        playPauseBtn.setPrefWidth(55);
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
            game.clear();
            timeCounter = 0;
            updateStates = false;
            playPauseBtn.setText("Play");
        });        
        
        refreshRateTxtFld = new TextField(String.valueOf(refreshRate));
        refreshRateTxtFld.setPrefWidth(50);
        refreshRateTxtFld.setTooltip(new Tooltip("Delay between frames in ms"));
        
        refreshRateUpdateBtn.setOnAction((ActionEvent t) -> {
            try {
                refreshRate = Integer.parseInt(refreshRateTxtFld.getText());
            } catch (NumberFormatException ex) {
                new Alert(AlertType.WARNING, "That is not a valid number", ButtonType.OK).showAndWait();
            }
            gameDisplay.requestFocus();
        });
        
        new AnimationTimer() {
            
            long prevNanoTime = System.nanoTime();
            long frameNanoTime = System.nanoTime();
            long frameCounter = 0;
            
            @Override
            public void handle(long currentNanoTime) {
                if (updateStates && currentNanoTime - prevNanoTime >= refreshRate * 1_000_000 || skipFrame) {
                    game.updateStates();
                    frameCounter++;
                    timeCounter++;
                    skipFrame = false;
                    prevNanoTime = currentNanoTime;                    
                }
                
                if (currentNanoTime - frameNanoTime >= 1_000_000_000) {
                    fpsLbl.setText("FPS: " + String.valueOf(frameCounter));                    
                    frameCounter = 0;
                    frameNanoTime = currentNanoTime;
                }
                
                timeLbl.setText("Time: " + String.valueOf(timeCounter));                
                
                game.drawGame();
            }
            
        }.start();
        
//        scene.setOnKeyPressed((KeyEvent t) -> {
//            game.moveGameArea(t.getCode());
//        });
        
        scene.setOnScroll((ScrollEvent t) -> {
            game.changeZoom(t.getDeltaY() < 0);
        });
        
        toolbar.getChildren().addAll(playPauseBtn, skipFrameBtn, resetBtn, refreshRateLbl, refreshRateTxtFld, refreshRateUpdateBtn, fpsLbl, timeLbl);
        toolbar.getChildren().stream().forEach((node) -> {
            node.setFocusTraversable(false);
        });
        
        root.setBottom(gameDisplay);
        root.setTop(toolbar);
        
        this.setTitle("Conway's Game of Life");
        this.setScene(scene);     
    }
    
}

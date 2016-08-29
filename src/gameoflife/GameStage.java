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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 *
 * @author Ashley Allen
 */
public class GameStage extends Stage {
    private final BorderPane root2 = new BorderPane();;
    private final Canvas gameDisplay;
    private final GraphicsContext gc;
    private GameController game;
    private final FlowPane toolbar = new FlowPane(5, 5);;
    private Button playPauseBtn = new Button("Play");
    private final Button clearBtn = new Button("Clear");
    private final Label refreshRateLbl = new Label("Refresh rate (delay between frames in ms):");
    private TextField refreshRateTxtFld;
    private final Button refreshRateUpdateBtn = new Button("Update");
    private final CheckBox modeChkBx = new CheckBox("Wrap edges");
    private final Scene scene2 = new Scene(root2);
    
    private boolean updateStates = false;
    private int refreshRate = 240;
    private final int factor = 20;
    
    public GameStage(int width, int height, boolean example) {
        gameDisplay = new Canvas(width * factor, height * factor);
        gameDisplay.setOnMouseClicked((MouseEvent t) -> {
            game.toggleCell((int) t.getX() / factor, (int) t.getY() / factor);
        });
        
        gc = gameDisplay.getGraphicsContext2D();
        
        game = new GameController(width, height, factor, gc, example);
        
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
        
        clearBtn.setOnAction((ActionEvent t) -> game.clear());        
        
        refreshRateTxtFld = new TextField(String.valueOf(refreshRate));
        refreshRateTxtFld.setPrefWidth(40);
        
        refreshRateUpdateBtn.setOnAction((ActionEvent t) -> {
            try {
                refreshRate = Integer.parseInt(refreshRateTxtFld.getText());
            } catch (NumberFormatException ex) {
                new Alert(AlertType.WARNING, "That is not a valid number", ButtonType.OK).showAndWait();
            }
        });
        
        modeChkBx.setOnAction((ActionEvent t) -> game.toggleWrapped());        
        
        new AnimationTimer() {
            
            long prevNanoTime = System.nanoTime();
            
            @Override
            public void handle(long currentNanoTime) {
                if (updateStates && currentNanoTime - prevNanoTime >= refreshRate * 1_000_000) {
                    prevNanoTime = currentNanoTime;
                    game.updateStates();
                }
                game.drawGame();
            }
            
        }.start();
        
        toolbar.getChildren().addAll(playPauseBtn, clearBtn, refreshRateLbl, refreshRateTxtFld, refreshRateUpdateBtn, modeChkBx);
        root2.setBottom(gameDisplay);
        root2.setTop(toolbar);
        
        this.setTitle("Conway's Game of Life");
        this.setScene(scene2);     
    }
    
}

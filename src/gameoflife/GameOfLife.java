/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 *
 * @author Ashley
 */
public class GameOfLife extends Application {
    private boolean updateStates = false;
    private int refreshRate = 240;
    
    @Override
    public void start(Stage primaryStage) {
        final int width = 50, height = 50, factor = 20;
        final BorderPane root = new BorderPane();
        final Canvas gameDisplay = new Canvas(width * factor, height * factor);
        final GraphicsContext gc = gameDisplay.getGraphicsContext2D();
        final GameController game = new GameController(width, height, factor);
        final FlowPane toolbar = new FlowPane(5, 0);
        toolbar.setPadding(new Insets(5));
        final Button playPauseBtn = new Button("Play");
        playPauseBtn.setPrefWidth(55);
        final Button clearBtn = new Button("Clear");
        final Label refreshRateLbl = new Label("Refresh rate (delay between frames in ms):");
        final TextField refreshRateTxtFld = new TextField(String.valueOf(refreshRate));
        final Button refreshRateUpdateBtn = new Button("Update");
        final Scene scene = new Scene(root);
        
        new AnimationTimer() {
            
            long prevNanoTime = System.nanoTime();
            
            @Override
            public void handle(long currentNanoTime) {
                if (updateStates && currentNanoTime - prevNanoTime >= refreshRate * 1_000_000) {
                    prevNanoTime = currentNanoTime;
                    game.updateStates();
                }
                game.drawGame(gc);
            }
            
        }.start();
        
        gameDisplay.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                game.toggleCell((int) t.getX() / factor, (int) t.getY() / factor);
            }
            
        });
        
        playPauseBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                if (updateStates) {
                    updateStates = false;
                    playPauseBtn.setText("Play");
                } else {
                    updateStates = true;
                    playPauseBtn.setText("Pause");
                }
            }
            
        });
        
        clearBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                game.clear();
            }
            
        });
        
        refreshRateUpdateBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                try {
                    refreshRate = Integer.parseInt(refreshRateTxtFld.getText());
                } catch (NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                };
            }
            
        });
        
        toolbar.getChildren().addAll(playPauseBtn, clearBtn, refreshRateLbl, refreshRateTxtFld, refreshRateUpdateBtn);
        root.setBottom(gameDisplay);
        root.setTop(toolbar);
        
        primaryStage.setTitle("Conway's Game of Life");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

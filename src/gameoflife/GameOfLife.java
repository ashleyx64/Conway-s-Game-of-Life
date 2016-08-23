package gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameOfLife extends Application {
    private static boolean updateStates = false, example = false;
    private static int refreshRate = 240;
    private static int width = 50, height = 50;
    private static final int factor = 20;
        
    private static BorderPane root2;
    private static Canvas gameDisplay;
    private static GraphicsContext gc;
    private static GameController game;
    private static FlowPane toolbar;
    private static Button playPauseBtn, clearBtn;
    private static Label refreshRateLbl;
    private static TextField refreshRateTxtFld;
    private static Button refreshRateUpdateBtn;
    private static CheckBox modeChkBx;
    private static Scene scene2;
    private static Stage secondaryStage;
    
    @Override
    public void start(final Stage primaryStage) {
        final BorderPane root1 = new BorderPane();
        root1.setPadding(new Insets(5));
        
        final Label introLbl = new Label("Welcome to Conway's Game of Life!");
        BorderPane.setAlignment(introLbl, Pos.CENTER);
        
        final GridPane options = new GridPane();
        options.setPadding(new Insets(5));
        options.setHgap(5);
        options.setVgap(5);
        final Label widthLbl = new Label("Width:");
        
        final TextField widthTxtFld = new TextField(String.valueOf(width));
        widthTxtFld.setPrefWidth(40);
        
        final Label heightLbl = new Label("Height:");
        
        final TextField heightTxtFld = new TextField(String.valueOf(height));
        heightTxtFld.setPrefWidth(40);
        
        final CheckBox exampleChkBx = new CheckBox("Generate example");
        GridPane.setHalignment(exampleChkBx, HPos.CENTER);
        
        final Button confirmBtn = new Button("Confirm");
        BorderPane.setAlignment(confirmBtn, Pos.CENTER);
        
        final Scene scene1 = new Scene(root1);
        
        options.add(widthLbl, 0, 0, 1, 1);
        options.add(widthTxtFld, 1, 0, 1, 1);
        options.add(heightLbl, 2, 0, 1, 1);
        options.add(heightTxtFld, 3, 0, 1, 1);
        options.add(exampleChkBx, 0, 1, 4, 1);
        
        root1.setTop(introLbl);
        root1.setCenter(options);
        root1.setBottom(confirmBtn);
        
        primaryStage.setTitle("CGoL");
        primaryStage.setScene(scene1);
        primaryStage.show();
        
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                primaryStage.hide();
                width = Integer.parseInt(widthTxtFld.getText());
                height = Integer.parseInt(heightTxtFld.getText());
                example = exampleChkBx.isSelected();
                initialiseStage2();
            }
        
        });
        
    }
    
    private static void initialiseStage2() {
        root2 = new BorderPane();
        gameDisplay = new Canvas(width * factor, height * factor);
        gc = gameDisplay.getGraphicsContext2D();
        game = new GameController(width, height, factor, gc, example);
        toolbar = new FlowPane(5, 5);
        toolbar.setPadding(new Insets(5));  
        playPauseBtn = new Button("Play");
        playPauseBtn.setPrefWidth(55);
        clearBtn = new Button("Clear");
        refreshRateLbl = new Label("Refresh rate (delay between frames in ms):");
        refreshRateTxtFld = new TextField(String.valueOf(refreshRate));
        refreshRateTxtFld.setPrefWidth(60);
        refreshRateUpdateBtn = new Button("Update");
        modeChkBx = new CheckBox("Wrap edges");
        scene2 = new Scene(root2);
        secondaryStage = new Stage();
        
        new AnimationTimer() {
            
            long prevNanoTime = System.nanoTime();
            
            @Override
            public void handle(long currentNanoTime) {
                if (modeChkBx.isSelected()) {
                    game.setWrap(true);
                } else {
                    game.setWrap(false);
                }
                if (updateStates && currentNanoTime - prevNanoTime >= refreshRate * 1_000_000) {
                    prevNanoTime = currentNanoTime;
                    game.updateStates();
                }
                game.drawGame();
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
                }
            }
            
        });
        
        toolbar.getChildren().addAll(playPauseBtn, clearBtn, refreshRateLbl, refreshRateTxtFld, refreshRateUpdateBtn, modeChkBx);
        root2.setBottom(gameDisplay);
        root2.setTop(toolbar);
        
        secondaryStage.setTitle("Conway's Game of Life");
        secondaryStage.setScene(scene2);
        secondaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

package gameoflife;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
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
 * Main class
 *
 * @author Ashley Allen
 */
public class Main extends Application {
    
    /**
     * Inherited from Application. Sets up all the graphical window elements
     * as well as manages updating the labels using an AnimationTimer
     * 
     * @param primaryStage the primary Stage to display the Application on
     */
    @Override
    public void start(Stage primaryStage) {
        final int gridWidth = 50, gridHeight = 50;
        final double gameWidth = 1000, gameHeight = 1000;

        //The game canvas that will display the game grid
        final Canvas canvas = new Canvas(gameWidth, gameHeight);

        //The GameController that will control the game
        final GameController game = new GameController(canvas.getGraphicsContext2D(), gridWidth, gridHeight);

        //When the canvas is clicked toggle the cell at that location
        canvas.setOnMouseClicked((MouseEvent t) -> {
            game.toggleCell(game.convertX(t.getX()), game.convertY(t.getY()));
        });

        //A button to play and pause the game
        final Button playPauseBtn = new Button("Play");
        playPauseBtn.setPrefWidth(60);
        playPauseBtn.setOnAction((ActionEvent t) -> game.togglePaused());

        //A button to skip a frame of the game
        final Button skipFrameBtn = new Button("Skip Frame");
        skipFrameBtn.setOnAction((ActionEvent t) -> game.skipFrame());

        //A button to reset the game
        final Button resetBtn = new Button("Reset");
        resetBtn.setOnAction((ActionEvent t) -> game.reset());

        final Label frameDelayLbl = new Label("Frame delay (ms):");

        //A TextField to allow the user to specify the frame delay
        final TextField frameDelayTxtFld = new TextField();
        frameDelayTxtFld.setPrefWidth(60);

        //A button to confirm entry of a new frame delay
        final Button frameDelayBtn = new Button("Update");
        frameDelayBtn.setOnAction((ActionEvent t) -> {
            try {
                game.setFrameDelay(Integer.parseInt(frameDelayTxtFld.getText()));
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid number", ButtonType.OK).showAndWait();
            }
            canvas.requestFocus();
        });

        //Labels to show statistics to the player
        final Label fpsLbl = new Label("FPS: 0");
        final Label genLbl = new Label("Generations: 0");
        final Label cellsLbl = new Label("Cells: 0");
        final Label timeElapsedLbl = new Label("Time Elapsed: 0s");
        
        //An AnimationTimer to regularly update the statistical labels
        new AnimationTimer() {

            @Override
            public void handle(long now) {
                fpsLbl.setText("FPS: " + game.getFPS());
                genLbl.setText("Generations: " + game.getNumGenerations());
                cellsLbl.setText("Cells: " + game.getNumCells());
                timeElapsedLbl.setText("Time Elapsed: " + game.getTimeElapsed());
                playPauseBtn.setText(game.isPaused() ? "Play" : "Pause");
            }

        }.start();

        final Label machineLbl = new Label("Machines:");

        //The toolbar that will store the buttons
        final HBox btnToolbar = new HBox(5);
        btnToolbar.setPadding(new Insets(5));
        btnToolbar.setAlignment(Pos.CENTER_LEFT);
        btnToolbar.getChildren().addAll(playPauseBtn, skipFrameBtn, resetBtn, frameDelayLbl, frameDelayTxtFld, frameDelayBtn);

        //The toolbar that will store the labels
        final HBox lblToolbar = new HBox(5);
        lblToolbar.setPadding(new Insets(5));
        lblToolbar.setAlignment(Pos.CENTER_RIGHT);
        lblToolbar.getChildren().addAll(fpsLbl, genLbl, cellsLbl, timeElapsedLbl);

        //The toolbar that will store the buttons to generate the machines
        final HBox machineToolbar = new HBox(5);
        machineToolbar.setPadding(new Insets(5));
        machineToolbar.setAlignment(Pos.CENTER_LEFT);
        machineToolbar.getChildren().addAll(machineLbl);

        //Create a button for each machine imported from the 'exampleMachines.txt'
        //file and populate them into the toolbar
        final List<Button> machineBtns = new ArrayList<>();
        for (int i = 0; i < game.getNumberOfMachines(); i++) {
            final int index = i;
            Button newBtn = new Button(game.getMachineName(index));
            newBtn.setOnAction((ActionEvent t) -> {
                game.reset();
                game.drawMachine(index);
            });
            machineBtns.add(newBtn);
        }
        machineToolbar.getChildren().addAll(machineBtns);

        //The root node that will manage all other nodes
        final GridPane root = new GridPane();
        root.add(btnToolbar, 0, 0);
        root.add(lblToolbar, 1, 0);
        root.add(machineToolbar, 0, 1, 2, 1);
        root.add(canvas, 0, 2, 2, 1);

        //The main scene
        final Scene scene = new Scene(root);
        scene.setOnScroll((ScrollEvent t) -> {
            game.changeZoom(t.getDeltaY() < 0);
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Conway's Game of Life");
        primaryStage.show();

        game.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

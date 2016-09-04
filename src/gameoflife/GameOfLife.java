package gameoflife;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main class
 * @author Ashley
 */
public class GameOfLife extends Application {
    private static final int gridWidth = 50, gridHeight = 50;
    private static final double gameWidth = 1000, gameHeight = 1000;
    
    @Override
    public void start(final Stage primaryStage) {
        final BorderPane root = new BorderPane();
        root.setPadding(new Insets(5));
        
        final Label introLbl = new Label("Welcome to Conway's Game of Life!");
        BorderPane.setAlignment(introLbl, Pos.CENTER);
        
        final CheckBox exampleChkBx = new CheckBox("Generate example");
        exampleChkBx.setPadding(new Insets(5));
        
        final Button confirmBtn = new Button("Confirm");
        BorderPane.setAlignment(confirmBtn, Pos.CENTER);
        
        final Scene scene = new Scene(root);
        
        root.setTop(introLbl);
        root.setCenter(exampleChkBx);
        root.setBottom(confirmBtn);
        
        primaryStage.setTitle("CGoL");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        confirmBtn.setOnAction((ActionEvent t) -> {
            primaryStage.hide();
            new GameStage(gridWidth, gridHeight, gameWidth, gameHeight, exampleChkBx.isSelected()).show();
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

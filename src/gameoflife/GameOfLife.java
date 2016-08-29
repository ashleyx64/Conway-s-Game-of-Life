package gameoflife;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameOfLife extends Application {
    private static int gridWidth = 50, gridHeight = 50;
    private static double gameWidth = 1000, gameHeight = 1000;
    private static boolean example = false;
    
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
        
        final TextField widthTxtFld = new TextField(String.valueOf(gridWidth));
        widthTxtFld.setPrefWidth(40);
        
        final Label heightLbl = new Label("Height:");
        
        final TextField heightTxtFld = new TextField(String.valueOf(gridHeight));
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
        
        confirmBtn.setOnAction((ActionEvent t) -> {
            try {
                gridWidth = Integer.parseInt(widthTxtFld.getText());
                gridHeight = Integer.parseInt(heightTxtFld.getText());
            } catch (NumberFormatException ex) {
                new Alert(AlertType.WARNING, "That is not a valid number", ButtonType.OK).showAndWait();
                return;
            }
            if (gridWidth < 20 || gridHeight < 20) {
                new Alert(AlertType.WARNING, "Minimum allowed size is 20 x 20", ButtonType.OK).showAndWait();
                return;
            }
            primaryStage.hide();
            example = exampleChkBx.isSelected();
            new GameStage(gridWidth, gridHeight, gameWidth, gameHeight, example).show();            
        });
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

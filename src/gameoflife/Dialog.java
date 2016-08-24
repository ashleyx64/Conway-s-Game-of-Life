/*
 * Copyright (C) 2016 Ashley Allen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameoflife;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Ashley Allen
 */
public class Dialog extends Stage {
    Label textLbl;
    Button okBtn = new Button("OK");
    GridPane root = new GridPane();
    Scene scene = new Scene(root);
    
    public Dialog(String text, String title) {
        textLbl = new Label(text);
        GridPane.setHalignment(textLbl, HPos.CENTER);
        GridPane.setHalignment(okBtn, HPos.CENTER);
        root.add(textLbl, 0, 0);
        root.add(okBtn, 0, 1);
        root.setVgap(5);        
        root.setPadding(new Insets(5));
        
        okBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                hideDialog();
            }
        
        });
        
        this.setTitle(title);
        this.setScene(scene);
        this.show();
    }
    
    private void hideDialog() {
        this.hide();
    }
}

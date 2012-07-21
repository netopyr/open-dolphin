package com.canoo.dolphin.logo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ShowLogo extends Application {
    @Override
    public void start(Stage stage) {
        DolphinLogo control = new DolphinLogo();
        control.setPrefSize(400, 257);

        StackPane pane = new StackPane();
        pane.getChildren().add(control);

        Scene scene = new Scene(pane, 440, 297, Color.FLORALWHITE);

        stage.setTitle("Dolphin Greetings");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}



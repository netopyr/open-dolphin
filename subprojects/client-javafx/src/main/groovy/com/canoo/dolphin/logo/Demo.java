package com.canoo.dolphin.logo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Demo extends Application {
    @Override
    public void start(Stage stage) {
        DolphinLogo control = new DolphinLogo();
        control.setPrefSize(401, 257);

        StackPane pane = new StackPane();
        pane.getChildren().add(control);

        Scene scene = new Scene(pane, 401, 257, Color.DARKGRAY);

        stage.setTitle("Dolphin says 'hi'!");
        stage.setScene(scene);
        stage.show();
        System.out.println("done");
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}



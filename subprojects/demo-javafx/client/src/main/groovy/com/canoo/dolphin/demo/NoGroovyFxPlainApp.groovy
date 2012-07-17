package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage

import static com.canoo.dolphin.binding.Binder.bind
import static com.canoo.dolphin.demo.MyProps.TEXT
import static com.canoo.dolphin.demo.MyProps.TITLE

public class NoGroovyFxPlainApp extends Application {

    @Override public void start(Stage stage) {

        // construct the view
        stage.title = ""

        Group root = new Group()
        Scene scene = new Scene(root)
        stage.scene = scene

        def vbox = new VBox()
        def label = new Label("")
        def textField = new TextField()
        vbox.children << label
        vbox.children << textField
        root.children << vbox
        
        // construct the PM
        def titleAttr = new ClientAttribute('title')
        def pm = new ClientPresentationModel('demo',[titleAttr])
        pm.title.value = "Hello JavaFX"

        stage.titleProperty().bind(label.textProperty()) // JavaFX: changes to label will be propagated to the stage title

        // bind the view onto the PM
        bind TITLE of pm to TEXT of label
        bind(TITLE).of(pm).to(TEXT).of(textField) // java style

        textField.onAction = { titleAttr.value = textField.text } as EventHandler

        // let the show begin
        stage.show()
    }
    


    public static void main(String[] args) {
        Application.launch(NoGroovyFxPlainApp, args)
    }
}



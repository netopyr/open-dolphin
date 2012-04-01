package com.canoo.dolphin.demo

import javafx.scene.Scene
import groovyx.javafx.SceneGraphBuilder
import javafx.scene.layout.GridPane
import static javafx.geometry.HPos.*
import static javafx.scene.layout.Priority.ALWAYS
import javafx.stage.Stage

class DemoStyle {

    static blueStyle(SceneGraphBuilder sgb){
        sgb.with {
            primaryStage.scene.fill = radialGradient(stops: [groovyblue.brighter(), groovyblue.darker()]).build()
            primaryStage.scene.stylesheets << 'demo.css'
        }
    }

    static style(SceneGraphBuilder sgb) {
        blueStyle(sgb)
        Stage frame = sgb.primaryStage
        Scene scene = frame.scene

        GridPane grid = scene.root
        grid.styleClass << 'form'
        grid.hgap = 5  // for some reason, the gaps are not taken from the css
        grid.vgap = 10
        grid.columnConstraints << sgb.columnConstraints(halignment: RIGHT, hgrow: ALWAYS)
        grid.columnConstraints << sgb.columnConstraints(halignment: LEFT,  hgrow: ALWAYS)

        sgb.translateTransition(1.s, node: grid, fromY: -100, toY: 0).play()
    }
}

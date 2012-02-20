package com.canoo.dolphin.demo

import javafx.scene.Scene
import groovyx.javafx.SceneGraphBuilder
import javafx.scene.layout.GridPane
import static javafx.geometry.HPos.*
import static javafx.scene.layout.Priority.ALWAYS
import javafx.stage.Stage

class DemoStyle {

    static style(SceneGraphBuilder sgb) {
        Stage frame = sgb.primaryStage
        Scene scene = frame.scene
        def groovyblue = sgb.groovyblue
        scene.fill = sgb.radialGradient(stops: [
            groovyblue.brighter(),
            groovyblue.darker()]
        ).build() // a scene fill cannot be set via css

        GridPane grid = scene.root
        grid.styleClass << 'form'
        grid.hgap = 5  // for some reason, the gaps are not taken from the css
        grid.vgap = 10
        grid.columnConstraints << sgb.columnConstraints(halignment: RIGHT, hgrow: ALWAYS)
        grid.columnConstraints << sgb.columnConstraints(halignment: LEFT,  hgrow: ALWAYS)

        scene.stylesheets << 'demo.css'

        sgb.translateTransition(1.s, node: grid, fromY: -100, toY: 0).play()
    }
}

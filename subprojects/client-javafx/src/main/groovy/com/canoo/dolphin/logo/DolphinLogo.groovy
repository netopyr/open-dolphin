package com.canoo.dolphin.logo

import groovyx.javafx.SceneGraphBuilder
import javafx.scene.shape.Path

class DolphinLogo {
    long width, height
    boolean shuffle = false
    def effect = null

    def addTo(SceneGraphBuilder builder) {
        List<Path> strokes = new DolphinLogoPaths(width, height).paths()
        builder.stackPane {
            rectangle x: 0, y: 0, width: width, height: height, opacity: 0d
            allAnimations = parallelTransition()
            group id: 'dolphinLogoStrokes', effect: effect, {
                for (stroke in strokes) {
                    path(stroke, rotate: shuffle ? Math.random() * 360 : 0) {
                        allAnimations.children <<
                            rotateTransition(3.s, to: 0)
                    }
                }
            }
            onMouseClicked { allAnimations.playFromStart() }
        }
    }
}
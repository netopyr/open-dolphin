
package com.canoo.dolphin.logo

import groovyx.javafx.GroovyFX
import javafx.scene.shape.Path

GroovyFX.start { app ->

    List<Path> strokes = new DolphinLogoPaths(401, 257).paths()

    stage title: "Tickle the Dolphin!", {
        scene width:441, height: 297, {
            stackPane id:'stack', {
                rectangle x: 0, y: 0, width: 441, height: 297, opacity: 0d
                allAnimations = parallelTransition()
                group id: 'logo',{
                    for(stroke in strokes){
                        path(stroke) {
                            allAnimations.children <<
                                rotateTransition(2.s, to: -360, onFinished: { it.source.targetNode.rotate = 0d} )
                        }
                    }
                }
                onMouseClicked { allAnimations.playFromStart() }
            }
        }
    }

    primaryStage.show()
}

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.comm.NamedCommand
import static groovyx.javafx.GroovyFX.start
import java.beans.PropertyChangeListener

class PushView {

    static show() {

        def communicator = InMemoryClientConnector.instance

        start { app ->
            def sgb = delegate
            def rects = [:]
            stage {
                scene width: 400, height: 400, {
                    stackPane {
                        group id:'parent', {
                            rectangle(x: 0, y: 0, width: 400, height: 400, fill: transparent) // rigidArea
            }   }   }   }

            communicator.send(new NamedCommand(id: 'pullVehicles')) { pmIds ->
                pmIds.each { id ->
                    rects[id] = rectangle(fill: sgb[id], arcWidth:10, arcHeight:10) {
                        effect lighting(surfaceScale: 1.0) { distant(azimuth: 180.0) }
                    }
                    def pm = communicator.modelStore[id]
                    pm.attributes*.propertyName.each { prop ->
                        rects[id][prop] = pm[prop].value
                        pm[prop].addPropertyChangeListener 'value', {
                            timeline {
                                at(0.5.s) { change(rects[id], prop) to pm[prop].value tween "ease_both" }
                            }.play()
                        } as PropertyChangeListener
                    }
                    parent.children << rects[id]
                }
                def longPoll
                longPoll = { communicator.send(new NamedCommand(id: "longPoll"), longPoll) }
                longPoll()
            }
            primaryStage.show()
        }
    }
}

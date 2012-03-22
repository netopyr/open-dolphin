package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import static groovyx.javafx.GroovyFX.start
import com.canoo.dolphin.core.comm.NamedCommand

class PushView {

    static show() {

        def communicator = InMemoryClientConnector.instance


        start { app ->
            ObservableMap rects = communicator.modelStore

            rects.addPropertyChangeListener({ PropertyChangeEvent evt ->
                if (!evt.hasProperty('type')) return
                if (evt.type.value == ObservableMap.ChangeType.ADDED.ordinal()) {
                    println "-> $evt.newValue"
                    bind pm.x to rects.x
                    //parent.children.add(rectangle(evt.newValue))
                }
            } as PropertyChangeListener)

            stage {
                scene width: 400, height: 400, {
                    parent = borderPane {
                        onMouseClicked {
                            communicator.send(new NamedCommand(id: 'pullPm'))
                            communicator.send(new NamedCommand(id: 'pullValues'))
                        }
                    }

                }

            }
            primaryStage.show()
        }

    }
}

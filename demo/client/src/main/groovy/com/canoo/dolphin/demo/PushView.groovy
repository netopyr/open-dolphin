package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

import static groovyx.javafx.GroovyFX.start
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.binding.JFXBinder

import static com.canoo.dolphin.binding.JFXBinder.bind
import groovyx.javafx.Trigger

class PushView {

    static show() {

        def communicator = InMemoryClientConnector.instance

        start { app ->

            stage {
                scene width: 400, height: 400, {
                    parent = borderPane {
                        onMouseClicked {
                            communicator.send(new NamedCommand(id: 'pullPm')) { pms ->
                                communicator.send(new NamedCommand(id: 'pullValues')) {
                                    pms.each {
                                        def pm = communicator.modelStore[it]
                                        def rect = rectangle()
                                        pm.attributes*.propertyName.each { prop ->
                                            bind prop of pm to prop of rect
                                        }
                                        parent.children << rect
            }   }   }   }   }   }   }
            primaryStage.show()
        }
    }
}

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.comm.NamedCommand

import static groovyx.javafx.GroovyFX.start
import com.canoo.dolphin.binding.JFXBinder

import static com.canoo.dolphin.binding.JFXBinder.bind
import javafx.application.Platform

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
                            rectangle(x: 0, y: 0, width: 400, height: 400, fill: transparent)
                            onMouseClicked {
                                def callback
                                callback = { println it; communicator.send(new NamedCommand(id: "randomMove"), callback) }
                                callback()
                            }
                        }
                    }
                }
            }

            communicator.send(new NamedCommand(id: 'pullBlackAndBlueRect')) { pmIds ->
                pmIds.each { id ->
                    rects[id] = sgb.rectangle {
                        onMouseClicked {
                            timeline {
                                at(1.s) {
                                    change(rects[id], "x") to 0 tween "ease_both"
                                    change(rects[id], "y") to 0 tween "ease_both"
                                }
                            }.play()
                        }
                    }
                    def pm = communicator.modelStore[id]
                    pm.attributes*.propertyName.each {
                        bind it of pm to it of rects[id]
                    }
                    parent.children << rects[id]
                }
            }
            primaryStage.show()
        }
    }
}

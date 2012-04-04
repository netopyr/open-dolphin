package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.comm.NamedCommand
import java.beans.PropertyChangeListener
import javafx.event.EventHandler
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.comm.SwitchPmCommand
import javafx.scene.shape.Rectangle

import static groovyx.javafx.GroovyFX.start
import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.VehicleProperties.*
import static com.canoo.dolphin.demo.DemoStyle.blueStyle

class PushView {

    static show() {

        def communicator = InMemoryClientConnector.instance
        def selectedVehicle = new ClientPresentationModel(
                'selectedVehicle',
                [X, Y, WIDTH, HEIGHT, ROTATE].collect { new ClientAttribute(it) }
        )

        start { app ->
            def sgb = delegate
            def rects = [:] // pmId to rectangle
            stage {
                scene width: 500, height: 500, {
                    borderPane {
                        top {
                            hbox translateX:50, translateY: 20, spacing:5, id:'header', {
                                label 'selected'
                                rectangle(id:'selRect', fill: transparent, arcWidth:10, arcHeight:10, width:74, height:20) {
                                    effect dropShadow(offsetY:2,radius:3)
                                }
                                label ' x:';     label id: 'selX'
                                label ' y:';     label id: 'selY'
                                label ' angle:'; label id: 'selAngle'
                            }
                        }
                        stackPane {
                            group id:'parent', effect: dropShadow(offsetY:2,radius:3), {
                                rectangle(x: 0, y: 0, width: 400, height: 400, fill: transparent, stroke: groovyblue, strokeWidth: 0.5) // rigidArea
            }   }   }   }   }

            communicator.send(new NamedCommand(id: 'pullVehicles')) { pmIds ->
                pmIds.each { id ->
                    rects[id] = rectangle(fill: sgb[id], arcWidth:10, arcHeight:10, stroke: cyan, strokeWidth: 0, strokeType:'outside') {
                        effect lighting()
                    }
                    Rectangle rectangle = rects[id]
                    rectangle.onMouseClicked = {
                        rects.values().each { it.strokeWidth = 0 }
                        communicator.send(new SwitchPmCommand(pmId: selectedVehicle.id, sourcePmId: id))
                        selRect.fill = rectangle.fill
                        rectangle.strokeWidth = 3
                    } as EventHandler
                    def pm = communicator.modelStore[id]
                    pm.attributes*.propertyName.each { prop ->
                        rectangle[prop] = pm[prop].value
                        pm[prop].addPropertyChangeListener 'value', { evt ->
                            timeline {
                                at(0.5.s) { change(rectangle, prop) to evt.newValue tween "ease_both" }
                            }.play()
                        } as PropertyChangeListener
                    }
                }
                parent.children.addAll rects.values()
                def longPoll
                longPoll = {
                    communicator.send(new NamedCommand(id: "longPoll"), longPoll)
                }
                longPoll()
            }
            blueStyle sgb

            bind X      of selectedVehicle to 'text' of selX
            bind Y      of selectedVehicle to 'text' of selY
            bind ROTATE of selectedVehicle to 'text' of selAngle

            primaryStage.show()
        }
    }
}

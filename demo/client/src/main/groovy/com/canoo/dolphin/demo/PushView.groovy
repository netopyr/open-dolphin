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

import javafx.util.Callback
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections

class PushView {

    static show() {

        def communicator = InMemoryClientConnector.instance
        def selectedVehicle = new ClientPresentationModel(
                'selectedVehicle',
                [X, Y, WIDTH, HEIGHT, ROTATE].collect { new ClientAttribute(it) }
        )
        def obsPmList = FXCollections.observableArrayList()
        def rects = [:] // pmId to rectangle

        start { app ->
            def sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        top margin:10, {
                            rectangle(x: 0, y: 0, width: 1, height: 40, fill: transparent) // rigidArea
                            hbox alignment:'center', prefWidth: 700, spacing:5, id:'header', {
                                label 'selected'
                                rectangle(id:'selRect', fill: transparent, arcWidth:10, arcHeight:10, width:74, height:20, stroke: cyan, strokeWidth: 2, strokeType:'outside') {
                                    effect dropShadow(offsetY:2,radius:3)
                                }
                                label ' x:';     label id: 'selX'
                                label ' y:';     label id: 'selY'
                                label ' angle:'; label id: 'selAngle'
                            }
                        }
                        left margin:10, {
                            tableView(id: 'table') {
                                tableColumn(property:'id', text:"Color", prefWidth: 50 )
                                xCol   = tableColumn(text:'X', prefWidth: 40)
                                yCol   = tableColumn(text:'Y', prefWidth: 40)
                                rotCol = tableColumn(text:'Angle')
                            }
                        }
                        stackPane {
                            group id: 'parent', effect: dropShadow(offsetY: 2, radius: 3), {
                                rectangle(x: 0, y: 0, width: 400, height: 400, fill: transparent, stroke: groovyblue, strokeWidth: 0.5) // rigidArea
            }   }   }   }   }

            table.items = obsPmList

            // auto-update the cell values
            xCol.cellValueFactory   = { return it.getValue().x.valueProperty() } as Callback
            yCol.cellValueFactory   = { return it.getValue().y.valueProperty() } as Callback
            rotCol.cellValueFactory = { return it.getValue().rotate.valueProperty() } as Callback

            // used as both, event handler and change listener
            def changeSelectionHandler = { id ->
                return {
                    rects.values().each { it.strokeWidth = 0 }
                    communicator.send(new SwitchPmCommand(pmId: selectedVehicle.id, sourcePmId: id))
                    def rectangle = rects[id]
                    sgb.selRect.fill = rectangle.fill // todo: should not know the view!
                    rectangle.strokeWidth = 3

                    def pm = communicator.clientModelStore.findPmById(id)
                    sgb.table.selectionModel.select pm // todo: should not know the view!
                }
            }

            communicator.send(new NamedCommand(id: 'pullVehicles')) { pmIds ->
                pmIds.each { id ->
                    rects[id] = rectangle(fill: sgb[id], arcWidth:10, arcHeight:10, stroke: cyan, strokeWidth: 0, strokeType:'outside') {
                        effect lighting()
                    }
                    Rectangle rectangle = rects[id]
                    rectangle.onMouseClicked = changeSelectionHandler(id) as EventHandler
                    def pm = communicator.clientModelStore.findPmById(id)
                    obsPmList.add pm
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

            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, newVal ->
                changeSelectionHandler(newVal.id).call()
            } as ChangeListener )

            primaryStage.show()
        }
    }
}

class XY { def x, y }

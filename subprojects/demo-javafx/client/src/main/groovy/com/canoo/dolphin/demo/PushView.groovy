package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.comm.NamedCommand

import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientAttribute

import static groovyx.javafx.GroovyFX.start
import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.VehicleProperties.*
import static com.canoo.dolphin.demo.DemoStyle.blueStyle

import javafx.util.Callback
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ListChangeListener
import javafx.scene.shape.Rectangle
import javafx.event.EventHandler
import java.beans.PropertyChangeListener
import groovyx.javafx.SceneGraphBuilder
import com.canoo.dolphin.binding.Binder
import com.canoo.dolphin.core.ModelStore

class PushView {

    static show() {

        def communicator = InMemoryClientConnector.instance

        def longPoll
        longPoll = {
            communicator.send(new NamedCommand(id: "longPoll"), longPoll)
        }

        def selectedVehicle = new ClientPresentationModel(
                'selectedVehicle',
                [X, Y, WIDTH, HEIGHT, ROTATE, COLOR].collect { new ClientAttribute(it) }
        )
        communicator.clientModelStore.add selectedVehicle

        ObservableList<ClientPresentationModel> observableListOfPms = FXCollections.observableArrayList()
        Map<String, Rectangle> pmIdsToRect = [:] // pmId to rectangle

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        top margin:10, {
                            //rectangle(x: 0, y: 0, width: 1, height: 40, fill: transparent) // rigidArea only needed with the b15, no longer with b19
                            hbox alignment:'center', prefWidth: 700, spacing:5, id:'header', {
                                label 'Selected'
                                rectangle(id:'selRect', arcWidth:10, arcHeight:10, width:74, height:20, stroke: cyan, strokeWidth: 2, strokeType:'outside') {
                                    effect dropShadow(offsetY:2, offsetX:2, radius:3, input: lighting{distant(azimuth: -135.0)})
                                }
                                label ' X:';     textField id: 'selX', prefColumnCount:3
                                label ' Y:';     textField id: 'selY', prefColumnCount:3
                                label ' Angle:'; rectangle id: 'selAngle', width:26, height:5, fill: linearGradient(stops: [[0.6, white], [1, red]])
                            }
                        }
                        left margin:10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property:'id', text:"Color", prefWidth: 50 )
                                xCol   = tableColumn(text:'X', prefWidth: 40)
                                yCol   = tableColumn(text:'Y', prefWidth: 40)
                                rotCol = tableColumn(text:'Angle')
                            }
                        }
                        stackPane {
                            group id: 'parent', effect: dropShadow(offsetY: 2, offsetX: 2, radius: 3, input: lighting{distant(azimuth: -135.0)}), {
                                rectangle(x: 0, y: 0, width: 400, height: 400, fill: transparent, stroke: groovyblue, strokeWidth: 0.5) // rigidArea
            }   }   }   }   }

            table.items = observableListOfPms

            // auto-update the cell values
            xCol.cellValueFactory   = { return it.getValue().x.valueProperty() } as Callback
            yCol.cellValueFactory   = { return it.getValue().y.valueProperty() } as Callback
            rotCol.cellValueFactory = { return it.getValue().rotate.valueProperty() } as Callback

            // used as both, event handler and change listener
            def changeSelectionHandler = { pm ->
                return {
                    communicator.switchPmAndSend selectedVehicle, pm
                }
            }

            // when a new pm is added to the list, create the rectangles along with their animations
            observableListOfPms.addListener({ ListChangeListener.Change listChange ->
                while(listChange.next()) { /*sigh*/
                    for (ClientPresentationModel pm in listChange.addedSubList) {
                        pmIdsToRect[pm.id] = sgb.rectangle(fill: sgb[pm.id], arcWidth:10, arcHeight:10, stroke: cyan, strokeWidth: 0, strokeType:'outside') {
                            //effect lighting()
                        }
                        Rectangle rectangle = pmIdsToRect[pm.id]
                        rectangle.onMouseClicked = changeSelectionHandler(pm) as EventHandler
                        pm.attributes*.propertyName.each { prop ->
                            if(prop == 'fill') return // only for the moment - until we convert types
                            rectangle[prop] = pm[prop].value
                            pm[prop].addPropertyChangeListener 'value', { evt ->
                                sgb.timeline {
                                    at(0.5.s) { change(rectangle, prop) to evt.newValue tween "ease_both" }
                                }.play()
                            } as PropertyChangeListener
                        }
                        sgb.parent.children << rectangle
                    }
                }
            } as ListChangeListener)

            // startup and main loop

            communicator.send(new NamedCommand(id: 'pullVehicles')) { pmIds ->
                for (id in pmIds) {
                    observableListOfPms << communicator.clientModelStore.findPresentationModelById(id)
                }
                fadeTransition(1.s, node:table, to:1).playFromStart()
                longPoll()
            }
            blueStyle sgb

            // all the bindings ...

            bind X      of selectedVehicle to 'text' of selX // simple binding + action
            selX.onAction = { selectedVehicle.x.value = it.source.text.toInteger() } as EventHandler

            bind Y      of selectedVehicle to 'text' of selY // example of a "bidirectional" binding
            bind 'text' of selY            to Y      of selectedVehicle, { it ? it.toInteger() : 0 }

            bind ROTATE of selectedVehicle to 'rotate' of selAngle, { (it ?: 0 ).toDouble() }
            bind COLOR  of selectedVehicle to 'fill' of selRect,    { it ? sgb[it] : sgb.transparent }

            // bind 'selectedItem' of table.selectionModel to { ... }
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
                changeSelectionHandler(selectedPm).call()
            } as ChangeListener )

            // bind COLOR of selectedVehicle to { ... }
            selectedVehicle[COLOR].valueProperty().addListener( { o, from, to ->
                if (from) pmIdsToRect[from].strokeWidth = 0
                pmIdsToRect[to].strokeWidth = 3
                table.selectionModel.select communicator.clientModelStore.findPresentationModelById(to)
            } as ChangeListener)

            primaryStage.show()
        }
    }
}

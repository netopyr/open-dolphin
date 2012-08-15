package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.logo.DolphinLogo
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.shape.Rectangle
import javafx.util.Callback

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static com.canoo.dolphin.demo.VehicleProperties.*
import static groovyx.javafx.GroovyFX.start
import com.canoo.dolphin.core.client.ClientAttributeWrapper

class PushView {

    static show(ClientDolphin clientDolphin) {

        def communicator = clientDolphin.clientConnector

        def longPoll
        longPoll = {
            communicator.send(new NamedCommand(id: "longPoll"),
                longPoll as OnFinishedHandler)
        }

        def selectedVehicle = clientDolphin.presentationModel('selectedVehicle', [ATT_X, ATT_Y, ATT_WIDTH, ATT_HEIGHT, ATT_ROTATE, ATT_COLOR])

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
                            logo = new DolphinLogo(width:401, height: 257).addTo(delegate)
                            group id: 'parent', effect: dropShadow(offsetY: 2, offsetX: 2, radius: 3, input: lighting{distant(azimuth: -135.0)}), {
                                rectangle(x: 0, y: 0, width: 400, height: 400, fill: transparent, stroke: groovyblue, strokeWidth: 0) // rigidArea
            }   }   }   }   }
            logo.opacity = 0.1d

            table.items = observableListOfPms

            // auto-update the cell values
            xCol.cellValueFactory   = { return new ClientAttributeWrapper(it.value.ATT_X) } as Callback
            yCol.cellValueFactory   = { return new ClientAttributeWrapper(it.value.ATT_Y) } as Callback
            rotCol.cellValueFactory = { return new ClientAttributeWrapper(it.value.rotate) } as Callback

            // used as both, event handler and change listener
            def changeSelectionHandler = { pm ->
                return {
                    communicator.switchPresentationModelAndSend selectedVehicle, pm
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

            communicator.send(new NamedCommand(id: 'pullVehicles'), { pms ->
                for (pm in pms) {
                    observableListOfPms << pm
                }
                fadeTransition(1.s, node:table, to:1).playFromStart()
                longPoll()
            } as OnFinishedHandler )
            blueStyle sgb

            // all the bindings ...

            bind ATT_X      of selectedVehicle to 'text' of selX // simple binding + action
            selX.onAction = { selectedVehicle.ATT_X.value = it.source.text.toInteger() } as EventHandler

            bind ATT_Y      of selectedVehicle to 'text' of selY // example of a "bidirectional" binding
            bind 'text' of selY            to ATT_Y      of selectedVehicle, { it ? it.toInteger() : 0 }

            bind ATT_ROTATE of selectedVehicle to 'rotate' of selAngle, { (it ?: 0 ).toDouble() }
            bind ATT_COLOR  of selectedVehicle to 'fill' of selRect,    { it ? sgb[it] : sgb.transparent }

            // bind 'selectedItem' of table.selectionModel to { ... }
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
                changeSelectionHandler(selectedPm).call()
            } as ChangeListener )

            // bind COLOR of selectedVehicle to { ... }
            selectedVehicle[ATT_COLOR].addPropertyChangeListener('value', { evt ->
                def from = evt.oldValue
                def to   = evt.newValue
                if (from ) pmIdsToRect[from].strokeWidth = 0
                pmIdsToRect[to].strokeWidth = 3
            } as PropertyChangeListener)

            selectedVehicle[ATT_COLOR].addPropertyChangeListener('value', { evt ->
                def to   = evt.newValue
                table.selectionModel.select clientDolphin.clientModelStore.findPresentationModelById(to)
            } as PropertyChangeListener)

            primaryStage.show()
        }
    }
}

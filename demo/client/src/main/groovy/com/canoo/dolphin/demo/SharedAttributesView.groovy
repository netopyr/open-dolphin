package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.comm.NamedCommand
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections

import javafx.collections.ObservableList

import javafx.util.Callback

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static com.canoo.dolphin.demo.VehicleProperties.*
import static groovyx.javafx.GroovyFX.start

class SharedAttributesView {

    static show() {

        def communicator = InMemoryClientConnector.instance

        ObservableList<ClientPresentationModel> observableListOfPms   = FXCollections.observableArrayList()
        ObservableList<ClientPresentationModel> observableListOfTasks = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        left margin:10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property:'id', text:"Color", prefWidth: 50 )
                                xCol   = tableColumn(text:'X', prefWidth: 40)
                                yCol   = tableColumn(text:'Y', prefWidth: 40)
                                rotCol = tableColumn(text:'Angle')
                            }
                        }
                        center margin:[10,0,10,0], {
                            tabPane 'vehicles', id:'vehicles'
                        }
                        right margin:10, {
                            tableView(id: 'taskTable', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "descr", prefWidth: 100)
                                vehicleFillCol = tableColumn(text: 'Vehicle Color', prefWidth: 50)
                                vehicleX = tableColumn(text: 'Vehicle X', prefWidth: 50)
                            }
                        }
              }   }   }

            table.items = observableListOfPms
            taskTable.items = observableListOfTasks

            // auto-update the cell values
            xCol.cellValueFactory   = { return it.getValue().x.valueProperty() } as Callback
            yCol.cellValueFactory   = { return it.getValue().y.valueProperty() } as Callback
            rotCol.cellValueFactory = { return it.getValue().rotate.valueProperty() } as Callback

            vehicleFillCol.cellValueFactory = { return it.getValue().vehicleFill.valueProperty() } as Callback
            vehicleX.cellValueFactory       = { return it.getValue().vehicleX.valueProperty() } as Callback

            // used as both, event handler and change listener
            def changeSelectionHandler = { pm ->
                return {
                    // todo: the selection handler should not know anything about the views (tabpane) that are
                    // affected by the selection change. Instead, use a "selectedVehicle" PM and bind the
                    // tab view to its value change. Also consider the "tab closed" case.
                    def tab = sgb.vehicles.tabs.find { it.id == pm.id }
                    if (! tab) {
                        tab = sgb.tab id: pm.id, {
                            gridPane(hgap: 5, vgap: 5, padding: 10, alignment: "top_left") {
                                columnConstraints(halignment: "right")
                                text ' X:', row: 0, column:0
                                textField id:'x', prefColumnCount:3, row: 0, column:1
                                text ' Y:', row: 1, column: 0
                                textField id:'y', prefColumnCount:3, row: 1, column: 1
                                text ' Angle:', row: 2, column:0
                                textField id:'angle', prefColumnCount:3, row: 2, column: 1
                                text ' Width:', row: 3, column:0
                                textField id:'width', prefColumnCount:3, row: 3, column: 1
                            }
                        }

                        communicator.withPm('vehicleDetail', pm.id) { ClientPresentationModel detailPm ->
                            assert detailPm

                            bind COLOR of detailPm to 'text' of tab

                            bind X of detailPm to 'text' of sgb.x
                            bind 'text' of sgb.x to X of detailPm

                            bind Y of detailPm to 'text' of sgb.y
                            bind ROTATE of detailPm to 'text' of sgb.angle

                            bind WIDTH of detailPm to 'text' of sgb.width
                            bind 'text' of sgb.width to WIDTH of detailPm
                        }

                        sgb.vehicles.tabs << tab
                    }
                    sgb.vehicles.selectionModel.select(tab)
                }
            }

            // startup and main loop

            communicator.send(new NamedCommand(id: 'pullVehicles')) { pmIds ->
                for (id in pmIds) {
                    observableListOfPms << communicator.clientModelStore.findPmById(id)
                }
                fadeTransition(1.s, node:table, to:1).playFromStart()
            }

            communicator.send(new NamedCommand(id: 'pullTasks')) { pmIds ->
                for (id in pmIds) {
                    observableListOfTasks << communicator.clientModelStore.findPmById(id)
                }
                fadeTransition(1.s, node: taskTable, to: 1).playFromStart()
            }

            blueStyle sgb

            // all the bindings ...

            // bind 'selectedItem' of table.selectionModel to { ... }
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
                changeSelectionHandler(selectedPm).call()
            } as ChangeListener )


            primaryStage.show()
        }
    }
}

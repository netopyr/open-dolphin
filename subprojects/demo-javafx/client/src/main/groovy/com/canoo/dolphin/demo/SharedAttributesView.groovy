package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.Dolphin
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

        def communicator = Dolphin.clientConnector

        def selectedVehicle = new ClientPresentationModel('selectedVehicle', [new ClientAttribute('vehiclePmId')])
        Dolphin.clientModelStore.add selectedVehicle

        ObservableList<ClientPresentationModel> observableListOfPms = FXCollections.observableArrayList()
        ObservableList<ClientPresentationModel> observableListOfTasks = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        left margin: 10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "Color", prefWidth: 50)
                                xCol = tableColumn(text: 'X', prefWidth: 40)
                                yCol = tableColumn(text: 'Y', prefWidth: 40)
                                rotCol = tableColumn(text: 'Angle')
                            }
                        }
                        center margin: [10, 0, 10, 0], {
                            tabPane id: 'vehicles'
                        }
                        right margin: 10, {
                            tableView(id: 'taskTable', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "descr", prefWidth: 100)
                                vehicleFillCol = tableColumn(text: 'Vehicle Color', prefWidth: 50)
                                vehicleXCol = tableColumn(text: 'Vehicle X', prefWidth: 50)
                            }
                        }
                    }
                }
            }

            table.items = observableListOfPms
            taskTable.items = observableListOfTasks

            // auto-update the cell values
            xCol.cellValueFactory = { return it.getValue().x.valueProperty() } as Callback
            yCol.cellValueFactory = { return it.getValue().y.valueProperty() } as Callback
            rotCol.cellValueFactory = { return it.getValue().rotate.valueProperty() } as Callback

            vehicleFillCol.cellValueFactory = { return it.getValue().fill.valueProperty() } as Callback
            vehicleXCol.cellValueFactory = { return it.getValue().x.valueProperty() } as Callback

            // startup and main loop

            communicator.send(new NamedCommand(id: 'pullVehicles')) { pmIds ->
                for (id in pmIds) {
                    observableListOfPms << Dolphin.clientModelStore.findPresentationModelById(id)
                }
                fadeTransition(1.s, node: table, to: 1).playFromStart()
            }

            communicator.send(new NamedCommand(id: 'pullTasks')) { pmIds ->
                for (id in pmIds) {
                    observableListOfTasks << Dolphin.clientModelStore.findPresentationModelById(id)
                }
                fadeTransition(1.s, node: taskTable, to: 1).playFromStart()
            }

            blueStyle sgb

            // all the bindings ...

            // bind 'selectedItem' of table.selectionModel to { ... }
            table.selectionModel.selectedItemProperty().addListener({ o, oldVal, selectedPm ->
                selectedVehicle.vehiclePmId.value = selectedPm.id
            } as ChangeListener)

            taskTable.selectionModel.selectedItemProperty().addListener({ o, oldVal, selectedPm ->
                selectedVehicle.vehiclePmId.value = selectedPm.fill.value
            } as ChangeListener)

            selectedVehicle.vehiclePmId.valueProperty().addListener({ o, oldVal, selectedPmId ->
                def tab = sgb.vehicles.tabs.find { it.id == selectedPmId }
                if (!tab) {
                    def grid
                    tab = sgb.tab id: selectedPmId, {
                        grid = gridPane hgap: 5, vgap: 5, padding: 10, alignment: "top_left", opacity: 0.3d, translateY: -200, {
                            columnConstraints(halignment: "right")
                            text ' X:', row: 0, column: 0
                            textField id: 'x', prefColumnCount: 3, row: 0, column: 1
                            text ' Y:', row: 1, column: 0
                            textField id: 'y', prefColumnCount: 3, row: 1, column: 1
                            text ' Angle:', row: 2, column: 0
                            textField id: 'angle', prefColumnCount: 3, row: 2, column: 1
                            text ' Width:', row: 3, column: 0
                            textField id: 'width', prefColumnCount: 3, row: 3, column: 1
                            translateTransition(0.5.s, to: 0).playFromStart()
                        }
                    }

                    communicator.withPm('vehicleDetail', selectedPmId) { ClientPresentationModel detailPm ->
                        assert detailPm

                        bind COLOR of detailPm to 'text' of tab

                        bind X of detailPm to 'text' of sgb.x
                        bind 'text' of sgb.x to X of detailPm

                        bind Y of detailPm to 'text' of sgb.y
                        bind ROTATE of detailPm to 'text' of sgb.angle

                        bind WIDTH of detailPm to 'text' of sgb.width
                        bind 'text' of sgb.width to WIDTH of detailPm

                        fadeTransition(1.s, node: grid, to: 1).playFromStart()
                    }
                    sgb.vehicles.tabs << tab
                }
                sgb.vehicles.selectionModel.select(tab)
            } as ChangeListener)

            primaryStage.show()
        }
    }
}

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import groovyx.javafx.SceneGraphBuilder

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Callback

import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static com.canoo.dolphin.demo.VehicleProperties.*

import static groovyx.javafx.GroovyFX.start
import javafx.event.EventHandler

import com.canoo.dolphin.core.client.ClientAttributeWrapper

/**
 * This demos shows two list views on the same list of PresentationModels where one list view shows
 * all models of a given type and the second view shows only a subset (the "magenta" ones).
 * It also shows how to bind against a (changing) list of PresentationModels of a certain type and how
 * to use an additional custom filter.
 * How to use: initially, the right view should be empty (no magenta ones).
 * Clicking the button adds magenta objects to the store and they should appear in both list views.
 */

class BindListView {

    static show(ClientDolphin dolphin) {

        ObservableList<ClientPresentationModel> observableListOfPms = FXCollections.observableArrayList()
        ObservableList<ClientPresentationModel> observableListOfMagentaPms = FXCollections.observableArrayList()

        dolphin.onPresentationModelListChanged PM_TYPE_VEHICLE,
           added:   { observableListOfPms << it },
           removed: { observableListOfPms.remove(it) }

        dolphin.onPresentationModelListChanged PM_TYPE_VEHICLE,
           added: { if (it.id.startsWith('magenta')) observableListOfMagentaPms << it }

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        left margin: 10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "Color", prefWidth: 50)
                                x1Col = tableColumn(text: 'X', prefWidth: 40)
                            }
                        }
                        center margin: 10, {
                            tableView(id: 'smallTable', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "Color", prefWidth: 50)
                                x2Col = tableColumn(text: 'X', prefWidth: 40)
                            }
                        }

                        right margin: 10, {
                            button id:'add', text:'Add'
                        }
                    }
                }
            }

            blueStyle sgb

            table.items = observableListOfPms
            smallTable.items = observableListOfMagentaPms

			add.onAction = {
                dolphin.presentationModel "magenta_${System.currentTimeMillis()}",
                   PM_TYPE_VEHICLE,
                   (ATT_X) : 0
			} as EventHandler

            // auto-update the cell values
            x1Col.cellValueFactory = { new ClientAttributeWrapper(it.value[ATT_X]) } as Callback
            x2Col.cellValueFactory = { new ClientAttributeWrapper(it.value[ATT_X]) } as Callback

            dolphin.send CMD_PULL, { pms ->
                fadeTransition(1.s, node: table,      to: 1).playFromStart()
                fadeTransition(1.s, node: smallTable, to: 1).playFromStart()
            }

            // startup and main loop
            primaryStage.show()
        }
    }
}

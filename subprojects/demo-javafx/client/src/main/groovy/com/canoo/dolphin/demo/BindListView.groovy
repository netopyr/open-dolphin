package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.Dolphin
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.comm.NamedCommand
import groovyx.javafx.SceneGraphBuilder

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Callback

import static com.canoo.dolphin.demo.DemoStyle.blueStyle

import static groovyx.javafx.GroovyFX.start
import javafx.event.EventHandler

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.PresentationModelListChangedListener
import com.canoo.dolphin.core.client.ClientAttributeWrapper


class BindListView {

    static show() {

        def communicator = Dolphin.clientConnector

        ObservableList<ClientPresentationModel> observableListOfPms = FXCollections.observableArrayList()
        ObservableList<ClientPresentationModel> observableListOfSmallPms = FXCollections.observableArrayList()

		Dolphin.clientModelStore.onPresentationModelListChanged('vehicle', [ added: { observableListOfPms << it }, removed: { observableListOfPms.remove(it)} ] as PresentationModelListChangedListener)
		Dolphin.clientModelStore.onPresentationModelListChanged('vehicle', [ added: { if (it.id.startsWith('magenta')) observableListOfSmallPms << it } ] as PresentationModelListChangedListener)
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

            table.items = observableListOfPms
            smallTable.items = observableListOfSmallPms

			add.onAction = {
				ClientPresentationModel pm = new ClientPresentationModel("magenta_${System.currentTimeMillis()}", [new ClientAttribute('x', 0)])
				pm.presentationModelType = 'vehicle'
				Dolphin.clientModelStore.add(pm);
			} as EventHandler


            // auto-update the cell values
            x1Col.cellValueFactory = { return new ClientAttributeWrapper(it.value.x) } as Callback
            x2Col.cellValueFactory = { return new ClientAttributeWrapper(it.value.x) } as Callback

            // startup and main loop

            communicator.send(new NamedCommand(id: 'pullVehicles'), { pms ->
                fadeTransition(1.s, node: table, to: 1).playFromStart()
                fadeTransition(1.s, node: smallTable, to: 1).playFromStart()
            } as OnFinishedHandler )

            blueStyle sgb

            // all the bindings ...

            primaryStage.show()
        }
    }
}

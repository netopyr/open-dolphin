package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientDolphin
import javafx.collections.FXCollections
import javafx.util.Callback

import static com.canoo.dolphin.demo.DemoStyle.style
import static groovyx.javafx.GroovyFX.start
import com.canoo.dolphin.core.client.ClientAttributeWrapper

/**
 * This demo shows how to handle the case of presentation models created on the server
 * side as result of a client request
 */

class CreatePresentationModelView {
	static show(ClientDolphin clientDolphin) {
		start { app ->

            def tableModel = FXCollections.observableArrayList()
            clientDolphin.onPresentationModelListChanged 'person',
                    added:   { tableModel << it },
                    removed: { tableModel.remove(it) }

			stage {
				scene {
					gridPane {
						label id: 'header', row: 0, column: 1,
								'CreatePresentationModel example'

                        button id: 'createButton', 'Create', row: 1, column: 0,
                                onAction: { clientDolphin.send 'createNewPresentationModel' }
                        button id: 'dumpButton', 'Dump', row: 2, column: 0,
                                onAction: { clientDolphin.send 'dumpPresentationModels' }
						tableView(id: 'peopleTableView', row: 2, column: 1) {
							nameColumn     = tableColumn(text: 'Name',     prefWidth: 100)
                            lastnameColumn = tableColumn(text: 'Lastname', prefWidth: 100)
						}
					}
				}
			}

            peopleTableView.items = tableModel

            nameColumn.cellValueFactory     = { new ClientAttributeWrapper(it.value.name)     } as Callback
            lastnameColumn.cellValueFactory = { new ClientAttributeWrapper(it.value.lastname) } as Callback

			style delegate

			primaryStage.show()
		}
	}
}

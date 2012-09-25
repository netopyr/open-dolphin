/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientDolphin
import javafx.collections.FXCollections
import javafx.util.Callback

import static com.canoo.dolphin.demo.DemoStyle.style
import static groovyx.javafx.GroovyFX.start
import com.canoo.dolphin.core.client.ClientAttributeWrapper

import com.canoo.dolphin.core.ModelStoreEvent

/**
 * This demo shows how to handle the case of presentation models created on the server
 * side as result of a client request
 */

class CreatePresentationModelView {
	static show(ClientDolphin clientDolphin) {
		start { app ->

            def tableModel = FXCollections.observableArrayList()

            clientDolphin.addModelStoreListener 'person', { evt ->
                switch(evt.eventType) {
                    case ModelStoreEvent.EventType.ADDED:
                        tableModel << evt.presentationModel
                        break
                    case ModelStoreEvent.EventType.REMOVED:
                        tableModel.remove(evt.presentationModel)
                }
            }

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

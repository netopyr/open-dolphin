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

import com.canoo.dolphin.core.client.ClientAttributeWrapper
import com.canoo.dolphin.core.client.ClientDolphin
import javafx.collections.FXCollections
import javafx.util.Callback

import static com.canoo.dolphin.demo.DemoStyle.style
import static groovyx.javafx.GroovyFX.start
import static com.canoo.dolphin.demo.ReferenceTableDemoProperties.CURRENCY
import javafx.beans.value.ChangeListener

/**
 * Demonstrates link support between presentation models.
 * There are 3 parent nodes on the left side table (once the "Load" button is clicked).
 * The first 2 parents have parent/child relationships with other nodes (when the "Link" button is clicked).
 * Selecting a parent node on the left table should display its children nodes on the right table.
 * parent #3 has no children.
 */

class PresentationModelLinksView {
    static show(ClientDolphin clientDolphin) {
        start { app ->

            def parentTableModel = FXCollections.observableArrayList()
            def childrenTableModel = FXCollections.observableArrayList()
            clientDolphin.onPresentationModelListChanged 'parent',
                    added: { parentTableModel << it },
                    removed: { parentTableModel.remove(it) }

            stage {
                scene {
                    gridPane {
                        button id: 'loadButton', 'Load', row: 0, column: 0,
                                onAction: { clientDolphin.send('loadPms'); loadButton.disable = true }
                        button id: 'linkButton', 'Link', row: 0, column: 1,
                                onAction: { clientDolphin.send('linkPms'); linkButton.disable = true }

                        tableView(id: 'parentTableView', row: 1, column: 0) {
                            parentColumn0 = tableColumn(text: 'Column0', prefWidth: 100)
                            parentColumn1 = tableColumn(text: 'Column1', prefWidth: 100)
                        }

                        tableView(id: 'childrenTableView', row: 1, column: 1) {
                            childColumn0 = tableColumn(text: 'Column0', prefWidth: 100)
                            childColumn1 = tableColumn(text: 'Column1', prefWidth: 100)
                            childColumn2 = tableColumn(text: 'Column2', prefWidth: 100)
                        }
                    }
                }
            }

            parentTableView.items = parentTableModel
            childrenTableView.items = childrenTableModel

            parentColumn0.cellValueFactory = { new ClientAttributeWrapper(it.value.column0) } as Callback
            parentColumn1.cellValueFactory = { new ClientAttributeWrapper(it.value.column1) } as Callback
            childColumn0.cellValueFactory = { new ClientAttributeWrapper(it.value.column0) } as Callback
            childColumn1.cellValueFactory = { new ClientAttributeWrapper(it.value.column1) } as Callback
            childColumn2.cellValueFactory = { new ClientAttributeWrapper(it.value.column2) } as Callback

            parentTableView.selectionModel.selectedItemProperty().addListener({ o, oldVal, newVal ->
                if (newVal) {
                    childrenTableModel.clear()
                    childrenTableModel.addAll clientDolphin.modelStore.findAllLinksByModelAndType(newVal, 'PARENT_CHILD').end
                }
            } as ChangeListener)

            style delegate

            primaryStage.show()
        }
    }
}

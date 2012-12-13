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
import com.canoo.dolphin.core.ModelStoreEvent
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.WithPresentationModelHandler
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Callback

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start

/**
 * A demo that shows how to easily do lazy loading with the standard Dolphin on-board means.
 * Simply start and see how the table on the left-hand-side fills - depending on the sleepMillis
 * more or less quickly.
 * The details show how big the "imaginary" table is and how many items are loaded lazily from the server.
 * Clicking on any row will display the details in the textField - loaded as a presentation model.
 * Clicking in a not-yet-lazily loaded row does nothing.
 */

class LazyLoadingView {

    static show(ClientDolphin dolphin) {

        ClientPresentationModel dataMold = dolphin.presentationModel('dataMold', detail: null)

        ObservableList<Integer> observableList = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage title:"Dolphin lazy loading demo", {
                scene width: 700, height: 500, {
                    borderPane {
                        center margin:10, {
                            gridPane hgap:10, vgap:12, padding: 20, {
                                columnConstraints  halignment: "right"
                                columnConstraints  halignment: "right"
                                label     row: 0, column: 0, 'detail'
                                textField row: 0, column: 1, id: 'detailsField', prefColumnCount:10
                                label     row: 1, column: 0, 'table size:'
                                label     row: 1, column: 1, id:'tableSizeField'
                                label     row: 2, column: 0, 'lazily loaded:'
                                label     row: 2, column: 1, id:"lazilyLoadedField"
                            }
                        }
                        left margin:10, {
                            tableView(id: 'table') {
                                idCol = tableColumn(property:'id', text:"values", prefWidth: 100 )
                            }
                        }
            }   }   }
            blueStyle sgb

            table.items = observableList

            // all the bindings ...
            bind 'detail' of dataMold to 'text' of detailsField

            // cell values are lazily requested from JavaFX and must return an observable value
            idCol.cellValueFactory = {
                String lazyId = it.value['id']
                def placeholder = new SimpleStringProperty("...")
                dolphin.clientModelStore.withPresentationModel(lazyId, new WithPresentationModelHandler() {
                    void onFinished(ClientPresentationModel presentationModel) {
                        placeholder.setValue( presentationModel.detail.value ) // fill async lazily
                    }
                } )
                return placeholder
            } as Callback

            // when a table row is selected, we fill the mold and the detail view gets updated
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
                dolphin.clientModelStore.withPresentationModel(selectedPm.id.toString(), new WithPresentationModelHandler() {
                    void onFinished(ClientPresentationModel presentationModel) {
                        dolphin.apply presentationModel to dataMold
                    }
                } )
            } as ChangeListener )

            // count the number of lazily loaded pms by listing to the model store
            int count = 0
            dolphin.addModelStoreListener("LAZY") { ModelStoreEvent evt ->
                if (evt.type == ModelStoreEvent.Type.ADDED) lazilyLoadedField.text = ++count
            }

            // when starting, first fill the table with pm ids
            dolphin.data "fullDataRequest", { data ->
                for (map in data) {
                    observableList << map
                }
                tableSizeField.text = observableList.size()
            }

            primaryStage.show()
        }
    }
}

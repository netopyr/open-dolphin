/*
 * Copyright 2012-2013 Canoo Engineering AG.
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

package org.opendolphin.demo

import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.WithPresentationModelHandler

import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.blueStyle
import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.MasterDetailConstants.*

/**
 * A demo that shows how to easily create a master-detail view with the standard Dolphin on-board means.
 */

class MasterDetailView {

    static show(ClientDolphin dolphin) {

        ClientPresentationModel dataMold = dolphin.presentationModel('dataMold', [ATT_RANK, ATT_NAME])

        ObservableList<Integer> observableList = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate

            stage title:"Dolphin master-detail demo", {
                scene width: 750, height: 500, {
                    borderPane {
                        center margin:10, {
                            gridPane hgap:10, vgap:12, padding: 20, {
                                columnConstraints  halignment: "right"
                                columnConstraints  halignment: "left"
                                label     row: 0, column: 0, 'Rank'
                                textField row: 0, column: 1, id: 'rankField', prefColumnCount:10
                                label     row: 1, column: 0, 'Name'
                                textField row: 1, column: 1, id: 'nameField', prefColumnCount:10
                            }
                        }
                        left margin:10, {
                            tableView(id: 'table') {
                                value ATT_RANK, tableColumn(property:'rank', text:"Rank", prefWidth: 50 )
                                value ATT_NAME, tableColumn(property:'name', text:"Name", prefWidth: 250 )
                            }
                        }
            }   }   }
            blueStyle sgb

            table.items = observableList

            // all the bindings ...
            bind ATT_RANK of dataMold to FX.TEXT of rankField
            bind ATT_NAME of dataMold to FX.TEXT of nameField
            bind FX.TEXT of nameField to ATT_NAME of dataMold

            // when a table row is selected, we fill the mold and the detail view gets updated
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
                dolphin.clientModelStore.withPresentationModel(selectedPm.id.toString(), new WithPresentationModelHandler() {
                    void onFinished(ClientPresentationModel presentationModel) {
                        dolphin.apply presentationModel to dataMold
                    }
                } )
            } as ChangeListener )


            dolphin.send CMD_PULL, { pms ->
                for (pm in pms) {
                    observableList << pm
                }
            }

            primaryStage.show()
        }
    }
}

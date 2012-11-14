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
import com.canoo.dolphin.core.ModelStoreListener
import com.canoo.dolphin.core.client.ClientAttributeWrapper
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.util.Callback

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static groovyx.javafx.GroovyFX.start

class CrudView {

    static show(ClientDolphin clientDolphin) {

        def selectedPortfolio = clientDolphin.presentationModel(
                'selectedPortfolio',
                 domainId:1, name:'Portfolio One', total:"n/a", fixed:false)

        ObservableList<ClientPresentationModel> observableListOfPositions = FXCollections.observableArrayList()

        start { app ->
            stage {
                scene width: 500, height: 300, {
                    tabPane {
                        tab id:'tab', {
                            gridPane hgap:10, vgap:12, padding: 20, {
                                label       "Portfolio",    row: 0, column: 0
                                textField   id:'nameField', row: 0, column: 1
                                label       "Positions",    row: 1, column: 0
                                vbox        id:'tableBox',  row: 1, column: 1, {
                                    tableView id:'positions', selectionMode:"single", editable:true, {
                                        instrumentCol = tableColumn(text: 'Instrument',   prefWidth: 100, editable:true )
                                        weightCol     = tableColumn(text: 'Weight',       prefWidth:  60, editable:true,
                                            onEditCommit: { event ->
                                                def positionPm = event.tableView.items.get(event.tablePosition.row)
                                                positionPm.weight.value = event.newValue.toInteger()
                                            })
                                    }
                                    hbox {
                                        button id:'plus', '+'; button id: 'minus', '-'
                                    }
                                }
                                label       'Total',        row: 2, column: 0
                                text        id:'totalField',row: 2, column: 1
                                label       'Fixed',        row: 3, column: 0
                                checkBox    id:'fixedField',row: 3, column: 1
                            }
                        }
                    }
                }
            }
            positions.items = observableListOfPositions

            // auto-update the cell values // todo dk: put behind a convenience method
            instrumentCol.cellValueFactory = { new ClientAttributeWrapper(it.value['instrument']) } as Callback
            weightCol.cellValueFactory     = { new ClientAttributeWrapper(it.value['weight']) }     as Callback

            bind 'name'     of selectedPortfolio to 'text'      of tab

            bind 'selected' of fixedField        to 'fixed'     of selectedPortfolio
            bind 'fixed'    of selectedPortfolio to 'selected'  of fixedField

            bind 'name'     of selectedPortfolio to 'text'      of nameField
            bind 'text'     of nameField         to 'name'      of selectedPortfolio

            bind 'total'    of selectedPortfolio to 'text'      of totalField

            clientDolphin.addModelStoreListener 'Position', { ModelStoreEvent event ->
                if (event.presentationModel.portfolioId.value != selectedPortfolio.domainId.value) return
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        observableListOfPositions << event.presentationModel
                        event.presentationModel.weight.addPropertyChangeListener('value', { clientDolphin.send 'updateTotal' } as PropertyChangeListener)
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        observableListOfPositions.remove event.presentationModel
                        break
                }
            }

            plus.onAction {
                clientDolphin.presentationModel(null, 'Position', instrument:'changeme', weight:10, portfolioId:selectedPortfolio.domainId.value)
                clientDolphin.send 'updateTotal'
            }

            minus.onAction {
                def position = positions.selectionModel.selectedItem
                if (! position) return
                clientDolphin.delete(position)
                positions.selectionModel.clearSelection() // this may become a server decision
                clientDolphin.send 'updateTotal'
            }

            // startup and main loop
            clientDolphin.send 'pullPositions', { positions ->
                clientDolphin.send 'updateTotal'
            }

            primaryStage.show()
        }
    }
}

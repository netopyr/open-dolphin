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
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.PieChart
import javafx.scene.layout.GridPane

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JavaFxUtil.value
import static com.canoo.dolphin.binding.JavaFxUtil.cellEdit
import static groovyx.javafx.GroovyFX.start
import static javafx.scene.layout.GridPane.*

class CrudView {

    static show(ClientDolphin clientDolphin) {

        def selectedPortfolio = clientDolphin.presentationModel(
                'selectedPortfolio',
                 domainId:1, name:'Portfolio One', total:"n/a", fixed:false)

        ObservableList<ClientPresentationModel> observableListOfPositions = FXCollections.observableArrayList()
        ObservableList<ClientPresentationModel> observableListOfPortfolios = FXCollections.observableArrayList()

        start { app ->
            stage {
                scene width: 1000, height: 600, stylesheets:"CrudDemo.css", {
                    splitPane  {
                        dividerPosition(index: 0, position: 0.2)
                        tableView id:'portfolios', selectionMode:"single", {
                            value 'name',  tableColumn('Portfolio', prefWidth: 100 )
                            value 'total', tableColumn('Total',     prefWidth: 40 )
                            value 'fixed', tableColumn('Fixed',     prefWidth: 40 )
                        }
                        tabPane id:'tabs', {
                            tab id:'tab', {
                                gridPane hgap:10, vgap:12, padding: 20, {
                                    label       "Portfolio",    row: 0, column: 0
                                    textField   id:'nameField', row: 0, column: 1, minHeight:32
                                    label       "Positions",    row: 1, column: 0
                                    vbox        id:'tableBox',  row: 1, column: 1, {
                                        tableView id:'positions', selectionMode:"single", editable:true, {
                                            value 'instrument', tableColumn('Instrument', prefWidth: 100, editable:true,
                                                                            onEditCommit: cellEdit('instrument', { it.toString() } ) )
                                            value 'weight'    , tableColumn('Weight',     prefWidth:  60, editable:true,
                                                                            onEditCommit: cellEdit('weight',     { it.toInteger() } ) )
                                        }
                                        hbox {
                                            button id:'plus',  '+'
                                            button id:'minus', '-'
                                        }
                                    }
                                    pieChart(id:'chart', row:0, column:2, rowSpan:REMAINING, animated: true)
                                    label       'Total',        row: 2, column: 0
                                    text        id:'totalField',row: 2, column: 1
                                    label       'Fixed',        row: 3, column: 0
                                    checkBox    id:'fixedField',row: 3, column: 1
                                }
                            }
                        }
                    }
                }
            }
            positions.items  = observableListOfPositions
            portfolios.items = observableListOfPortfolios
            ObservableList<PieChart.Data> chartData = chart.data

            bind 'name'     of selectedPortfolio to 'text'      of tab

            bind 'selected' of fixedField        to 'fixed'     of selectedPortfolio
            bind 'fixed'    of selectedPortfolio to 'selected'  of fixedField

            bind 'name'     of selectedPortfolio to 'text'      of nameField
            bind 'text'     of nameField         to 'name'      of selectedPortfolio

            bind 'total'    of selectedPortfolio to 'text'      of totalField

            clientDolphin.addModelStoreListener 'Position', { ModelStoreEvent event ->
                PresentationModel pm = event.presentationModel
                if (pm.portfolioId.value != selectedPortfolio.domainId.value) return
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        observableListOfPositions << pm
                        pm.weight.addPropertyChangeListener('value', { clientDolphin.send 'updateTotal' } as PropertyChangeListener)
                        def pieDataPoint = new PieChart.Data("",0)
                        bind 'instrument' of pm to 'name'     of pieDataPoint
                        bind 'weight'     of pm to 'pieValue' of pieDataPoint, { it.toDouble() }
                        chartData << pieDataPoint
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        observableListOfPositions.remove pm
                        def dataPoint = chartData.find { it.name == pm.instrument.value }
                        chartData.remove dataPoint
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

            clientDolphin.send 'pullPortfolios', { portfolios ->
                for (pm in portfolios) {
                    observableListOfPortfolios << pm
                }
                fadeTransition(1.s, node: portfolios, to: 1).playFromStart()
            }

            primaryStage.show()
        }
    }
}

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

package org.opendolphin.demo
import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.WithPresentationModelHandler
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Callback

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start

import jfxtras.labs.scene.control.gauge.*
import static jfxtras.labs.scene.control.gauge.Gauge.FrameDesign.CHROME
import static jfxtras.labs.scene.control.gauge.Gauge.LcdFont.LCD;

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

            def lcd = new Lcd (
                 styleModel: new StyleModel(lcdValueFont: LCD, lcdUnitVisible: true),
                 title: "Selected Value" ,
                 unit: "value",
                 prefWidth: 250,
                 prefHeight: 70
            )

            def gauge = new Radial (
                styleModel: new StyleModel(frameDesign: CHROME),
                title: "Real Load %",
                lcdDecimals : 3,
                prefWidth:  250,
                prefHeight: 250,
                effect: dropShadow(radius: 20, color:rgba(0,0,0,0.4))
            )

            stage title:"Dolphin lazy loading demo", {
                scene width: 700, height: 500, {
                    borderPane {
                        center margin:10, {
                            gridPane hgap:10, vgap:12, padding: 20, {
                                columnConstraints  halignment: "right"
                                columnConstraints  halignment: "right"
                                label     row: 0, column: 0, 'detail'
                                textField row: 0, column: 1, id: 'detailsField', prefColumnCount:10
                                node      row: 1, column: 1, lcd
                                label     row: 2, column: 0, 'table size:'
                                label     row: 2, column: 1, id:'tableSizeField'
                                label     row: 3, column: 0, 'lazily loaded:'
                                label     row: 3, column: 1, id:"lazilyLoadedField"
                                node      row: 4, column: 1, gauge
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
            bind 'detail' of dataMold to FX.TEXT  of detailsField
            bind 'detail' of dataMold to FX.VALUE of lcd, { it ? (it-"server: ").toDouble() : 0 }

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
                if (evt.type == ModelStoreEvent.Type.ADDED) {
                    lazilyLoadedField.text = ++count
                    gauge.value = 100d * count / observableList.size()
                }
            }

            // when starting, first fill the table with pm ids
            dolphin.data "fullDataRequest", { data ->
                for (map in data) {
                    observableList << map
                }
                tableSizeField.text = observableList.size()
                lcd.maxValue = observableList.size()
            }

            primaryStage.show()
        }
    }
}

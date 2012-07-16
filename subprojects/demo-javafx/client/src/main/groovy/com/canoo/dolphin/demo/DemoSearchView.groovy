package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.comm.NamedCommand
import groovyx.javafx.SceneGraphBuilder

import javafx.collections.FXCollections

import javafx.collections.ObservableList
import javafx.event.EventHandler

import javafx.util.Callback

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.blueStyle

import static groovyx.javafx.GroovyFX.start
import javafx.beans.value.ChangeListener
import static com.canoo.dolphin.demo.DemoSearchProperties.*

class DemoSearchView {

    static show() {

        def communicator = InMemoryClientConnector.instance

        def searchCriteria = new ClientPresentationModel(
                SEARCH_CRITERIA,
                [FIRST,SECOND,NAME].collect { new ClientAttribute(it) }
        )

        ObservableList<ClientPresentationModel> observableListOfKoPms = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 500, height: 500, {
                    borderPane {
                        top margin:10, {
                            gridPane vgap: 5, hgap:5, {
                                label       'One first choice', row:0, column:0
                                choiceBox   id:'gvf',           row:0, column:1, items: FXCollections.observableArrayList(), opacity: 0.2
                                label       'Another one',      row:1, column:0
                                choiceBox   id:'dst',           row:1, column:1, items: FXCollections.observableArrayList(), opacity: 0.2
                                label       'Name',             row:2, column:0
                                textField   id:'bez',           row:2, column:1
                                button      'Search', id:'search', row:3, column:1
                            }

                        }
                        center margin:10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property:'id', text:"Contact", prefWidth: 100 )
                                koNameCol = tableColumn(text:'Name', prefWidth: 100)
                                dateCol   = tableColumn(text:'Date', prefWidth: 250)
                            }
                        }
            }   }   }

            table.items = observableListOfKoPms

            koNameCol.cellValueFactory = { return it.getValue()[CONTACT_NAME].valueProperty() } as Callback
            dateCol.cellValueFactory   = { return it.getValue()[CONTACT_DATE].valueProperty() } as Callback

            communicator.send(new NamedCommand(id: FIRST_FILL_CMD)) { pmIds ->
                for (id in pmIds) {
                    gvf.items << communicator.clientModelStore.findPresentationModelById(id)[TEXT].value
                }
                gvf.selectionModel.selectedIndex = 0
                fadeTransition(1.s, node: gvf, to: 1).playFromStart()
            }

            communicator.send(new NamedCommand(id: SECOND_FILL_CMD)) { pmIds ->
                for (id in pmIds) {
                    dst.items << communicator.clientModelStore.findPresentationModelById(id)[TEXT].value
                }
                dst.selectionModel.selectedIndex = 0
                fadeTransition(1.s, node: dst, to: 1).playFromStart()
            }

            // listeners
            search.onAction = {
                search.disabled = true
                searchCriteria[NAME].value = bez.text
                table.opacity = 0.2
                observableListOfKoPms.clear()
                communicator.send(new NamedCommand(id: SEARCH_CMD)) { pmIds ->
                    for (id in pmIds) {
                        observableListOfKoPms << communicator.clientModelStore.findPresentationModelById(id)
                    }
                    search.disabled = false
                    fadeTransition(0.5.s, node: table, to: 1).playFromStart()
                }
            } as EventHandler

            blueStyle sgb

            // all the bindings ...

            bind FIRST of searchCriteria to 'value' of gvf
            gvf.selectionModel.selectedItemProperty().addListener( { o, oldVal, newVal ->
                searchCriteria[FIRST].value = newVal
            } as ChangeListener)

            bind SECOND of searchCriteria to 'value' of dst
            dst.selectionModel.selectedItemProperty().addListener( { o, oldVal, newVal ->
                searchCriteria[SECOND].value = newVal
            } as ChangeListener)

            bind NAME of searchCriteria to 'text' of bez

            primaryStage.show()
        }
    }
}

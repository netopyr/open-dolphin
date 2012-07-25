package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttributeWrapper
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.Dolphin
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.comm.NamedCommand
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.shape.Rectangle
import javafx.util.Callback
import javafx.util.StringConverter

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static com.canoo.dolphin.demo.VehicleProperties.*
import static groovyx.javafx.GroovyFX.start

class DependentChoiceBoxView {

    static show() {

        def selectedFirst = ClientPresentationModel.make('selectedFirst', ['value'])

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 200, height: 200, {
                    gridPane padding:5, vgap: 5, hgap: 5, {
                        label "First", row:0, column:0
                        choiceBox id:'first'  ,row:0, column:1, items: [], {
                            onSelect { control, item -> selectedFirst.syncWith(item.pm) }
                        }
                        label "Second", row:1, column:0
                        choiceBox id:'second' ,row:1, column:1, items: []
            }   }   }

            blueStyle(sgb)

            Dolphin.clientConnector.send(new NamedCommand("fillFirst"), { pms ->
                sgb.first.items.clear()
                pms.each {
                    sgb.first.items.add new PmWrapper(pm: it, displayProperty: 'value')
                }
            } as OnFinishedHandler)

            Dolphin.clientConnector.send(new NamedCommand("fillRelation"))

            selectedFirst.findAttributeByPropertyName('value').addPropertyChangeListener({evt->
                def evenOdd = evt.source.value
                def relations = Dolphin.getClientModelStore().findAllPresentationModelsByType("FirstSecondRelation")

                def matches = relations.findAll { it.findAttributeByPropertyName("first").value == evenOdd }

                sgb.second.items.clear()
                matches.each {
                    sgb.second.items.add new PmWrapper(pm: it, displayProperty: 'second')
                }

            } as PropertyChangeListener)

            primaryStage.show()
        }
    }
}

class PmWrapper {
    ClientPresentationModel pm
    String displayProperty
    String toString() { pm.findAttributeByPropertyName(displayProperty).value }
}
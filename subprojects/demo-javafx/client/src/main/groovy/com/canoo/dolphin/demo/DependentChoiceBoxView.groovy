package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import groovyx.javafx.SceneGraphBuilder

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start

/**
 * This demos shows how to use dolphin when one selection (choicebox) determines the available options of
 * a second selection (choicebox). In order to allow setting the options of the second selection
 * without waiting for a server roundtrip, the full information about selection dependencies must
 * be available on the server side.
 * To this end, we use a presentation model that captures the relation between the two selections,
 * much like a relation table in a relational datastore.
 */

class DependentChoiceBoxView {

    static show(ClientDolphin dolphin) {

        def selectedFirst = dolphin.presentationModel 'selectedFirst', value:null

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

            dolphin.send "fillFirst", { pms ->
                sgb.first.items.clear()
                pms.each {
                    sgb.first.items.add new PmWrapper(pm: it, displayProperty: 'value')
                }
            }

            dolphin.send "fillRelation"

            selectedFirst.value.addPropertyChangeListener({evt->
                def evenOdd = evt.source.value
                def relations = dolphin.findAllPresentationModelsByType("FirstSecondRelation")

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
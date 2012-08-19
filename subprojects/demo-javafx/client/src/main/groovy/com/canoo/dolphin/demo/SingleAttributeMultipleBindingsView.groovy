package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import groovyx.javafx.SceneGraphBuilder
import javafx.event.EventHandler

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.TITLE
import static com.canoo.dolphin.demo.MyProps.TEXT
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.RIGHT

class SingleAttributeMultipleBindingsView {
    void show(ClientDolphin clientDolphin) {
        start { app ->
            SceneGraphBuilder builder = delegate
            layoutFrame builder
            style builder

            def pm = createPresentationModel()
            clientDolphin.clientModelStore.add pm
            bindPmToViews pm, builder
            attachHandlers pm, builder

            primaryStage.show() // must come last or css shrinks textfield height
        }
    }

    def layoutFrame(SceneGraphBuilder sgb) {
        sgb.stage {
            scene {
                gridPane {
                    label id: 'header', row: 0, column: 1
                    label id: 'label', row: 1, column: 0
                    textField id: 'input', row: 1, column: 1
                    button id: 'submit', row: 3, column: 1, halignment: RIGHT,
                            "Update labels and title"
                }
            }
        }
    }

    ClientPresentationModel createPresentationModel() {
        def titleAttr = new ClientAttribute(TITLE)
        titleAttr.value = "Some Text: <enter> or <submit>"
        return new ClientPresentationModel('demo', [titleAttr])
    }

    void bindPmToViews(ClientPresentationModel pm, SceneGraphBuilder sgb) {
        sgb.with {
            bind TITLE of pm to TITLE of primaryStage // groovy style

            bind(TITLE).of(pm).to(TEXT).of(label)       // java fluent-interface style

            bind TITLE of pm to TEXT of input

            // auto-update the header with every keystroke
            bind TEXT of input to TEXT of header

            // the below is an alternative that updates the pm with every keystroke and thus all bound listeners
            // bind TEXT of input to TITLE of pm
        }
    }

    void attachHandlers(ClientPresentationModel pm, SceneGraphBuilder sgb) {
        sgb.updatePm = { pm.title.value = sgb.input.text } as EventHandler
        sgb.input.onAction = sgb.updatePm
        sgb.submit.onAction = sgb.updatePm
    }
}
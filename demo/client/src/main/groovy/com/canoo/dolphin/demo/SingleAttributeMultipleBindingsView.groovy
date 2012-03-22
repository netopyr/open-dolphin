package com.canoo.dolphin.demo

import static com.canoo.dolphin.demo.MyProps.*
import static com.canoo.dolphin.binding.JFXBinder.bind
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.*
import javafx.event.EventHandler
import groovyx.javafx.SceneGraphBuilder
import static com.canoo.dolphin.demo.DemoStyle.style

class SingleAttributeMultipleBindingsView {
    void show() {
        start { app ->
            SceneGraphBuilder builder = delegate
            layoutFrame builder
            style       builder

            def pm = createPresentationModel()
            bindPmToViews  pm, builder
            attachHandlers pm, builder

            primaryStage.show() // must come last or css shrinks textfield height
        }
    }

    def layoutFrame( SceneGraphBuilder sgb) {
        sgb.stage {
            scene {
                gridPane {
                    label       id: 'header', row: 0, column: 1
                    label       id: 'label',  row: 1, column: 0
                    textField   id: 'input',  row: 1, column: 1
                    button      id: 'submit', row: 3, column: 1, halignment: RIGHT,
                                "Update labels and title"
    }   }   }   }

    ClientPresentationModel createPresentationModel() {
        def titleAttr = new ClientAttribute('title')
        titleAttr.value = "Some Text: <enter> or <submit>"
        return new ClientPresentationModel('demo', [titleAttr])
    }

    void bindPmToViews(ClientPresentationModel pm, SceneGraphBuilder sgb) {
        sgb.with {
            bind TITLE  of pm  to TITLE of primaryStage    // groovy style
            bind TITLE  of pm  to TEXT  of label
            bind(TITLE).of(pm).to(TEXT).of(input)   // java style
            bind TEXT of label to TEXT  of header   // bind javafx implementation-wise (regression test)
        }
    }

    void attachHandlers(ClientPresentationModel pm, SceneGraphBuilder sgb) {
        sgb.updatePm = { pm.title.value = sgb.input.text } as EventHandler
        sgb.input.onAction  = sgb.updatePm
        sgb.submit.onAction = sgb.updatePm
    }
}
package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.comm.NamedCommand

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.CENTER

class MultipleAttributeSwitchView {

    static show(ClientDolphin dolphin) {

        def communicator = dolphin.clientConnector

        start { app ->

            def pm1 = makePm 'pm1', 'First PM',  "Show a first pm", dolphin
            def pm2 = makePm 'pm2', 'Second PM', "Show a second pm", dolphin

            def actualPm = makePm 'pm', 'actualPm', null, dolphin
            actualPm.syncWith pm1

            stage {
                scene {
                    gridPane {

                        label id: 'header', row:0, column:0, halignment: CENTER, columnSpan: 2

                        label 'Title',          row: 1, column: 0
                        label id: 'titleLabel', row: 1, column: 1

                        label 'Purpose',          row: 2, column: 0
                        label id: 'purposeLabel', row: 2, column: 1

                        hbox styleClass:"submit", row:3, column:1, {
                            button "Actual is one",
                                   onAction: { dolphin.apply pm1 to actualPm }
                            button "Actual is two",
                                   onAction: { dolphin.apply pm2 to actualPm }
                        }
                        hbox styleClass:"submit", row:4, column:1, {
                            button "Set title",
                                   onAction: { dolphin.send "setTitle" }
                            button "Set purpose",
                                   onAction: { dolphin.send "setPurpose" }
            }   }   }   }

            style delegate

            bind TITLE       of actualPm to TITLE of primaryStage
            bind TITLE       of actualPm to TEXT  of header
            bind TITLE       of actualPm to TEXT  of titleLabel
            bind ATT_PURPOSE of actualPm to TEXT  of purposeLabel

            primaryStage.show()
        }
    }

    protected static ClientPresentationModel makePm(String idPrefix, String id, String purpose, ClientDolphin clientDolphin) {
        def attributes = [TITLE, ATT_PURPOSE].collect { propName ->
            def attr = new ClientAttribute(propName)
            attr.qualifier = idPrefix + '.' + propName
            attr
        }
        def pm = new ClientPresentationModel(id, attributes)
        pm.title.value   = id
        pm.purpose.value = purpose
        clientDolphin.clientModelStore.add pm
        pm
    }
}
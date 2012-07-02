package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.style

import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.CENTER
import static com.canoo.dolphin.demo.MyProps.TITLE
import static com.canoo.dolphin.demo.MyProps.TEXT
import static com.canoo.dolphin.demo.MyProps.PURPOSE
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

class MultipleAttributeSwitchView {

    static show() {

        def communicator = InMemoryClientConnector.instance

        start { app ->

            def pm1 = makePm 'First PM',  "Show a first pm"
            def pm2 = makePm 'Second PM', "Show a second pm"

            def actualPm = makePm 'actualPm', null
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
                                   onAction: { communicator.switchPmAndSend(actualPm, pm1) }
                            button "Actual is two",
                                   onAction: { communicator.switchPmAndSend(actualPm, pm2) }
                        }
                        hbox styleClass:"submit", row:4, column:1, {
                            button "Set title",
                                   onAction: { communicator.send(new NamedCommand(id: "setTitle")) }
                            button "Set purpose",
                                   onAction: { communicator.send(new NamedCommand(id: "setPurpose")) }
            }   }   }   }

            style delegate

            bind TITLE   of actualPm to TITLE of primaryStage
            bind TITLE   of actualPm to TEXT  of header
            bind TITLE   of actualPm to TEXT  of titleLabel
            bind PURPOSE of actualPm to TEXT  of purposeLabel

            primaryStage.show()
        }
    }

    protected static ClientPresentationModel makePm(String id, String purpose) {
        def pm = new ClientPresentationModel(id, [TITLE, PURPOSE].collect { new ClientAttribute(it) })
        pm.title.value   = id
        pm.purpose.value = purpose
        pm
    }
}
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

LogConfig.logCommunication()

start { app ->

    def bean1 = new DemoBean(title: "Bean one", purpose: "Show a first bean")
    def bean2 = new DemoBean(title: "Bean two", purpose: "Show a second bean")

    // construct the PMs
    def actualPm = new ClientPresentationModel([TITLE,PURPOSE].collect{new ClientAttribute(DemoBean, it)})

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
                           onAction: { actualPm.applyBean bean1 }
                    button "Actual is two",
                           onAction: { actualPm.applyBean bean2 }
                }
            }
        }
    }
    style delegate
    
    bind TITLE of actualPm to TITLE of primaryStage
    bind TITLE of actualPm to TEXT of header
    bind TITLE of actualPm to TEXT of titleLabel
    bind PURPOSE of actualPm to TEXT of purposeLabel

    primaryStage.show()
}

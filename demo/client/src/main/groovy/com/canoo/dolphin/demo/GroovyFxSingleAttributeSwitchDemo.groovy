package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.*
import static com.canoo.dolphin.demo.DemoStyle.style
import com.canoo.dolphin.core.client.ClientPresentationModel

LogConfig.logCommunication()

start { app ->

    def bean1 = new DemoBean(title: "Bean one")
    def bean2 = new DemoBean(title: "Bean two")

    // construct the PMs
    def titleAttr1 = new ClientAttribute(DemoBean, 'title')
    titleAttr1.bean = bean1

    def titleAttr2 = new ClientAttribute(DemoBean, 'title')
    titleAttr2.bean = bean2

    def actualTitleAttr = new ClientAttribute(DemoBean, 'title')
    // no bean set, value remains null
    def actualPm = new ClientPresentationModel([actualTitleAttr])

    stage {
        scene {
            gridPane {

                label id: 'header', row:0, column:0, halignment: CENTER

                hbox styleClass:"submit", row:1, column:0, {
                    button "Actual is one",
                           onAction: { actualTitleAttr.bean = bean1 }
                    button "Actual is two",
                           onAction: { actualTitleAttr.bean = bean2 }
                }
            }
        }
    }
    style delegate
    
    bind TITLE of actualPm to TITLE of primaryStage
    bind TITLE of actualPm to TEXT of header

    primaryStage.show()
}

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.*
import static com.canoo.dolphin.demo.DemoStyle.style
import com.canoo.dolphin.core.client.ClientPresentationModel

Startup.bootstrap()

fakeServerUpdate = { pm, bean, evt ->
    for(attr in pm.attributes) {
        if(null == bean) {
            attr.value = null
        } else {
            attr.value = bean[attr.propertyName]
        }
    }
}

start { app ->

    def bean1 = new DemoBean(title: "Bean one")
    def bean2 = new DemoBean(title: "Bean two")

    // construct the PMs
    def titleAttr1 = new ClientAttribute(TITLE)
    titleAttr1.value = bean1.title

    def titleAttr2 = new ClientAttribute(TITLE)
    titleAttr2.value = bean2.title

    def actualTitleAttr = new ClientAttribute(TITLE)
    // no bean set, value remains null
    def actualPm = new ClientPresentationModel('actualPm', [actualTitleAttr])

    stage {
        scene {
            gridPane {

                label id: 'header', row:0, column:0, halignment: CENTER

                hbox styleClass:"submit", row:1, column:0, {
                    button "Actual is one",
                           onAction: fakeServerUpdate.curry(actualPm, bean1)
                    button "Actual is two",
                           onAction: fakeServerUpdate.curry(actualPm, bean2)
                }
            }
        }
    }
    style delegate
    
    bind TITLE of actualPm to TITLE of primaryStage
    bind TITLE of actualPm to TEXT of header

    primaryStage.show()
}

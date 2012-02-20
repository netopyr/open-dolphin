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

LogConfig.logCommunication()

start { app ->
    SceneGraphBuilder builder = delegate
    layoutFrame builder
    style       builder

    def data = createData()
    bindPmToViews  data.pm, builder
    attachHandlers data,    builder

    primaryStage.show() // must come last or css shrinks textfield height
}

def layoutFrame(SceneGraphBuilder sgb) {
    sgb.stage {
        scene {
            gridPane {
                label       id: 'header', row: 0, column: 1
                label       id: 'label',  row: 1, column: 0
                textField   id: 'input',  row: 1, column: 1
                button      id: 'submit', row: 3, column: 1, halignment: RIGHT,
                            "Update labels and title"
}   }   }   }

Map createData() {
    def titleAttr = new ClientAttribute(DemoBean, 'title')
    titleAttr.bean = new DemoBean(title: "Some Text: <enter> or <submit>")
    def pm = new ClientPresentationModel([titleAttr])
    [title: titleAttr, pm: pm]
}

void bindPmToViews(ClientPresentationModel pm, SceneGraphBuilder sgb) {
    sgb.with {
        bind TITLE  of pm  to TITLE of primaryStage    // groovy style
        bind TITLE  of pm  to TEXT  of label
        bind(TITLE).of(pm).to(TEXT).of(input)   // java style
        bind TEXT of label to TEXT  of header   // bind javafx implementation-wise (regression test)
    }
}

void attachHandlers(Map data, SceneGraphBuilder sgb) {
    updatePm = { data.title.value = sgb.input.text } as EventHandler
    sgb.input.onAction  = updatePm
    sgb.submit.onAction = updatePm
}
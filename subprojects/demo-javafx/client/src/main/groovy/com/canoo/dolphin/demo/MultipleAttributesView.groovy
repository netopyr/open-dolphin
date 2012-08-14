package com.canoo.dolphin.demo

import com.canoo.dolphin.binding.Binder
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.RIGHT

class MultipleAttributesView {

    static show(ClientDolphin clientDolphin) {

        start { app ->
            // construct the PM
            def titleAttr = new ClientAttribute(TITLE)
            titleAttr.value = "A PM with multiple attributes"
            def purposeAttr = new ClientAttribute(PURPOSE)
            purposeAttr.value = "Show the need for PMs"
            def pm = new ClientPresentationModel('demo',[titleAttr, purposeAttr])
            clientDolphin.clientModelStore.add pm

            def updateTitle   = { pm.title.value = titleInput.text }
            def updatePurpose = { pm.purpose.value = purposeInput.text }

            stage {
                scene {
                    gridPane  {

                        label id: 'header', row: 0, column: 1,
                              'A composite presentation model'

                        label       'Title: ',        row: 1, column: 0
                        label       id: 'titleLabel', row: 1, column: 1
                        textField   id: 'titleInput', row: 2, column: 1,
                              onAction: updateTitle, onKeyReleased: updateTitle

                        label       'Purpose: ',        row: 3, column: 0
                        label       id: 'purposeLabel', row: 3, column: 1
                        textField   id: 'purposeInput', row: 4, column: 1,
                              onAction: updatePurpose

                        button "Update labels", row: 5, column: 1,
                              halignment: RIGHT,
                              onAction: {
                                  updateTitle()
                                  updatePurpose()
                              }
            }   }   }

            style delegate

            bind TITLE of pm to TEXT of titleLabel
            Binder.bind TITLE of pm to TEXT of titleInput

            bind PURPOSE of pm to TEXT of purposeLabel
            Binder.bind PURPOSE of pm to TEXT of purposeInput

            primaryStage.show()
        }
    }
}

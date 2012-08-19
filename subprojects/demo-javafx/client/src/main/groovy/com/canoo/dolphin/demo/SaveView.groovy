package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientDolphin

import static javafx.scene.paint.Color.*
import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JFXBinder.bindInfo
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start

import static com.canoo.dolphin.core.Attribute.DIRTY_PROPERTY

// todo dk: we should discuss the design around the save use case

class SaveView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def model = dolphin.presentationModel 'person', (ATT_NAME):'', (ATT_LASTNAME):'Smith'

            stage {
                scene {
                    gridPane {

                        label id: 'header', row: 0, column: 1,
                                'Person Form'

                        label id: 'nameLabel', 'Name: ', row: 1, column: 0
                        textField id: 'nameInput', row: 1, column: 1

                        label id: 'lastnameLabel', 'Lastname: ', row: 2, column: 0
                        textField id: 'lastnameInput', row: 2, column: 1

                        button id: 'saveButton', 'Save', row: 3, column: 1,
                                onAction: { dolphin.clientModelStore.save(model) }
                    }
                }
            }

            style delegate

            bind ATT_NAME     of model         to TEXT         of nameInput
            bind ATT_LASTNAME of model         to TEXT         of lastnameInput
            bind TEXT         of nameInput     to ATT_NAME     of model
            bind TEXT         of lastnameInput to ATT_LASTNAME of model

            bindInfo DIRTY_PROPERTY of model[ATT_NAME]     to TEXT_FILL  of nameLabel,     { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of model[ATT_LASTNAME] to TEXT_FILL  of lastnameLabel, { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of model               to TITLE      of primaryStage , { it ? '** Unsaved **': '' }
            bindInfo DIRTY_PROPERTY of model               to DISABLED   of saveButton,    { !it }

            primaryStage.show()
        }
    }
}

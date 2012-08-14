package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import javafx.scene.paint.Color

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JFXBinder.bindInfo
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start

import static com.canoo.dolphin.core.Attribute.DIRTY_PROPERTY

class DirtyAttributeFlagView {
    static show(ClientDolphin clientDolphin) {
        start { app ->
            def model = createPresentationModel(clientDolphin)

            stage {
                scene {
                    gridPane {

                        label id: 'header', row: 0, column: 1,
                                'Person Form'

                        label id: 'nameLabel', 'Name: ', row: 1, column: 0
                        textField id: 'nameInput', row: 1, column: 1

                        label id: 'lastnameLabel', 'Lastname: ', row: 2, column: 0
                        textField id: 'lastnameInput', row: 2, column: 1

                        button id: 'saveButton', 'Save', row: 3, column: 1
                    }
                }
            }

            style delegate

            bind NAME     of model         to TEXT     of nameInput
            bind LASTNAME of model         to TEXT     of lastnameInput
            bind TEXT     of nameInput     to NAME     of model
            bind TEXT     of lastnameInput to LASTNAME of model

            bindInfo DIRTY_PROPERTY of model[NAME]     to TEXT_FILL  of nameLabel,     { it ? Color.RED: Color.WHITE }
            bindInfo DIRTY_PROPERTY of model[LASTNAME] to TEXT_FILL  of lastnameLabel, { it ? Color.RED: Color.WHITE }
            bindInfo DIRTY_PROPERTY of model           to TITLE      of primaryStage , { it ? '** DIRTY **': '' }
            bindInfo DIRTY_PROPERTY of model           to DISABLED   of saveButton,    { !it }

            primaryStage.show()
        }
    }

    private static ClientPresentationModel createPresentationModel(ClientDolphin clientDolphin) {
        def nameAttribute = new ClientAttribute(NAME, '')
        def lastnameAttribute = new ClientAttribute(LASTNAME, 'Smith')
        def model = new ClientPresentationModel('person', [nameAttribute, lastnameAttribute])
        clientDolphin.clientModelStore.add model
        model
    }
}

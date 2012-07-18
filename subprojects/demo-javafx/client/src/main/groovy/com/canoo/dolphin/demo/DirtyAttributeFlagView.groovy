package com.canoo.dolphin.demo

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.Dolphin
import javafx.scene.paint.Color

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start

class DirtyAttributeFlagView {
    static show() {
        start { app ->
            def model = createPresentationModel()

            stage {
                scene {
                    gridPane {

                        label id: 'header', row: 0, column: 1,
                                'Person Form'

                        label id: 'nameLabel', 'Name: ', row: 1, column: 0
                        textField id: 'nameInput', row: 1, column: 1

                        label id: 'lastnameLabel', 'Lastname: ', row: 2, column: 0
                        textField id: 'lastnameInput', row: 2, column: 1
                    }
                }
            }

            style delegate

            bind NAME of model to TEXT of nameInput
            bind LASTNAME of model to TEXT of lastnameInput
            bind TEXT of nameInput to NAME of model
            bind TEXT of lastnameInput to LASTNAME of model

            def colorSwapper = { target, evt ->
                target.textFill = evt.newValue ? Color.RED : Color.WHITE
            }
            model[NAME].addPropertyChangeListener('dirty', colorSwapper.curry(nameLabel) as PropertyChangeListener)
            model[LASTNAME].addPropertyChangeListener('dirty', colorSwapper.curry(lastnameLabel) as PropertyChangeListener)

            primaryStage.show()
        }
    }

    private static PresentationModel createPresentationModel() {
        def nameAttribute = new ClientAttribute(NAME, '')
        def lastnameAttribute = new ClientAttribute(LASTNAME, 'Smith')
        def model = new ClientPresentationModel('person', [nameAttribute, lastnameAttribute])
        Dolphin.clientModelStore.add model
        model
    }
}

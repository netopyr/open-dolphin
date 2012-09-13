/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientDolphin
import javafx.event.EventHandler

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JFXBinder.bindInfo
import static com.canoo.dolphin.core.Attribute.DIRTY_PROPERTY
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start
import static javafx.scene.paint.Color.*

/**
 * This demos shows how to handle the case that a temporary presentation model needs to synchronize
 * with a persistent one, which is not initially available.
 */

class NewAndSaveView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def person = dolphin.presentationModel 'selectedPerson', (ATT_NAME):'', (ATT_LASTNAME):'', persistent:false
            def form   = dolphin.presentationModel 'personForm', mode:'Create'

            stage {
                scene {
                    gridPane {
                        label id: 'header', row: 0, column: 1

                        label id: 'nameLabel', 'Name: ', row: 1, column: 0
                        textField id: 'nameInput', row: 1, column: 1

                        label id: 'lastnameLabel', 'Lastname: ', row: 2, column: 0
                        textField id: 'lastnameInput', row: 2, column: 1

                        button id: 'saveButton', row: 3, column: 1
                    }
                }
            }

            style delegate

            bind ATT_NAME     of person         to TEXT         of nameInput
            bind ATT_LASTNAME of person         to TEXT         of lastnameInput
            bind TEXT         of nameInput     to ATT_NAME     of person
            bind TEXT         of lastnameInput to ATT_LASTNAME of person

            bind 'mode' of form to TEXT of saveButton
            bind 'mode' of form to TEXT of header, { it == 'Create' ? "New Person" : "Edit Person" }

            bindInfo DIRTY_PROPERTY of person[ATT_NAME]     to TEXT_FILL  of nameLabel,     { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of person[ATT_LASTNAME] to TEXT_FILL  of lastnameLabel, { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of person               to TITLE      of primaryStage , { it ? '** Unsaved **': '' }
            bindInfo DIRTY_PROPERTY of person               to DISABLED   of saveButton,    { !it }

            saveButton.onAction = {
                if (form.mode.value == "Update") {
                    println "do the update handling"
                    return
                }
                dolphin.send "saveNewSelectedPerson", { pms ->
                    if (pms.size() != 1) return // not getting back a new pm means that save was not successful
                    dolphin.apply pms.first() to person
                    form.mode.value = "Update"
                }
            } as EventHandler

            primaryStage.show()
        }
    }
}

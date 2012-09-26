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

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JFXBinder.bindInfo
import static com.canoo.dolphin.core.Attribute.DIRTY_PROPERTY
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.*
import static groovyx.javafx.GroovyFX.start
import static javafx.scene.paint.Color.RED
import static javafx.scene.paint.Color.WHITE

class ResetView {
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

                        button id: 'resetButton', 'Reset', row: 3, column: 1,
                                onAction: { dolphin.clientModelStore.reset(model) }
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
            bindInfo DIRTY_PROPERTY of model               to DISABLED   of resetButton,    { !it }

            primaryStage.show()
        }
    }
}

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
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientDolphin

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.MyProps.ATT.*
import static com.canoo.dolphin.demo.MyProps.CMD.*
import static com.canoo.dolphin.demo.MyProps.PM_ID.*
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.CENTER

class MultipleAttributeSwitchView {

    static show(ClientDolphin dolphin) {

        start { app ->

            def pm1 = dolphin.presentationModel('FirstDemo',
                new ClientAttribute(TITLE,   'First title',  "pm1-title"),
                new ClientAttribute(PURPOSE, 'First purpose',"pm1-purpose")
            )
            def pm2 = dolphin.presentationModel('SecondDemo',
                new ClientAttribute(TITLE,   'Second title',   "pm2-title"),
                new ClientAttribute(PURPOSE, 'Second purpose', "pm2-purpose")
            )

            def mold = dolphin.presentationModel(MOLD, (TITLE):'', (PURPOSE):'')

            dolphin.apply pm1 to mold

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
                                   onAction: { dolphin.apply pm1 to mold }
                            button "Actual is two",
                                   onAction: { dolphin.apply pm2 to mold }
                        }
                        hbox styleClass:"submit", row:4, column:1, {
                            button "Set title",
                                   onAction: { dolphin.send SET_TITLE }
                            button "Set purpose",
                                   onAction: { dolphin.send SET_PURPOSE }
            }   }   }   }

            style delegate

            bind TITLE       of mold to FX.TITLE of primaryStage
            bind TITLE       of mold to FX.TEXT  of header
            bind TITLE       of mold to FX.TEXT  of titleLabel
            bind PURPOSE     of mold to FX.TEXT  of purposeLabel

            primaryStage.show()
        }
    }

}
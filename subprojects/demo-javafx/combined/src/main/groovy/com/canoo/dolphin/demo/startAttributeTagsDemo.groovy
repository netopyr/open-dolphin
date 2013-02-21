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

import com.canoo.dolphin.core.Tag
import static com.canoo.dolphin.demo.MyProps.ATT.*

def config = new JavaFxInMemoryConfig()

// example for setting tags at startup
config.serverDolphin.action "init", { cmd, response ->
    config.serverDolphin.initAt response, 'person', NAME,     null, "First name: ", Tag.LABEL
    config.serverDolphin.initAt response, 'person', LASTNAME, null, "Last name: ",  Tag.LABEL

    config.serverDolphin.initAt response, 'person', NAME, null, ".*a.*",  Tag.REGEX
    config.serverDolphin.initAt response, 'person', NAME, null, "must contain an 'a' ", Tag.TOOLTIP
}

// example for changing tags at runtime
config.serverDolphin.action "german", { cmd, response ->
    def model = config.serverDolphin.getAt('person')
    config.serverDolphin.changeValue response, model.getAt(NAME,     Tag.LABEL), "Vorname: "
    config.serverDolphin.changeValue response, model.getAt(LASTNAME, Tag.LABEL), "Nachname: "
}




new AttributeTagView().show(config.clientDolphin)
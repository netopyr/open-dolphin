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

package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.comm.ActionRegistry

class CreatePresentationModelAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register(CreatePresentationModelCommand) { CreatePresentationModelCommand command, response ->
            List<ServerAttribute> attributes = []
            command.attributes.each { attr ->
                ServerAttribute attribute = new ServerAttribute(attr.propertyName, attr.value)
                attribute.value = attr.value
                attribute.id = attr.id
                attribute.qualifier = attr.qualifier
                attributes << attribute
            }
            PresentationModel model = new ServerPresentationModel(command.pmId, attributes)
            model.presentationModelType = command.pmType
            serverDolphin.serverModelStore.add(model)
        }
    }
}

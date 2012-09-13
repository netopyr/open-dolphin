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

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.PresentationModel

import com.canoo.dolphin.core.comm.InitializeAttributeCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.GetPresentationModelCommand

class PullPresentationModelValuesAction implements ServerAction {
    private final ModelStore modelStore

    PullPresentationModelValuesAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register GetPresentationModelCommand, { GetPresentationModelCommand command, response ->

            PresentationModel pm = modelStore.findPresentationModelById(command.pmId)
            pm.attributes.each {
                response << new InitializeAttributeCommand(pmId: pm.id, propertyName: it.propertyName, newValue: it.value)
            }
        }
    }
}


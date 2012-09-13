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

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.InitialValueChangedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreInitialValueChangeAction implements ServerAction {
    private final ModelStore modelStore

    StoreInitialValueChangeAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(InitialValueChangedCommand) { InitialValueChangedCommand command, response ->
            Attribute attribute = modelStore.findAttributeById(command.attributeId)
            /*
               attribute should exist in the store before this type of command comes
               this can only happen if the server is the sole responsible for creating attributes
            */
            if (attribute) attribute.save()
        }
    }
}

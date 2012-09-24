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
import com.canoo.dolphin.core.comm.AddPresentationModelLinkCommand
import com.canoo.dolphin.core.comm.RemovePresentationModelLinkCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class PresentationModelLinkAction extends DolphinServerAction {
    void registerIn(ActionRegistry registry) {
        registry.register(AddPresentationModelLinkCommand) { AddPresentationModelLinkCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            PresentationModel a = modelStore.findPresentationModelById(command.startId)
            PresentationModel b = modelStore.findPresentationModelById(command.endId)
            modelStore.link(a, b, command.type)
        }
        registry.register(RemovePresentationModelLinkCommand) { RemovePresentationModelLinkCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            PresentationModel a = modelStore.findPresentationModelById(command.startId)
            PresentationModel b = modelStore.findPresentationModelById(command.endId)
            modelStore.unlink(a, b, command.type)
        }
    }
}

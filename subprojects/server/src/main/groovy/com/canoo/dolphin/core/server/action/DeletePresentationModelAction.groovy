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
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.DeletePresentationModelCommand
import com.canoo.dolphin.core.comm.DeletedPresentationModelNotification
import com.canoo.dolphin.core.server.comm.ActionRegistry

class DeletePresentationModelAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register(DeletePresentationModelCommand) { DeletePresentationModelCommand command, response ->
            handleCommand(command)
            response << new DeletedPresentationModelNotification(pmId: model.id)
        }

        registry.register(DeletedPresentationModelNotification) { DeletedPresentationModelNotification command, response ->
            handleCommand(command)
        }
    }

    def void handleCommand(Command command) {
        PresentationModel model = serverDolphin.modelStore.findPresentationModelById(command.pmId)
        // application specific logic could be here (e.g. in a subclass)
        serverDolphin.modelStore.remove(model)
    }
}

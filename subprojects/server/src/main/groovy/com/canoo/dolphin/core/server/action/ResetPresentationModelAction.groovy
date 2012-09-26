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
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.PresentationModelResetedCommand
import com.canoo.dolphin.core.comm.ResetPresentationModelCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class ResetPresentationModelAction implements ServerAction {
    private final ModelStore modelStore

    ResetPresentationModelAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(ResetPresentationModelCommand) { ResetPresentationModelCommand command, response ->
            PresentationModel model = modelStore.findPresentationModelById(command.pmId)
            // todo: trigger application specific persistence
            // todo: deal with potential persistence errors
            response << doWithPresentationModel(model)
        }
    }

    List<Command> doWithPresentationModel(PresentationModel model) {
        [new PresentationModelResetedCommand(pmId: model.id)]
    }
}

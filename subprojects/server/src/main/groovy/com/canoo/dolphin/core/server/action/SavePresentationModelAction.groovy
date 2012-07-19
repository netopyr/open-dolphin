package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.PresentationModelSavedCommand
import com.canoo.dolphin.core.comm.SavePresentationModelCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class SavePresentationModelAction implements ServerAction {
    private final ModelStore modelStore

    SavePresentationModelAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(SavePresentationModelCommand) { SavePresentationModelCommand command, response ->
            PresentationModel model = modelStore.findPresentationModelById(command.pmId)
            // todo: trigger application specific persistence
            // todo: deal with potential persistence errors
            response << doWithPresentationModel(model)
        }
    }

    List<Command> doWithPresentationModel(PresentationModel model) {
        [new PresentationModelSavedCommand(pmId: model.id)]
    }
}

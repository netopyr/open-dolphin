package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.DeletePresentationModelCommand
import com.canoo.dolphin.core.comm.PresentationModelDeletedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class DeletePresentationModelAction extends DolphinServerAction {
    void registerIn(ActionRegistry registry) {
        registry.register(DeletePresentationModelCommand) { DeletePresentationModelCommand command, response ->
            PresentationModel model = serverDolphin.modelStore.findPresentationModelById(command.pmId)
            // todo: trigger application specific persistence
            // todo: deal with potential persistence errors
            response << doWithPresentationModel(model)
        }
    }

    List<Command> doWithPresentationModel(PresentationModel model) {
        [new PresentationModelDeletedCommand(pmId: model.id)]
    }
}

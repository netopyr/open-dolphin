package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.RemovePresentationModelCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class RemovePresentationModelAction extends DolphinServerAction {
    void registerIn(ActionRegistry registry) {
        registry.register(RemovePresentationModelCommand) { RemovePresentationModelCommand command, response ->
            PresentationModel model = serverDolphin.modelStore.findPresentationModelById(command.pmId)
            if (model) serverDolphin.modelStore.remove(model)
        }
    }
}

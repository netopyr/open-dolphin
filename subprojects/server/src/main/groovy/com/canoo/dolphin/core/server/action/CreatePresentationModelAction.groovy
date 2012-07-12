package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.comm.ActionRegistry

class CreatePresentationModelAction implements ServerAction {
    private final ModelStore modelStore

    CreatePresentationModelAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(CreatePresentationModelCommand) { CreatePresentationModelCommand command, response ->
            List<ServerAttribute> attributes = []
            command.attributes.each { attr ->
                ServerAttribute attribute = new ServerAttribute(attr.propertyName)
                attribute.value = attr.value
                attribute.id = attr.id
                attribute.dataId = attr.dataId
                attributes << attribute
            }
            PresentationModel model = new ServerPresentationModel(command.pmId, attributes)
            model.presentationModelType = command.pmType
            modelStore.add(model)
        }
    }
}

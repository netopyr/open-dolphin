package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreAttributeAction implements ServerAction {
    final ModelStore modelStore

    StoreAttributeAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(AttributeCreatedCommand) { AttributeCreatedCommand command, response ->
            def attribute = new ServerAttribute(command.propertyName)
            attribute.id = command.attributeId
            attribute.value = command.newValue
            attribute.dataId = command.dataId
            def pm = modelStore.findPresentationModelById(command.pmId)
            if (null == pm) {
                pm = new ServerPresentationModel(command.pmId, [])
                modelStore.add(pm)
            }
            pm.addAttribute(attribute)
            modelStore.registerAttribute(attribute)
        }
    }
}

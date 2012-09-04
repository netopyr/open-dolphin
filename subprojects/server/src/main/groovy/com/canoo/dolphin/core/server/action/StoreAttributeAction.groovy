package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.comm.ChangeAttributeMetadataCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreAttributeAction extends DolphinServerAction {
    void registerIn(ActionRegistry registry) {
        registry.register(AttributeCreatedCommand) { AttributeCreatedCommand command, response ->
            def attribute = new ServerAttribute(command.propertyName, command.newValue)
            attribute.id = command.attributeId
            attribute.value = command.newValue
            attribute.qualifier = command.qualifier
            def modelStore = serverDolphin.serverModelStore
            def pm = modelStore.findPresentationModelById(command.pmId)
            if (null == pm) {
                pm = new ServerPresentationModel(command.pmId, [])
                modelStore.add(pm)
            }
            pm.addAttribute(attribute)
            modelStore.registerAttribute(attribute)
        }

        registry.register(ChangeAttributeMetadataCommand) { ChangeAttributeMetadataCommand command, response ->
            def attribute = serverDolphin.serverModelStore.findAttributeById(command.attributeId)
            if (!attribute) return
            attribute[command.metadataName] = command.value
        }
    }
}

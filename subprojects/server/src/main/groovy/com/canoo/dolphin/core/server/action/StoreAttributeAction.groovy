package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.server.ServerPresentationModel
import java.util.concurrent.ConcurrentHashMap
import com.canoo.dolphin.core.server.ServerAttribute

@Singleton
class StoreAttributeAction {

    ConcurrentHashMap<String, ServerPresentationModel> modelStore = new ConcurrentHashMap<String, ServerPresentationModel>()

    def registerIn(ActionRegistry registry) {
        registry.register(AttributeCreatedCommand) { AttributeCreatedCommand command, response ->
            def attribute = new ServerAttribute(command.propertyName)
            attribute.id = command.attributeId
            attribute.value = command.newValue
            attribute.dataId = command.dataId
            if (!modelStore.containsKey(command.pmId)) modelStore[command.pmId] = new ServerPresentationModel(command.pmId, [])
            def pm = modelStore[command.pmId]
            pm.attributes << attribute
        }
    }

}

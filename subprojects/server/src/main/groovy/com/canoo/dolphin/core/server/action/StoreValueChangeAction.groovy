package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreValueChangeAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            def attribute = modelStore.findAttributeById(command.attributeId)
            /* todo dk: make more defensive
               attribute should exist in the store before this type of command comes
               this can only happen if the server is the sole responsible for creating attributes
            */
            if (attribute) {
                attribute.value = command.newValue
                def attributes = modelStore.findAllAttributesByQualifier(attribute.qualifier)
                attributes.each { it.value = command.newValue }
            }
        }
    }
}

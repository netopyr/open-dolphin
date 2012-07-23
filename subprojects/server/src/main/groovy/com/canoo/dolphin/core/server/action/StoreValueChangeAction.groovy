package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreValueChangeAction implements ServerAction {
    private final ModelStore modelStore

    StoreValueChangeAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def attribute = modelStore.findAttributeById(command.attributeId)
            /*
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

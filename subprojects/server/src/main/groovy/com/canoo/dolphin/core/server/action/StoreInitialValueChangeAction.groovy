package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.InitialValueChangedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreInitialValueChangeAction implements ServerAction {
    private final ModelStore modelStore

    StoreInitialValueChangeAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(InitialValueChangedCommand) { InitialValueChangedCommand command, response ->
            Attribute attribute = modelStore.findAttributeById(command.attributeId)
            /*
               attribute should exist in the store before this type of command comes
               this can only happen if the server is the sole responsible for creating attributes
            */
            if (attribute) attribute.save()
        }
    }
}

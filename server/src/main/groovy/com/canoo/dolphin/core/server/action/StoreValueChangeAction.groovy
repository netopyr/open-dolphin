package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreValueChangeAction {

    def registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def att = StoreAttributeAction.instance.modelStore.values().attributes.flatten().find { it.id == command.attributeId}
            att.value = command.newValue // no change check here since we have no events on the server side
        }
    }

}

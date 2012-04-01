package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class StoreValueChangeAction {

    def registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def attributes = StoreAttributeAction.instance.modelStore.values().attributes.flatten()
            def atts = attributes.findAll { it.id == command.attributeId}
            atts.each { it.value = command.newValue} // no change check here since we have no events on the server side
        }
    }

}

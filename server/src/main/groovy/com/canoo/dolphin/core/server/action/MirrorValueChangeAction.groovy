package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class MirrorValueChangeAction {

    def registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def attributes = StoreAttributeAction.instance.modelStore.values().attributes.flatten()
            def atts = attributes.findAll { it.id == command.attributeId}
            if (atts.any { it.value != command.newValue } ){
                response << command
            }
        }
    }

}

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.comm.ValueChangedCommand

class CustomAction {

    def impl = { propertyName, NamedCommand command, response ->
        def actual   = StoreAttributeAction.instance.modelStore['actualPm']
        def att = actual[propertyName]

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server" )
    }

    def registerIn(ActionRegistry registry) {
        registry.register 'setTitle',   impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
    }
}

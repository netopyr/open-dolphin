package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.comm.AttributeCreatedCommand

class CustomAction {

    def impl = { propertyName, NamedCommand command, response ->
        def actual   = StoreAttributeAction.instance.modelStore['actualPm']
        def att = actual[propertyName]

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server" )
    }

    def registerIn(ActionRegistry registry) {
        registry.register 'setTitle',   impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
        registry.register 'pullPm',  { NamedCommand command, response ->
            'x y width height'.split().each {
                response << new AttributeCreatedCommand(pmId:'blackRect', propertyName: it)
            }
        }
        registry.register 'pullValues',  { NamedCommand command, response ->
            def rect = StoreAttributeAction.instance.modelStore.blackRect
            int newVal = 300 * Math.random()
            sendVal(response, rect.x.id, newVal)
            sendVal(response, rect.y.id, newVal)
            sendVal(response, rect.width.id, 100)
            sendVal(response, rect.height.id, 100)
        }
    }

    protected void sendVal(response, attrId, int newVal) {
        response << new ValueChangedCommand(attributeId: attrId, oldValue: null, newValue: newVal)
    }
}

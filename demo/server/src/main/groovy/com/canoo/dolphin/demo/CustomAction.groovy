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
        registry.register 'pullPm',  {NamedCommand command, response ->
            response << new AttributeCreatedCommand(pmId:'blackRect', propertyName:'x')
            response << new AttributeCreatedCommand(pmId:'blackRect', propertyName:'y')
            response << new AttributeCreatedCommand(pmId:'blackRect', propertyName:'width')
            response << new AttributeCreatedCommand(pmId:'blackRect', propertyName:'height')
        }
        registry.register 'pullValues',  {NamedCommand command, response ->
            def attrId = StoreAttributeAction.instance.modelStore.blackRect.x.id
            response << new ValueChangedCommand(attributeId: attrId, oldValue: null, newValue: 10)
             attrId = StoreAttributeAction.instance.modelStore.blackRect.y.id
            response << new ValueChangedCommand(attributeId: attrId, oldValue: null, newValue: 10)
             attrId = StoreAttributeAction.instance.modelStore.blackRect.width.id
            response << new ValueChangedCommand(attributeId: attrId, oldValue: null, newValue: 100)
             attrId = StoreAttributeAction.instance.modelStore.blackRect.height.id
            response << new ValueChangedCommand(attributeId: attrId, oldValue: null, newValue: 100)
        }
    }
}

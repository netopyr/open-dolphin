package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.BasePresentationModel
import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.comm.InitializeAttributeCommand

class CustomAction {

    def impl = { propertyName, NamedCommand command, response ->
        def actual   = StoreAttributeAction.instance.modelStore['actualPm']
        def att = actual[propertyName]

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server" )
    }

    def registerIn(ActionRegistry registry) {
        registry.register 'setTitle',   impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
        registry.register 'pullBlackAndBlueRect',  { NamedCommand command, response ->
            def pos = 0
            ['black','blue'].each {
                pos += 100
                response << new InitializeAttributeCommand(pmId:it, propertyName:'x',      newValue:pos)
                response << new InitializeAttributeCommand(pmId:it, propertyName:'y',      newValue:pos)
                response << new InitializeAttributeCommand(pmId:it, propertyName:'width',  newValue:50)
                response << new InitializeAttributeCommand(pmId:it, propertyName:'height', newValue:50)
            }
        }
        registry.register 'randomMove',  { NamedCommand command, response ->
            sleep 1000 // long-polling: server sleeps
            ['black','blue'].each {
                def pm = StoreAttributeAction.instance.modelStore[it]
                response << new ValueChangedCommand(attributeId: pm.x.id, oldValue:pm.x.value , newValue: (pm.x.value+20) % 350 )
                response << new ValueChangedCommand(attributeId: pm.y.id, oldValue:pm.y.value , newValue: (pm.y.value+20) % 350 )
            }
        }
    }
}

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
        def vehicles = ['red','blue','green','orange']
        def rand = { (Math.random() * 350).toInteger() }
        registry.register 'setTitle',   impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
        registry.register 'pullVehicles',  { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeAttributeCommand(pmId:it, propertyName:'x',      newValue:rand())
                response << new InitializeAttributeCommand(pmId:it, propertyName:'y',      newValue:rand())
                response << new InitializeAttributeCommand(pmId:it, propertyName:'width',  newValue:80)
                response << new InitializeAttributeCommand(pmId:it, propertyName:'height', newValue:25)
                response << new InitializeAttributeCommand(pmId:it, propertyName:'rotate', newValue:rand()%180)
            }
        }
        registry.register 'longPoll',  { NamedCommand command, response ->
            sleep 1000 // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = StoreAttributeAction.instance.modelStore[vehicles.first()]
            response << new ValueChangedCommand(attributeId: pm.x.id, oldValue:pm.x.value , newValue: rand() )
            response << new ValueChangedCommand(attributeId: pm.y.id, oldValue:pm.y.value , newValue: rand() )
            response << new ValueChangedCommand(attributeId: pm.rotate.id, oldValue:pm.rotate.value , newValue: rand()%90 )

        }
    }
}

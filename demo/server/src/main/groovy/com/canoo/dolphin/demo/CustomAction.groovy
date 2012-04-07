package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.comm.InitializeAttributeCommand
import static com.canoo.dolphin.demo.VehicleProperties.*

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
                response << new InitializeAttributeCommand(pmId:it, propertyName:X,      newValue:rand())
                response << new InitializeAttributeCommand(pmId:it, propertyName:Y,      newValue:rand())
                response << new InitializeAttributeCommand(pmId:it, propertyName:WIDTH,  newValue:80)
                response << new InitializeAttributeCommand(pmId:it, propertyName:HEIGHT, newValue:25)
                response << new InitializeAttributeCommand(pmId:it, propertyName:ROTATE, newValue:rand())
                response << new InitializeAttributeCommand(pmId:it, propertyName:COLOR,  newValue:it)
            }
        }
        registry.register 'longPoll',  { NamedCommand command, response ->
            sleep ((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = StoreAttributeAction.instance.modelStore[vehicles.first()]
            response << new ValueChangedCommand(attributeId: pm[X].id, oldValue:pm[X].value , newValue: rand() )
            response << new ValueChangedCommand(attributeId: pm[Y].id, oldValue:pm[Y].value , newValue: rand() )
            response << new ValueChangedCommand(attributeId: pm[ROTATE].id, oldValue:pm[ROTATE].value , newValue: rand() )

        }
    }
}

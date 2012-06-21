package com.canoo.dolphin.demo

import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.*

import static com.canoo.dolphin.demo.VehicleProperties.*

class CustomAction {

    def impl = { propertyName, NamedCommand command, response ->
        def actual = StoreAttributeAction.instance.modelStore['actualPm']
        def att = actual[propertyName]

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server")
    }

    def registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def rand = { (Math.random() * 350).toInteger() }
        registry.register 'setTitle', impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
        registry.register 'pullVehicles', { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeSharedAttributeCommand(pmId: it, propertyName: X, newValue: rand(), sharedPmId: "vehicle-$it", sharedPropertyName: X)
                response << new InitializeSharedAttributeCommand(pmId: it, propertyName: Y, newValue: rand(), sharedPmId: "vehicle-$it", sharedPropertyName: Y)
                response << new InitializeAttributeCommand(pmId: it, propertyName: WIDTH, newValue: 80)
                response << new InitializeAttributeCommand(pmId: it, propertyName: HEIGHT, newValue: 25)
                response << new InitializeSharedAttributeCommand(pmId: it, propertyName: ROTATE, newValue: rand(), sharedPmId: "vehicle-$it", sharedPropertyName: ROTATE)
                response << new InitializeSharedAttributeCommand(pmId: it, propertyName: COLOR, newValue: it, sharedPmId: "vehicle-$it", sharedPropertyName: COLOR)
            }
        }
        registry.register 'longPoll', { NamedCommand command, response ->
            sleep((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = StoreAttributeAction.instance.modelStore[vehicles.first()]
            response << new ValueChangedCommand(attributeId: pm[X].id, oldValue: pm[X].value, newValue: rand())
            response << new ValueChangedCommand(attributeId: pm[Y].id, oldValue: pm[Y].value, newValue: rand())
            response << new ValueChangedCommand(attributeId: pm[ROTATE].id, oldValue: pm[ROTATE].value, newValue: rand())

        }
        registry.register 'pullTasks', { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "description", newValue: rand())
                response << new InitializeSharedAttributeCommand(pmId: "TaskFor " + it, propertyName: "vehicleFill", newValue: it, sharedPmId: "vehicle-$it", sharedPropertyName: COLOR)
                response << new InitializeSharedAttributeCommand(pmId: "TaskFor " + it, propertyName: "vehicleX", newValue: null, sharedPmId: "vehicle-$it", sharedPropertyName: X)
            }
        }

        registry.register GetPmCommand, { GetPmCommand command, response ->

            switch (command.pmType) {
                case 'vehicleDetail':
                    def pmId = command.pmId
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: WIDTH,     newValue: rand(),  )
                    response << new InitializeSharedAttributeCommand(pmId: pmId, propertyName: X,     newValue: null, sharedPmId: "vehicle-${ command.selector }", sharedPropertyName: X)
                    response << new InitializeSharedAttributeCommand(pmId: pmId, propertyName: Y,      newValue: null, sharedPmId: "vehicle-${ command.selector }", sharedPropertyName: Y)
                    response << new InitializeSharedAttributeCommand(pmId: pmId, propertyName: ROTATE, newValue: null, sharedPmId: "vehicle-${ command.selector }", sharedPropertyName: ROTATE)
                    response << new InitializeSharedAttributeCommand(pmId: pmId, propertyName: COLOR, newValue: null, sharedPmId: "vehicle-${ command.selector }", sharedPropertyName: COLOR)
                    break
            }

        }
    }
}

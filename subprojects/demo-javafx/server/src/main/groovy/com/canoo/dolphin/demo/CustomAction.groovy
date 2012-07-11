package com.canoo.dolphin.demo

import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.*

import static com.canoo.dolphin.demo.VehicleProperties.*

class CustomAction {

    def impl = { propertyName, NamedCommand command, response ->
        def actual = StoreAttributeAction.instance.modelStore.findPresentationModelById('actualPm')
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
                response << new InitializeAttributeCommand(pmId: it, propertyName: X,      newValue: rand(), dataId: "vehicle-${it}.x")
                response << new InitializeAttributeCommand(pmId: it, propertyName: Y,      newValue: rand(), dataId: "vehicle-${it}.y")
                response << new InitializeAttributeCommand(pmId: it, propertyName: WIDTH,  newValue: 80)
                response << new InitializeAttributeCommand(pmId: it, propertyName: HEIGHT, newValue: 25)
                response << new InitializeAttributeCommand(pmId: it, propertyName: ROTATE, newValue: rand(), dataId: "vehicle-${it}.rotate")
                response << new InitializeAttributeCommand(pmId: it, propertyName: COLOR,  newValue: it,     dataId: "vehicle-${it}.color")
            }
        }
        registry.register 'longPoll', { NamedCommand command, response ->
            sleep((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = StoreAttributeAction.instance.modelStore.findPresentationModelById(vehicles.first())
            response << new ValueChangedCommand(attributeId: pm[X].id, oldValue: pm[X].value, newValue: rand())
            response << new ValueChangedCommand(attributeId: pm[Y].id, oldValue: pm[Y].value, newValue: rand())
            response << new ValueChangedCommand(attributeId: pm[ROTATE].id, oldValue: pm[ROTATE].value, newValue: rand())

        }
        registry.register 'pullTasks', { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "description", newValue: rand())
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "fill", dataId: "vehicle-${it}.color")
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "x",    dataId: "vehicle-${it}.x")
            }
        }

        registry.register GetPmCommand, { GetPmCommand command, response ->
            switch (command.pmType) {
                case 'vehicleDetail':
                    def pmId = command.pmId
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: WIDTH,  newValue: rand(),  )
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: X,      dataId: "vehicle-${command.selector}.x")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: Y,      dataId: "vehicle-${command.selector}.y")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: ROTATE, dataId: "vehicle-${command.selector}.rotate")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: COLOR,  dataId: "vehicle-${command.selector}.color")
                    break
            }
        }
    }
}

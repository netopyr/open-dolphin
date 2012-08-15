package com.canoo.dolphin.demo

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.action.ServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.*

import static com.canoo.dolphin.demo.VehicleProperties.*

// todo dk: split into separate actions

class CustomAction implements ServerAction {
    private final ModelStore modelStore

    CustomAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    private Closure impl = { propertyName, NamedCommand command, response ->
        def actual = modelStore.findPresentationModelById('actualPm')
        def att = actual.findAttributeByPropertyName(propertyName)

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server")
    }

    void registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def rand = { (Math.random() * 350).toInteger() }
        registry.register 'setTitle', impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
        registry.register 'pullVehicles', { NamedCommand command, response ->
            vehicles.each { String pmId ->
                PresentationModel model = new ServerPresentationModel(pmId, [
                        newAttribute(propertyName: ATT_X,      value: rand(), qualifier: "vehicle-${pmId}.x"),
                        newAttribute(propertyName: ATT_Y,      value: rand(), qualifier: "vehicle-${pmId}.y"),
                        newAttribute(propertyName: ATT_WIDTH,  value: 80),
                        newAttribute(propertyName: ATT_HEIGHT, value: 25),
                        newAttribute(propertyName: ATT_ROTATE, value: rand(), qualifier: "vehicle-${pmId}.rotate"),
                        newAttribute(propertyName: ATT_COLOR,  value: pmId,   qualifier: "vehicle-${pmId}.color")
                ])
				model.setPresentationModelType('vehicle')
                response << new CreatePresentationModelCommand(model)
            }
        }
        registry.register 'longPoll', { NamedCommand command, response ->
            sleep((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = modelStore.findPresentationModelById(vehicles.first())
            response << pm[ATT_X].changeValueCommand(rand())
            response << pm[ATT_Y].changeValueCommand(rand())
            response << pm[ATT_ROTATE].changeValueCommand(rand())

        }
        registry.register 'pullTasks', { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "description", newValue: rand())
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "fill", qualifier: "vehicle-${it}.color")
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "x", qualifier: "vehicle-${it}.x")
            }
        }

        registry.register GetPresentationModelCommand, { GetPresentationModelCommand command, response ->
            if (command.pmId.startsWith('vehicleDetail')) {
                String selector = command.pmId.split('-')[1]
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_WIDTH, newValue: rand(),)
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_X, qualifier: "vehicle-${selector}.x")
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_Y, qualifier: "vehicle-${selector}.y")
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_ROTATE, qualifier: "vehicle-${selector}.rotate")
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_COLOR, qualifier: "vehicle-${selector}.color")
            }
        }
    }

    private ServerAttribute newAttribute(Map params) {
        ServerAttribute attribute = new ServerAttribute(params.remove('propertyName'), params.value)
        params.each { key, value -> attribute[key] = value }
        attribute
    }
}

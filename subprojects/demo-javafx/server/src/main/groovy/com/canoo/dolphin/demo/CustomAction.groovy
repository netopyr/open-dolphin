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
                        newAttribute(propertyName: X,      value: rand(), dataId: "vehicle-${pmId}.x"),
                        newAttribute(propertyName: Y,      value: rand(), dataId: "vehicle-${pmId}.y"),
                        newAttribute(propertyName: WIDTH,  value: 80),
                        newAttribute(propertyName: HEIGHT, value: 25),
                        newAttribute(propertyName: ROTATE, value: rand(), dataId: "vehicle-${pmId}.rotate"),
                        newAttribute(propertyName: COLOR,  value: pmId,   dataId: "vehicle-${pmId}.color")
                ])
				model.setPresentationModelType('vehicle')
                response << new CreatePresentationModelCommand(model)
            }
        }
        registry.register 'longPoll', { NamedCommand command, response ->
            sleep((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = modelStore.findPresentationModelById(vehicles.first())
            response << new ValueChangedCommand(attributeId: pm[X].id, oldValue: pm[X].value, newValue: rand())
            response << new ValueChangedCommand(attributeId: pm[Y].id, oldValue: pm[Y].value, newValue: rand())
            response << new ValueChangedCommand(attributeId: pm[ROTATE].id, oldValue: pm[ROTATE].value, newValue: rand())

        }
        registry.register 'pullTasks', { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "description", newValue: rand())
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "fill", dataId: "vehicle-${it}.color")
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "x", dataId: "vehicle-${it}.x")
            }
        }

        registry.register GetPmCommand, { GetPmCommand command, response ->
            switch (command.pmType) {
                case 'vehicleDetail':
                    def pmId = command.pmId
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: WIDTH, newValue: rand(),)
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: X, dataId: "vehicle-${command.selector}.x")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: Y, dataId: "vehicle-${command.selector}.y")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: ROTATE, dataId: "vehicle-${command.selector}.rotate")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: COLOR, dataId: "vehicle-${command.selector}.color")
                    break
            }
        }
    }

    private ServerAttribute newAttribute(Map params) {
        ServerAttribute attribute = new ServerAttribute(params.remove('propertyName'), params.value)
        params.each { key, value -> attribute[key] = value }
        attribute
    }
}

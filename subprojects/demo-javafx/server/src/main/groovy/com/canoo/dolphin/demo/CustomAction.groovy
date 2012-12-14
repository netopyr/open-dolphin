/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.demo
import com.canoo.dolphin.core.comm.*
import com.canoo.dolphin.core.server.DTO
import com.canoo.dolphin.core.server.Slot
import com.canoo.dolphin.core.server.action.DolphinServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry

import static com.canoo.dolphin.demo.VehicleProperties.*
// todo dk: split into separate actions

class CustomAction extends DolphinServerAction {


    private Closure impl = { propertyName, NamedCommand command, response ->
        def actual = serverDolphin.findPresentationModelById('actualPm')
        def att = actual.findAttributeByPropertyName(propertyName)

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server")
    }

    void registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def rand = { (Math.random() * 350).toInteger() }
        registry.register 'setTitle', impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
        registry.register CMD_PULL, { NamedCommand command, List<Command> response ->
            vehicles.each { String pmId ->
                presentationModel( pmId, PM_TYPE_VEHICLE, new DTO (
                    new Slot(ATT_X,        rand(), "vehicle-${pmId}.x"),
                    new Slot(ATT_Y,        rand(), "vehicle-${pmId}.y"),
                    new Slot(ATT_WIDTH,    80),
                    new Slot(ATT_HEIGHT,   25),
                    new Slot(ATT_ROTATE,   rand(), "vehicle-${pmId}.rotate"),
                    new Slot(ATT_COLOR,    pmId,   "vehicle-${pmId}.color")
                ))
            }
        }
        registry.register CMD_UPDATE, { NamedCommand command, response ->
            sleep((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = serverDolphin.findPresentationModelById(vehicles.first())
            changeValue pm[ATT_X],        rand()
            changeValue pm[ATT_Y],        rand()
            changeValue pm[ATT_ROTATE],   rand()
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

}

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

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.comm.NamedCommandHandler

import static VehicleConstants.*

class PullVehiclesActionHandler implements NamedCommandHandler {
    int rand() { return (Math.random() * 350).toInteger() }

    void handleCommand(NamedCommand command, List<Command> response) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        vehicles.each { String pmId ->
            PresentationModel model = new ServerPresentationModel(pmId, [
                new ServerAttribute(propertyName: ATT_X,        baseValue: rand(),  qualifier: "vehicle-${ pmId }.x"),
                new ServerAttribute(propertyName: ATT_Y,        baseValue: rand(),  qualifier: "vehicle-${ pmId }.y"),
                new ServerAttribute(propertyName: ATT_WIDTH,    baseValue: 80),
                new ServerAttribute(propertyName: ATT_HEIGHT,   baseValue: 25),
                new ServerAttribute(propertyName: ATT_ROTATE,   baseValue: rand(),  qualifier: "vehicle-${ pmId }.rotate"),
                new ServerAttribute(propertyName: ATT_COLOR,    baseValue: pmId,    qualifier: "vehicle-${ pmId }.color")
            ])
            model.setPresentationModelType(TYPE_VEHICLE)
            response << CreatePresentationModelCommand.makeFrom(model)
        }
    }
}
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

import static VehicleConstants.*

class VehiclePushActions extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def rand = { (Math.random() * 350).toInteger() }
        registry.register CMD_PULL, { NamedCommand command, List<Command> response ->
            vehicles.each { String pmId ->
                presentationModel( pmId, TYPE_VEHICLE, new DTO (
                    new Slot(ATT_X,        rand(), qualify(pmId, ATT_X)),
                    new Slot(ATT_Y,        rand(), qualify(pmId, ATT_Y)),
                    new Slot(ATT_WIDTH,    80),
                    new Slot(ATT_HEIGHT,   25),
                    new Slot(ATT_ROTATE,   rand(), qualify(pmId, ATT_ROTATE)),
                    new Slot(ATT_COLOR,    pmId,   qualify(pmId, ATT_COLOR))
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

    }

}

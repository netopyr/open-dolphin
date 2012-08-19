package com.canoo.dolphin.demo

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.action.NamedCommandHandler

import static com.canoo.dolphin.demo.VehicleProperties.*

class PullVehiclesActionHandler implements NamedCommandHandler {

    int rand() { return (Math.random() * 350).toInteger() }

    void call(NamedCommand command, List<Command> response) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        vehicles.each { String pmId ->
            PresentationModel model = new ServerPresentationModel(pmId, [
                new ServerAttribute(propertyName: ATT_X,        initialValue: rand(),  qualifier: "vehicle-${ pmId }.x"),
                new ServerAttribute(propertyName: ATT_Y,        initialValue: rand(),  qualifier: "vehicle-${ pmId }.y"),
                new ServerAttribute(propertyName: ATT_WIDTH,    initialValue: 80),
                new ServerAttribute(propertyName: ATT_HEIGHT,   initialValue: 25),
                new ServerAttribute(propertyName: ATT_ROTATE,   initialValue: rand(),  qualifier: "vehicle-${ pmId }.rotate"),
                new ServerAttribute(propertyName: ATT_COLOR,    initialValue: pmId,    qualifier: "vehicle-${ pmId }.color")
            ])
            model.setPresentationModelType(PM_TYPE_VEHICLE)
            response << new CreatePresentationModelCommand(model)
        }
    }
}
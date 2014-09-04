package org.opendolphin.demo

import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

public class TesselAction extends DolphinServerAction {

    public void registerIn(ActionRegistry actionRegistry) {
        actionRegistry.register("tesselInit", new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {

                println "tesselInit called"

                getServerDolphin().presentationModel("tessel", null, new DTO(
                    new Slot("light", 0),
                    new Slot("sound", 0),
                    new Slot("led1",  0),
                    new Slot("led2",  0),
                ))
            }
        })

        actionRegistry.register("tesselPing", new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {

                def tessel = getServerDolphin().getAt("tessel")

                println tessel

                def led = tessel.getAt('led1')

                println led.value

                led.value = 1
            }
        })


    }

}


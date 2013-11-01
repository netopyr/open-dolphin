package org.opendolphin.demo

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.util.List

public class TutorialAction extends DolphinServerAction {

    private int count = 0

    public void registerIn(ActionRegistry actionRegistry) {
        actionRegistry.register("org.opendolphin.demo.Tutorial.echo", new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {

                println getServerDolphin().listPresentationModelIds()

                final ServerPresentationModel presentationModel = getServerDolphin().getAt('org.opendolphin.demo.Tutorial.modelId')
                println presentationModel
                final ServerAttribute attribute = presentationModel["attrId"]
                println attribute

                changeValue attribute, "Server: ${attribute.value}"
            }
        })

        actionRegistry.register(ValueChangedCommand, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                println command
            }
        })

        actionRegistry.register("org.opendolphin.demo.Tutorial.add", new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                count++
                presentationModel("weather.$count", "weather", new DTO(
                        new Slot("temperature", String.valueOf((int) (Math.random() * 100)), "weather.${count}.temperature"),
                        new Slot("humidity",    String.valueOf((int) (Math.random() * 100)), "weather.${count}.humidity")
                ))
            }
        })
    }

}


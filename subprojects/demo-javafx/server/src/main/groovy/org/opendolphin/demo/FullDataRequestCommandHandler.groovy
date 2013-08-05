package org.opendolphin.demo

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.comm.NamedCommandHandler

class FullDataRequestCommandHandler implements NamedCommandHandler {

    private final int numEntries

    public FullDataRequestCommandHandler(int numEntries) {
        this.numEntries = numEntries
    }

    @Override
    void handleCommand(NamedCommand command, List<Command> response) {
        for (int i=0; i < numEntries; i++) {
            response.add(new DataCommand(new HashMap(id: i)))
        }
    }
}

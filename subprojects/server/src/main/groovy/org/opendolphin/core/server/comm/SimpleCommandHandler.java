package org.opendolphin.core.server.comm;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;

import java.util.List;

/**
 * Convenience class for all command handlers that do not need any info
 * from the command nor access the response.
 */

public abstract class SimpleCommandHandler implements NamedCommandHandler {
    @Override
    public void handleCommand(NamedCommand command, List<Command> response) {
        handleCommand();
    }
    public abstract void handleCommand();
}

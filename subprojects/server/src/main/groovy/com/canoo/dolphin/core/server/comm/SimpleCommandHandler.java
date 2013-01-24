package com.canoo.dolphin.core.server.comm;

import com.canoo.dolphin.core.comm.Command;
import com.canoo.dolphin.core.comm.NamedCommand;

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

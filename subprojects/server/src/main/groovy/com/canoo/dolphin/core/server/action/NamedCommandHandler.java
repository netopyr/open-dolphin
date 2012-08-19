package com.canoo.dolphin.core.server.action;

import com.canoo.dolphin.core.comm.Command;
import com.canoo.dolphin.core.comm.NamedCommand;

import java.util.List;

public interface NamedCommandHandler {
    public void call(NamedCommand command, List<Command> response);
}

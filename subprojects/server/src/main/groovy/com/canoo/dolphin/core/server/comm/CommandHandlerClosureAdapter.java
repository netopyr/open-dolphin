package com.canoo.dolphin.core.server.comm;

import com.canoo.dolphin.core.comm.Command;
import groovy.lang.Closure;

import java.util.List;

public class CommandHandlerClosureAdapter implements CommandHandler<Command> {
    private final Closure closure;

    public CommandHandlerClosureAdapter(Closure closure) {
        this.closure = closure;
    }

    public Closure getClosure() {
        return closure;
    }

    @Override
    public void handleCommand(Command command, List<Command> response) {
        closure.call(command, response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommandHandlerClosureAdapter that = (CommandHandlerClosureAdapter) o;

        if (!closure.equals(that.closure)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return closure.hashCode();
    }
}

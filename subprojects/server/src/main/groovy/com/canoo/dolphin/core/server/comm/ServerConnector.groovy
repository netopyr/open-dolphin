package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Codec
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.server.action.*
import groovy.util.logging.Log

@Log
class ServerConnector {
    Codec codec

    ActionRegistry registry = new ActionRegistry()

    /** doesn't fail on missing commands **/
    List<Command> receive(Command command) {
        log.info "S:     received $command"
        List<Command> response = new LinkedList() // collecting parameter pattern
        List<CommandHandler> actions = registry[command.id]
        if (! actions){
            log.warning "S: there is no server action registered for received command: $command, " +
                        "known commands are ${registry.actions.keySet()}"
            return response
        }
        // copying the list of actions allow an Action to unregister itself
        // avoiding ConcurrentModificationException to be thrown by the loop
        List<CommandHandler> actionsCopy = []
        actionsCopy.addAll actions
        for (CommandHandler action: actionsCopy) {
            action.handleCommand command, response
        }
        return response
    }

    void register(ServerAction action){
        action.registerIn registry
    }
}

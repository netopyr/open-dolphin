package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.server.action.*
import groovy.util.logging.Log

@Log
class ServerConnector {

    ActionRegistry registry = new ActionRegistry()

    /** doesn't fail on missing commands **/
    List<Command> receive(Command command) {
        log.info "S:     received $command"
        List<Command> response = new LinkedList() // collecting parameter pattern
        def actions = registry[command.id]
        if (! actions){
            log.warning "S: there is no server action registered for received command: $command, " +
                        "known commands are ${registry.actions.keySet()}"
            return response
        }
        for (action in actions) {
            action command, response
        }
        return response
    }

    void register(ServerAction action){
        action.registerIn registry
    }

}

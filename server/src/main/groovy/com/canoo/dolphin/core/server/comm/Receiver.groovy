package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command
import groovy.util.logging.Log

@Log
class Receiver {

    ActionRegistry registry = new ActionRegistry()

    /** doesn't fail on missing commands **/
    void receive(Command command) {
        log.info "received: $command"
        def serverCommand = registry[command.commandId]
        if (null == serverCommand){
            log.warning "there is no server command registered for received command: $command"
            return
        }
        serverCommand command
    }


    
}

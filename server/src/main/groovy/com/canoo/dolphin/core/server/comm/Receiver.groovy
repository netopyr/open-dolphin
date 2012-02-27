package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command
import groovy.util.logging.Log

@Log
class Receiver {

    CommandRegistry registry = new CommandRegistry()

    /** doesn't fail on missing commands **/
    def receive(Command command) {
        log.info "received: $command"
        ServerCommand serverCommand = registry[command.commandId]
        if (!serverCommand){
            log.warning "there is no server command registered for received command: $command"
            return
        }
        serverCommand command
    }


    
}

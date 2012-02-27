package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command
import groovy.util.logging.Log

@Log
class ValueChangedServerCommand extends ServerCommand {

    def call(Command command) {
        log.info "processing value change: $command"
    }
    
}

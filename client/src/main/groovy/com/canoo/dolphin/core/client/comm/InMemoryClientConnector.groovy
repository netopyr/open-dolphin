package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.comm.Command
import groovy.util.logging.Log

@Singleton @Log
class InMemoryClientConnector extends ClientConnector {

    def receiver // must be injected since the class is only available in a "combined" context

    @Override
    List<Command> transmit(Command command) {
        if (!receiver) {
            log.warning "no receiver wired for in-memory connector"
            return Collections.EMPTY_LIST
        }
        receiver.receive(command) // there is no need for encoding since we are in-memory
    }
}

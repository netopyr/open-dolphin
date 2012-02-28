package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.comm.Command

@Singleton
class InMemoryClientConnector extends ClientConnector {

    def receiver // must be injected since the class is only available in a "combined" context

    @Override
    List<Command> transmit(Command command) {
        List<Command> response = receiver.receive(command) // there is no need for encoding since we are in-memory
    }
}

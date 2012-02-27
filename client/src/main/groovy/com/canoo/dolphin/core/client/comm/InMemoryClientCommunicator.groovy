package com.canoo.dolphin.core.client.comm

import groovy.util.logging.Log

@Singleton @Log
class InMemoryClientCommunicator extends ClientCommunicator {

    def receiver // must be injected since the class is only available in a "combined" context

    @Override
    void send(ClientCommand command) {
        receiver.receive(command) // there is no need for encoding since we are in-memory
    }
}

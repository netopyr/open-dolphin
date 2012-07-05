package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.comm.Command
import groovy.util.logging.Log

@Singleton @Log
class InMemoryClientConnector extends ClientConnector {

    def processAsync = true
    def sleepMillis = 0
    def receiver // must be injected since the class is only available in a "combined" context

    int getPoolSize() { 1 } // we want to be asynchronous but with one thread only

    @Override
    List<Command> transmit(Command command) {
        if (!receiver) {
            log.warning "no receiver wired for in-memory connector"
            return Collections.EMPTY_LIST
        }
        if (sleepMillis) sleep sleepMillis
        receiver.receive(command) // there is no need for encoding since we are in-memory
    }

    void processAsync(Runnable processing) {
        if (processAsync) super.processAsync(processing)
        else processing()
    }

}

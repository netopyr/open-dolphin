package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.client.comm.UiThreadHandler
import com.canoo.dolphin.core.server.action.ServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry

import java.util.concurrent.CountDownLatch

class TestInMemoryConfig extends DefaultInMemoryConfig {

    /** needed since tests should run fully asynchronous but we have to wait at the end of the test */
    CountDownLatch done = new CountDownLatch(1)

    TestInMemoryConfig() {
        registerDefaultActions()
        connector.sleepMillis = 0
        connector.uiThreadHandler = { it() } as UiThreadHandler
    }

    /** convenience method to register a named action */
    ServerAction register(String name, Closure logic){
        register new ServerAction() {
            @Override
            void registerIn(ActionRegistry registry) {
                registry.register name, logic
            }
        }
    }

    /** convenience method to send a named command */
    void send(String commandName, Closure onFinished = null) {
        connector.send new NamedCommand(commandName), onFinished
    }

    void assertionsDone() {
        done.countDown()
    }

}

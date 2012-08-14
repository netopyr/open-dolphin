package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.client.comm.UiThreadHandler
import com.canoo.dolphin.core.server.action.ClosureServerAction
import com.canoo.dolphin.core.server.action.ServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry

import java.util.concurrent.CountDownLatch

class TestInMemoryConfig extends DefaultInMemoryConfig {

    /** needed since tests should run fully asynchronous but we have to wait at the end of the test */
    CountDownLatch done = new CountDownLatch(1)

    TestInMemoryConfig() {
        serverDolphin.registerDefaultActions()
        clientDolphin.clientConnector.sleepMillis = 0
        clientDolphin.clientConnector.uiThreadHandler = { it() } as UiThreadHandler
    }

    /** convenience method to send a named command */
    void send(String commandName, OnFinishedHandler onFinished = null) {
        clientDolphin.clientConnector.send new NamedCommand(commandName), onFinished
    }

    void assertionsDone() {
        done.countDown()
    }

}

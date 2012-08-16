package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.client.comm.UiThreadHandler

import java.util.concurrent.CountDownLatch

class TestInMemoryConfig extends DefaultInMemoryConfig {

    /** needed since tests should run fully asynchronous but we have to wait at the end of the test */
    CountDownLatch done = new CountDownLatch(1)

    TestInMemoryConfig() {
        serverDolphin.registerDefaultActions()
        clientDolphin.clientConnector.sleepMillis = 0
        clientDolphin.clientConnector.uiThreadHandler = { it() } as UiThreadHandler
    }

    void assertionsDone() {
        done.countDown()
    }

}

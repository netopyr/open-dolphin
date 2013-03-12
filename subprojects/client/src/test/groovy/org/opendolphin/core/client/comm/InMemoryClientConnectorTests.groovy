package org.opendolphin.core.client.comm

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.comm.Command

import java.util.concurrent.CountDownLatch

class InMemoryClientConnectorTests extends GroovyTestCase {

    void testCallConnector_NoServerConnectorWired() {
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin())
        assert [] == connector.transmit(new Command())
    }

    void testCallConnector_ServerWired() {
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin())
        def command = new Command()
        boolean serverCalled = false
        connector.serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }]
        connector.transmit(command)
        assert serverCalled
    }

    void testCallConnector_ServerWiredWithSleep() {
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin())
        connector.sleepMillis = 10
        def command = new Command()
        boolean serverCalled = false
        connector.serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }]
        connector.transmit(command)
        assert serverCalled
    }

    void testProcessAsync_Disabled() {
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin())
        connector.processAsync = false
        def mainThread = Thread.currentThread()
        def workerThread = null
        connector.processAsync {
            workerThread = Thread.currentThread()
        }
        assert mainThread == workerThread
    }

    void testProcessAsync() {
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin())
        def mainThread = Thread.currentThread()
        def workerThread = null
        def latch = new CountDownLatch(1)
        def action = {
            workerThread = Thread.currentThread()
            latch.countDown()
        }
        connector.processAsync action
        latch.await()
        assert mainThread != workerThread
    }

}

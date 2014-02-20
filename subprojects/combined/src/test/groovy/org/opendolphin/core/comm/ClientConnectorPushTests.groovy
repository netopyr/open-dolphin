package org.opendolphin.core.comm

import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.server.ServerDolphin
import spock.lang.Specification
import java.util.concurrent.TimeUnit

class ClientConnectorPushTests extends Specification {

    volatile TestInMemoryConfig app
    ServerDolphin serverDolphin
    ClientDolphin clientDolphin

    protected TestInMemoryConfig initApp() {
        def result = new TestInMemoryConfig()
        serverDolphin = result.serverDolphin
        clientDolphin = result.clientDolphin
        result
    }

    protected void setup() {
        LogConfig.noLogs()
        app = initApp()
    }

    // make sure the tests only count as ok if context.assertionsDone() has been reached
    protected void cleanup() {
        clientDolphin.sync { app.assertionsDone() }
        assert app.done.await(2, TimeUnit.SECONDS) // max waiting time for async operations to have finished
    }


    void "listening without push action does not work"() {
        when:
        clientDolphin.startPushListening(null, "ReleaseAction")
        then:
        clientDolphin.isPushListening() == false
    }
    void "listening without release action does not work"() {
        when:
        clientDolphin.startPushListening("PushAction", null)
        then:
        clientDolphin.isPushListening() == false
    }
    void "listening can be started and stopped"() {
        when:
        clientDolphin.startPushListening("PushAction", "ReleaseAction")
        then:
        clientDolphin.isPushListening()
        when:
        clientDolphin.stopPushListening()
        then:
        clientDolphin.isPushListening() == false
    }

    void "core push: server-side commands are immediately processed"() {
    }
    void "autorelease: sending any client-side command releases the read lock"() {

    }



}
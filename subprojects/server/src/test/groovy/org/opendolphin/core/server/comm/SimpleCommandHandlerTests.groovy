package org.opendolphin.core.server.comm

import org.opendolphin.core.comm.NamedCommand

class SimpleCommandHandlerTests extends GroovyTestCase {

    void testCheckCallForwarding() {
        TestSimpleCommandHandler commandHandler = new TestSimpleCommandHandler()
        commandHandler.handleCommand(new NamedCommand('command'), [])
        assert commandHandler.callForwarded
    }
}

class TestSimpleCommandHandler extends SimpleCommandHandler {
    boolean callForwarded

    @Override
    void handleCommand() {
        callForwarded = true
    }
}

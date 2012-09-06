package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand

class ClientConnectorTest extends GroovyTestCase {

    ClientConnector clientConnector
    ClientDolphin   dolphin

    protected void setUp() {
        dolphin = new ClientDolphin()
        clientConnector = new TestClientConnector(dolphin)
        dolphin.clientConnector = clientConnector
        dolphin.clientModelStore = new ClientModelStore(dolphin)
    }

    void testHandleSimpleCreatePresentationModelCommand() {
        final myPmId = "myPmId"
        assert null == dolphin.findPresentationModelById(myPmId)
        CreatePresentationModelCommand command = new CreatePresentationModelCommand()
        command.pmId = myPmId
        def result = clientConnector.handle(command)
        assert myPmId == result
        assert dolphin.findPresentationModelById(myPmId)
    }

}

class TestClientConnector extends ClientConnector {
    TestClientConnector(ClientDolphin clientDolphin) {
        super(clientDolphin)
    }

    int getPoolSize() { 1 }

    List<Command> transmit(Command command) {
        return []
    }
}

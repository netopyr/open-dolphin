/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.core.client.comm

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand

class ClientConnectorTest extends GroovyTestCase {

    ClientConnector clientConnector
    ClientDolphin   dolphin

    protected void setUp() {
        dolphin = new ClientDolphin()
        clientConnector = new TestClientConnector(dolphin)
        clientConnector.uiThreadHandler = { it() } as UiThreadHandler
        dolphin.clientConnector = clientConnector
        dolphin.clientModelStore = new ClientModelStore(dolphin)
    }

    void testHandleSimpleCreatePresentationModelCommand() {
        final myPmId = "myPmId"
        assert null == dolphin.findPresentationModelById(myPmId)
        CreatePresentationModelCommand command = new CreatePresentationModelCommand()
        command.pmId = myPmId
        def result = clientConnector.handle(command)
        assert myPmId == result.id
        assert dolphin.findPresentationModelById(myPmId)
    }

    void testDefaultOnExceptionHandler(){
        def msg = shouldFail(RuntimeException) {
            clientConnector.onException(new RuntimeException("test exception"))
        }
        println "..."
        println msg
    }

}

import groovy.util.logging.Log

@Log
class TestClientConnector extends ClientConnector {
    TestClientConnector(ClientDolphin clientDolphin) {
        super(clientDolphin)
    }

    int getPoolSize() { 1 }

    List<Command> transmit(Command command) {
        return []
    }
}

/*
 * Copyright 2012-2013 Canoo Engineering AG.
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

package org.opendolphin.core.comm

import org.opendolphin.LogConfig
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.comm.NamedCommandHandler

/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */

class FunctionalPresentationModelTests extends GroovyTestCase {

    TestInMemoryConfig context
    ServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
        LogConfig.noLogs()
    }

    @Override
    protected void tearDown() {
        context.done.await()
    }

    void testPerformance() {
        long id = 0
        serverDolphin.action "performance", { cmd, response ->
            100.times { attr ->
                serverDolphin.presentationModel(response, "id_${id++}".toString(), null, new DTO(new Slot("attr_$attr", attr)))
            }
        }
        def start = System.nanoTime()
        100.times {
            clientDolphin.send "performance", { List<ClientPresentationModel> pms ->
                assert pms.size() == 100
                pms.each { clientDolphin.delete(it) }
            }
        }
        clientDolphin.send "performance", { List<ClientPresentationModel> pms ->
            assert pms.size() == 100
            println ((System.nanoTime() - start).intdiv(1_000_000))
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void testCreationRoundtripDefaultBehavior() {
        serverDolphin.action "create", { cmd, response ->
            serverDolphin.presentationModel(response, "id".toString(), null, new DTO(new Slot("attr", 'attr')))
        }
        serverDolphin.action "checkNotificationReached", { cmd, response ->
            assert 1 == serverDolphin.modelStore.listPresentationModels().size()
            assert serverDolphin.getAt("id")
        }

        clientDolphin.send "create", { List<ClientPresentationModel> pms ->
            assert pms.size() == 1
            assert 'attr' == pms.first().getAt("attr").value
            clientDolphin.send "checkNotificationReached", { List<ClientPresentationModel> xxx ->
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void testCreationNoRoundtripWhenClientSideOnly() {
        serverDolphin.action "create", { cmd, response ->
            serverDolphin.clientSideModel(response, "id".toString(), null, new DTO(new Slot("attr", 'attr')))
        }
        serverDolphin.action "checkNotificationReached", { cmd, response ->
            assert 0 == serverDolphin.modelStore.listPresentationModels().size()
        }

        clientDolphin.send "create", { List<ClientPresentationModel> pms ->
            assert pms.size() == 1
            assert 'attr' == pms.first().getAt("attr").value
            clientDolphin.send "checkNotificationReached", { List<ClientPresentationModel> xxx ->
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void testFetchingAnInitialListOfData() {
        serverDolphin.action "fetchData", { cmd, response ->
            ('a'..'z').each {
                PresentationModel model = new ServerPresentationModel(it, [
                    new ServerAttribute('char', it)
                ])
                response << CreatePresentationModelCommand.makeFrom(model)
            }
        }
        clientDolphin.send "fetchData", { List<ClientPresentationModel> pms ->
            assert pms.size() == 26
            assert pms.collect { it.id }.sort(false) == pms.collect { it.id }   // pmIds from a single action should come in sequence
            assert 'a' == context.clientDolphin.findPresentationModelById('a').char.value
            assert 'z' == context.clientDolphin.findPresentationModelById('z').char.value
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void testLoginUseCase() {
        serverDolphin.action "loginCmd", { cmd, response ->
            def user = context.serverDolphin.findPresentationModelById('user')
            if (user.name.value == 'Dierk' && user.password.value == 'Koenig') {
                ServerDolphin.changeValue(response, user.loggedIn, 'true')
            }
        }
        def user = clientDolphin.presentationModel 'user', name: null, password: null, loggedIn: null
        clientDolphin.send "loginCmd", {
            assert !user.loggedIn.value
        }
        user.name.value = "Dierk"
        user.password.value = "Koenig"

        clientDolphin.send "loginCmd", {
            assert user.loggedIn.value
            context.assertionsDone()
        }
    }

    void testAsynchronousExceptionOnTheServer() {
        def count = 0
        clientDolphin.clientConnector.onException = { count++ }

        serverDolphin.action "someCmd", { cmd, response ->
            throw new RuntimeException("EXPECTED: some arbitrary exception on the server")
        }

        clientDolphin.send "someCmd", {
            fail "the onFinished handler will not be reached in this case"
        }
        clientDolphin.sync {
            assert count == 1
        }

        // provoke a second exception
        clientDolphin.send "someCmd", {
            fail "the onFinished handler will not be reached either"
        }
        clientDolphin.sync {
            assert count == 2
            context.assertionsDone()
        }
    }

    void testAsynchronousExceptionInOnFinishedHandler() {

        clientDolphin.clientConnector.onException = { context.assertionsDone() }

        serverDolphin.action "someCmd", { cmd, response ->
            // nothing to do
        }
        clientDolphin.send "someCmd", {
            throw new RuntimeException("EXPECTED: some arbitrary exception in the onFinished handler")
        }
    }

    void testUnregisteredCommand() {
        clientDolphin.send "no-such-action-registered", {
            // unknown actions are silently ignored and logged as warnings on the server side.
        }
        context.assertionsDone()
    }

    void testActionAndSendJavaLike() {
        boolean reached = false
        serverDolphin.action("java", new NamedCommandHandler() {
            @Override
            void handleCommand(NamedCommand command, List<Command> response) {
                reached = true
            }
        })
        clientDolphin.send("java", new OnFinishedHandlerAdapter() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert reached
                context.assertionsDone()
            }
        })
    }
}
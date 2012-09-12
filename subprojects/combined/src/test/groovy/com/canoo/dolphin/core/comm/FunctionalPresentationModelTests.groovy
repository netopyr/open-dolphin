package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.comm.NamedCommandHandler
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
    }

    @Override
    protected void tearDown() {
        context.done.await()
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
            assert 'a' == context.clientDolphin.clientModelStore.findPresentationModelById('a').char.value
            assert 'z' == context.clientDolphin.clientModelStore.findPresentationModelById('z').char.value
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void testLoginUseCase() {
        serverDolphin.action "loginCmd", { cmd, response ->
            def user = context.serverDolphin.serverModelStore.findPresentationModelById('user')
            if (user.name.value == 'Dierk' && user.password.value == 'Koenig') {
                response << user.loggedIn.changeValueCommand('true')
            }
        }
        def user = clientDolphin.presentationModel 'user', name:null, password:null, loggedIn:null
        clientDolphin.send "loginCmd", {
            assert !user.loggedIn.value

            user.name.value = "Dierk"
            user.password.value = "Koenig"

            clientDolphin.send "loginCmd", {
                assert user.loggedIn.value
                context.assertionsDone()
            }
        }
    }

    void testAsynchronousExceptionOnTheServer() {
        serverDolphin.action "someCmd", { cmd, response ->
            throw new RuntimeException("some arbitrary exception on the server")
        }
        clientDolphin.send "someCmd", {
            fail "the onFinished handler will not be reached in this case"
        }

        assert context.clientDolphin.clientConnector.exceptionHappened.val
        context.assertionsDone()
    }

    void testAsynchronousExceptionInOnFinishedHandler() {
        serverDolphin.action "someCmd", { cmd, response ->
            // nothing to do
        }
        clientDolphin.send "someCmd", {
            context.assertionsDone()
            throw new RuntimeException("some arbitrary exception in the onFinished handler")
        }

        assert context.clientDolphin.clientConnector.exceptionHappened.val
    }

    void testUnregisteredCommand() {
        clientDolphin.send "no-such-action-registered", {
            fail "must not reach here"
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
        clientDolphin.send("java", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert reached
                context.assertionsDone()
            }
        })
    }
}
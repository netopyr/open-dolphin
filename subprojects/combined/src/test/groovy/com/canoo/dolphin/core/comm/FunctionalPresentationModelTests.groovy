package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */

class FunctionalPresentationModelTests extends GroovyTestCase {

    TestInMemoryConfig context

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
    }

    @Override
    protected void tearDown() {
        context.done.await()
    }

    void testFetchingAnInitialListOfData() {
        // server part
        context.register "fetchData", { cmd, response ->
            ('a'..'z').each {
                PresentationModel model = new ServerPresentationModel(it, [
                        new ServerAttribute('char', it)
                ])
                response << new CreatePresentationModelCommand(model)
            }
        }
        // client part
        context.send("fetchData", { List<ClientPresentationModel> pms ->
            assert pms.size() == 26
            assert pms.collect { it.id }.sort(false) == pms.collect { it.id }   // pmIds from a single action should come in sequence
            assert 'a' == context.clientDolphin.clientModelStore.findPresentationModelById('a').char.value
            assert 'z' == context.clientDolphin.clientModelStore.findPresentationModelById('z').char.value
            context.assertionsDone() // make sure the assertions are really executed
        } as OnFinishedHandler)
    }

    void testLoginUseCase() {
        // server part
        context.register "loginCmd", { cmd, response ->
            def user = context.serverDolphin.serverModelStore.findPresentationModelById('user')
            if (user.name.value == 'Dierk' && user.password.value == 'Koenig') {
                response << user.loggedIn.changeValueCommand('true')
            }
        }
        // client part
        def user = context.clientDolphin.presentationModel('user', ['name', 'password', 'loggedIn'])
        context.send("loginCmd", {
            assert !user.loggedIn.value

            user.name.value = "Dierk"
            user.password.value = "Koenig"

            context.send("loginCmd", {
                assert user.loggedIn.value
                context.assertionsDone()
            } as OnFinishedHandler)
        } as OnFinishedHandler)
    }

    void testAsynchronousExceptionOnTheServer() {
        // server part
        context.register "someCmd", { cmd, response ->
            throw new RuntimeException("some arbitrary exception on the server")
        }
        context.send("someCmd", {
            fail "the onFinished handler will not be reached in this case"
        } as OnFinishedHandler)

        assert context.clientDolphin.clientConnector.exceptionHappened.val
        context.assertionsDone()
    }

    void testAsynchronousExceptionInOnFinishedHandler() {
        // server part
        context.register "someCmd", { cmd, response ->
            // nothing to do
        }
        context.send("someCmd", {
            context.assertionsDone()
            throw new RuntimeException("some arbitrary exception in the onFinished handler")
        } as OnFinishedHandler)

        assert context.clientDolphin.clientConnector.exceptionHappened.val

    }
}
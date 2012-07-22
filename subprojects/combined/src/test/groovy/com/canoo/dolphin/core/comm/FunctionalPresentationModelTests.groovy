package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.Dolphin
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
        context.send "fetchData", { List<String> pmIds ->
            assert pmIds.size() == 26
            assert pmIds.sort(false) == pmIds   // pmIds from a single action should come in sequence
            assert 'a' == Dolphin.clientModelStore.findPresentationModelById('a').char.value
            assert 'z' == Dolphin.clientModelStore.findPresentationModelById('z').char.value
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void testLoginUseCase() {
        // server part
        context.register "loginCmd", { cmd, response ->
            def user = context.modelStore.findPresentationModelById('user')
            if (user.name.value == 'Dierk' && user.password.value == 'Koenig') {
                response << user.loggedIn.changeValueCommand('true')
            }
        }
        // client part
        def user = ClientPresentationModel.make('user', ['name','password','loggedIn'])
        context.send "loginCmd", {
            assert ! user.loggedIn.value
        }
        user.name.value = "Dierk"
        user.password.value = "Koenig"
        context.send "loginCmd", {
            assert user.loggedIn.value
            context.assertionsDone()
        }
    }
}
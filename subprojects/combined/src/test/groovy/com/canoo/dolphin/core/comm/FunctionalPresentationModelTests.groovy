package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.Dolphin
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */

class FunctionalPresentationModelTests extends GroovyTestCase {

    def context = new TestInMemoryConfig()

    @Override
    protected void tearDown() {
        context.done.await()
    }

    void testFetchingAnInitialListOfData() {
        context.register "fetchData", { cmd, response ->
            PresentationModel model = new ServerPresentationModel('pmId', [
                    new ServerAttribute('prop', 'value')
            ])
            response << new CreatePresentationModelCommand(model)
        }
        context.send "fetchData", { Set<String> pmIds ->
            assert pmIds.size() == 1
            assert 'value' == Dolphin.clientModelStore.findPresentationModelById('pmId')['prop'].value
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

}
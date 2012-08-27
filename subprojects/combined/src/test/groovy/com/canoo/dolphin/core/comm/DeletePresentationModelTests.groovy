package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.action.DeletePresentationModelAction
import com.canoo.dolphin.core.server.action.RemovePresentationModelAction

class DeletePresentationModelTests extends GroovyTestCase {

    TestInMemoryConfig context
    ServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin

        serverDolphin.register(new DeletePresentationModelAction(serverDolphin: serverDolphin))
        serverDolphin.register(new RemovePresentationModelAction(serverDolphin: serverDolphin))
    }

    @Override
    protected void tearDown() {
        // context.done.await()
    }

    void testCreateAndDeletePresentationModel() {
        String modelId = 'modelId'
        def model = new ClientPresentationModel(modelId, [])
        clientDolphin.clientModelStore.add model
        def found = clientDolphin.modelStore.findPresentationModelById(modelId)
        assert model == found
        // assert serverDolphin.modelStore.findPresentationModelById(modelId)
        clientDolphin.modelStore.delete(model)
        assert !clientDolphin.modelStore.findPresentationModelById(modelId)
        // assert !serverDolphin.modelStore.findPresentationModelById(modelId)
        context.assertionsDone()
    }
}
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
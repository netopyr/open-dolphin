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
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.*
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Functional tests for the server-side state changes.
 */

class ServerPresentationModelTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
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
        assert context.done.await(2, TimeUnit.SECONDS)
    }

    void testSecondServerActionCanRelyOnServerStateChange() {
        def model = clientDolphin.presentationModel("PM1", ["att1"] )

        serverDolphin.action "setValue", { cmd, response ->
            serverDolphin.getAt("PM1").getAt("att1").value = 1
        }

        serverDolphin.action "assertValue", { cmd, response ->
            assert 1 == serverDolphin.getAt("PM1").getAt("att1").value
        }

        clientDolphin.send "setValue"
        clientDolphin.send "assertValue"

        clientDolphin.sync {
            assert 1 == model.att1.value
            context.assertionsDone()
        }
    }

}
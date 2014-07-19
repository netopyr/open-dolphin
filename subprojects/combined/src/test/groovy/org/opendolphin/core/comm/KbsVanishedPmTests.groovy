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
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.*
import org.opendolphin.core.server.*
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Tests for the scenario that KBS observed where PMs vanished
 * when deleted and immediately recreated on the client side.
 */

class KbsVanishedPmTests extends GroovyTestCase {

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

    void testPMsWereDeletedAndRecreated() {
        // a pm created on the client side
        clientDolphin.presentationModel("pm1", new ClientAttribute("a", 0 ))

        // register a server-side action that sees the second PM
        serverDolphin.action("checkPmIsThere") { cmd, list ->
            assert serverDolphin.getAt("pm1").a.value == 1
            assert clientDolphin.getAt("pm1").a.value == 1
            context.assertionsDone()
        }

        assert clientDolphin.getAt("pm1").a.value == 0
        clientDolphin.delete(clientDolphin.getAt("pm1"))
        clientDolphin.presentationModel("pm1", new ClientAttribute("a", 1 ))
        clientDolphin.send("checkPmIsThere")
    }

}
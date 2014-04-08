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
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.server.*

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

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
//        LogConfig.noLogs()
    }

    @Override
    protected void tearDown() {
        assert context.done.await(2, TimeUnit.SECONDS)
    }

    void testSecondServerActionCanRelyOnAttributeValueChange() {
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

    void testSecondServerActionCanRelyOnAttributeReset() {
        def model = clientDolphin.presentationModel("PM1", att1:'base' )
        model.att1.value = 'changed'
        assert model.att1.dirty

        serverDolphin.action "reset", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert at.dirty
            at.reset()
            assert ! at.dirty
        }

        serverDolphin.action "assertPristine", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert ! at.dirty
            assert at.value == "base"
        }

        clientDolphin.send "reset"
        clientDolphin.send "assertPristine"

        clientDolphin.sync {
            assert ! model.att1.dirty
            assert model.att1.value == "base"
            context.assertionsDone()
        }
    }

    void testSecondServerActionCanRelyOnAttributeRebase() {
        def model = clientDolphin.presentationModel("PM1", att1:'base' )
        model.att1.value = 'changed'
        assert model.att1.dirty

        serverDolphin.action "rebase", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert at.dirty
            at.rebase()
            assert ! at.dirty
        }

        serverDolphin.action "assertNewPristine", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert ! at.dirty
            assert at.value == "changed"
        }

        clientDolphin.send "rebase"
        clientDolphin.send "assertNewPristine"

        clientDolphin.sync {
            assert ! model.att1.dirty
            assert model.att1.value == "changed"
            context.assertionsDone()
        }
    }


    void testSecondServerActionCanRelyOnPmReset() {
        def model = clientDolphin.presentationModel("PM1", att1:'base' )
        model.att1.value = 'changed'
        assert model.dirty

        serverDolphin.action "reset", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert pm.dirty
            pm.reset()
            assert ! pm.dirty
        }

        serverDolphin.action "assertPristine", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert ! pm.dirty
            assert pm.att1.value == "base"
        }

        clientDolphin.send "reset"
        clientDolphin.send "assertPristine"

        clientDolphin.sync {
            assert ! model.dirty
            assert model.att1.value == "base"
            context.assertionsDone()
        }
    }
    void testSecondServerActionCanRelyOnPmRebase() {
        def model = clientDolphin.presentationModel("PM1", att1:'base' )
        model.att1.value = 'changed'
        assert model.dirty

        serverDolphin.action "rebase", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert pm.dirty
            pm.rebase()
            assert ! pm.dirty
        }

        serverDolphin.action "assertNewPristine", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert ! pm.dirty
            assert pm.att1.baseValue == "changed"
        }

        clientDolphin.send "rebase"
        clientDolphin.send "assertNewPristine"

        clientDolphin.sync {
            assert ! model.dirty
            assert model.att1.baseValue == "changed"
            context.assertionsDone()
        }
    }

    void testSecondServerActionCanRelyOnPmCreate() {

        def pmWithNullId

        serverDolphin.action "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            def pm = serverDolphin.presentationModel("PM1", null, dto)
            pmWithNullId = serverDolphin.presentationModel(null, "pmType", dto)
            assert pm
            assert pmWithNullId
            assert serverDolphin.getAt("PM1")
            assert serverDolphin.findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        serverDolphin.action "assertVisible", { cmd, response ->
            assert serverDolphin.getAt("PM1")
            assert serverDolphin.findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        clientDolphin.send "create"
        clientDolphin.send "assertVisible"

        clientDolphin.sync {
            assert clientDolphin.getAt("PM1")
            assert clientDolphin.findAllPresentationModelsByType("pmType").size() == 1
            println clientDolphin.findAllPresentationModelsByType("pmType").first().id
            context.assertionsDone()
        }
    }

    void testServerCreatedAttributeChangesValueOnTheClientSide() {

        AtomicBoolean pclReached = new AtomicBoolean(false)

        serverDolphin.action "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            serverDolphin.presentationModel("PM1", null, dto)
            serverDolphin.getAt("PM1").getAt("att1").addPropertyChangeListener("value",{ pclReached.set(true) })
        }

        serverDolphin.action "assertValueChange", { cmd, response ->
            assert pclReached.get()
            assert serverDolphin.getAt("PM1").getAt("att1").value == 2
            context.assertionsDone()
        }

        clientDolphin.send "create", {
            assert clientDolphin.getAt("PM1").getAt("att1").value == 1
            clientDolphin.getAt("PM1").getAt("att1").value = 2

            clientDolphin.send "assertValueChange"
        }

    }

    // feature list
    // PM:  delete, deleteAllOfType, switch/apply, pcl, modelStoreListener

}
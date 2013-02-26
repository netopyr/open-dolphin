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

package org.opendolphin.core.comm

import org.opendolphin.core.client.comm.UiThreadHandler

import java.util.concurrent.CountDownLatch

class TestInMemoryConfig extends DefaultInMemoryConfig {

    /** needed since tests should run fully asynchronous but we have to wait at the end of the test */
    CountDownLatch done = new CountDownLatch(1)

    TestInMemoryConfig() {
        serverDolphin.registerDefaultActions()
        clientDolphin.clientConnector.sleepMillis = 0
        // todo dk: this is probably not quite right, since it doesn't force the callback to be handled in the main test thread
        clientDolphin.clientConnector.uiThreadHandler = { it() } as UiThreadHandler
    }

    void assertionsDone() {
        done.countDown()
    }

}

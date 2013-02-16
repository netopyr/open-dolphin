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

package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.comm.Command
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Log
import com.canoo.dolphin.core.client.ClientDolphin

@Log @InheritConstructors
class InMemoryClientConnector extends ClientConnector {

    def processAsync = true
    def sleepMillis = 0
    def serverConnector // must be injected since the class is only available in a "combined" context

    int getPoolSize() { 1 } // we want to be asynchronous but with one thread only

    InMemoryClientConnector(ClientDolphin clientDolphin) {
        super(clientDolphin)
    }

    @Override
    List<Command> transmit(Command command) {
        if (!serverConnector) {
            log.warning "no server connector wired for in-memory connector"
            return Collections.EMPTY_LIST
        }
        if (sleepMillis) sleep sleepMillis
        serverConnector.receive(command) // there is no need for encoding since we are in-memory
    }


    @CompileStatic
    void processAsync(Runnable processing) {
        if (processAsync) super.processAsync(processing)
        else doExceptionSafe(processing)
    }

}

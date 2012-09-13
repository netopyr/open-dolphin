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

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.action.CreatePresentationModelAction
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.server.action.StoreValueChangeAction
import com.canoo.dolphin.core.server.action.SwitchPresentationModelAction
import com.canoo.dolphin.core.server.comm.ServerConnector

class DefaultInMemoryConfig {

    ClientDolphin clientDolphin = new ClientDolphin()
    ServerDolphin serverDolphin = new ServerDolphin()

    DefaultInMemoryConfig() {
        LogConfig.logCommunication()

        clientDolphin.clientModelStore = new ClientModelStore(clientDolphin)
        clientDolphin.clientConnector = new InMemoryClientConnector(clientDolphin)

        clientDolphin.clientConnector.sleepMillis = 100
        clientDolphin.clientConnector.serverConnector = serverDolphin.serverConnector

    }

    /** @deprecated use clientDolphin.clientConnector */
    ClientConnector getConnector() { clientDolphin.clientConnector}

    /** @deprecated use serverDolphin.registerDefaultActions() */
    void registerDefaultActions() {
        serverDolphin.registerDefaultActions()
    }

}

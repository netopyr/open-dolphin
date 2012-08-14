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

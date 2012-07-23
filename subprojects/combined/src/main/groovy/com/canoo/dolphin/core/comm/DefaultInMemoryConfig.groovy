package com.canoo.dolphin.core.comm

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.client.Dolphin
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.server.action.CreatePresentationModelAction
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.server.action.StoreValueChangeAction
import com.canoo.dolphin.core.server.action.SwitchPresentationModelAction
import com.canoo.dolphin.core.server.comm.Receiver

class DefaultInMemoryConfig {

    Receiver receiver = new Receiver()
    ModelStore modelStore = new ModelStore()

    DefaultInMemoryConfig() {
        LogConfig.logCommunication()
        connector.sleepMillis = 100
        connector.receiver = receiver
        Dolphin.setClientConnector(connector)
        Dolphin.setClientModelStore(new ClientModelStore())
    }

    ClientConnector getConnector() { InMemoryClientConnector.instance }

    void registerDefaultActions() {
        [
                new StoreValueChangeAction(modelStore),
                new StoreAttributeAction(modelStore),
                new CreatePresentationModelAction(modelStore),
                new SwitchPresentationModelAction(modelStore),
        ].each { register it }
    }

    void register(action) {
        action.registerIn receiver.registry
    }
}

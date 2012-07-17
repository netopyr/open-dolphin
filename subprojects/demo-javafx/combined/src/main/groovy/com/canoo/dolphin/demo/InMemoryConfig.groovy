package com.canoo.dolphin.demo

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.core.client.comm.JavaFXUiThreadHandler
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.server.action.StoreValueChangeAction
import com.canoo.dolphin.core.server.action.SwitchPmAction
import com.canoo.dolphin.core.server.comm.Receiver
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.server.action.CreatePresentationModelAction

// todo dk: move default in-memory config to shared such that it can be used without dependencies to demo-javafx

class InMemoryConfig {

    Receiver receiver = new Receiver()
    ModelStore modelStore = new ModelStore()

    InMemoryConfig() {
        LogConfig.logCommunication()
        connector.sleepMillis = 100
        connector.receiver = receiver
        connector.uiThreadHandler = new JavaFXUiThreadHandler()
        new ClientModelStore(connector)
    }

    ClientConnector getConnector() { InMemoryClientConnector.instance }

    void withActions() {
        [
                new StoreValueChangeAction(modelStore),
                new StoreAttributeAction(modelStore),
                new CreatePresentationModelAction(modelStore),
                new SwitchPmAction(modelStore),
                new CustomAction(modelStore), // just to have also some application-specific action
        ].each { register it }
    }

    void register(action) {
        action.registerIn receiver.registry
    }
}

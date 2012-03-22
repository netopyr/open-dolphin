package com.canoo.dolphin.demo

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

import com.canoo.dolphin.core.server.comm.Receiver

class InMemoryConfig {

    Receiver receiver = new Receiver()

    InMemoryConfig() {
        LogConfig.logCommunication()
        InMemoryClientConnector.instance.receiver = receiver
    }

    void withActions() {
        [   new MirrorValueChangeAction(),
            new StoreValueChangeAction(),
            StoreAttributeAction.instance,
            new SwitchActualAction(),
        ].each { it.registerIn receiver.registry }
    }
}

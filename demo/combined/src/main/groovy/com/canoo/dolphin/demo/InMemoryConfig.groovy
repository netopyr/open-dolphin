package com.canoo.dolphin.demo

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

import com.canoo.dolphin.core.server.comm.Receiver
import com.canoo.dolphin.core.server.action.MirrorValueChangeAction
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.server.action.StoreValueChangeAction
import com.canoo.dolphin.core.server.action.SwitchPmAction

class InMemoryConfig {

    Receiver receiver = new Receiver()

    InMemoryConfig() {
        LogConfig.logCommunication()
        InMemoryClientConnector.instance.sleepMillis = 100
        InMemoryClientConnector.instance.receiver = receiver
    }

    void withActions() {
        [   new MirrorValueChangeAction(),
            new StoreValueChangeAction(),
            StoreAttributeAction.instance,
            new SwitchPmAction(),
            new CustomAction(), // just to have also some application-specific action
        ].each { it.registerIn receiver.registry }
    }
}

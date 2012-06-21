package com.canoo.dolphin.demo

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

import com.canoo.dolphin.core.server.comm.Receiver
import com.canoo.dolphin.core.server.action.StoreAttributeAction
import com.canoo.dolphin.core.server.action.StoreValueChangeAction
import com.canoo.dolphin.core.server.action.SwitchPmAction
import com.canoo.dolphin.core.client.comm.ClientConnector
import javafx.application.Platform

class InMemoryConfig {

    Receiver receiver = new Receiver()

    InMemoryConfig() {
        LogConfig.logCommunication()
        connector.sleepMillis = 100
        connector.receiver = receiver
        connector.howToProcessInsideUI = { Platform.runLater it }
    }

    ClientConnector getConnector() { InMemoryClientConnector.instance }

    void withActions() {
        [
            new StoreValueChangeAction(),
            StoreAttributeAction.instance,
            new SwitchPmAction(),
            new CustomAction(), // just to have also some application-specific action
        ].each { register it }
    }

    void register(action) {
        action.registerIn receiver.registry
    }
}

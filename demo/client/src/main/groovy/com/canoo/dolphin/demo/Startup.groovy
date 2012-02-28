package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import com.canoo.dolphin.LogConfig

class Startup {

    static void bootstrap() {
        LogConfig.logCommunication()
        InMemoryClientConnector.instance.receiver = [receive: {}]
    }

}

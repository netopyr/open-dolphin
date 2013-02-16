package com.canoo.dolphin.demo

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.client.comm.HttpClientConnector
import com.canoo.dolphin.core.comm.JsonCodec

class StarterUtil {

    static ClientDolphin setupForRemote() {
        LogConfig.logCommunication()
        def dolphin = new ClientDolphin()
        dolphin.setClientModelStore(new ClientModelStore(dolphin))
        def url = System.properties.remote ?: "http://localhost:8080/dolphin-grails"
        println " connecting to  $url "
        println "use -Dremote=... to override"
        def connector = new HttpClientConnector(dolphin, url)
        connector.codec = new JsonCodec()
        dolphin.clientConnector = connector
        return dolphin
    }
}

package org.opendolphin.demo

import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.HttpClientConnector
import org.opendolphin.core.comm.JsonCodec

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

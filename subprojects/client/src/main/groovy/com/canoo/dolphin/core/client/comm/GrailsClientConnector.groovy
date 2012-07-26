package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.comm.Command
import groovy.util.logging.Log
import groovy.json.JsonSlurper

@Log
class GrailsClientConnector extends ClientConnector {

    String baseUrl = "http://localhost:8080/dolphin-grails"

    int getPoolSize() { 1 }

    List<Command> transmit(Command command) {
        def result = new LinkedList<Command>()
        try {
            def url = "$baseUrl/command/${command.id}?"
            def props = command.properties.keySet() - ['id', 'class', 'metaClass']
            url += props.collect { "$it=${command[it]}" }.join('&') // needs URLEncode for the general case
            def response = url.toURL().text
            def got = new JsonSlurper().parseText(response)
            got.each { cmd ->
                Command responseCommand = Class.forName(cmd['class']).newInstance()
                cmd.each { key, value ->
                    if (key in ['id','class']) return
                    if (key == 'attributeId') value = value.toLong()
                    responseCommand[key] = value
                }
                result << responseCommand
            }
        }
        catch (ex) {
            log.severe("cannot transmit")
            ex.printStackTrace()
        }
        return result
    }
}

package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.comm.InitializeAttributeCommand
import groovy.util.logging.Log
import groovy.json.JsonSlurper
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient

@Log
class HttpClientConnector extends ClientConnector {

    String baseUrl = "http://localhost:8080/dolphin-grails"

    private DefaultHttpClient httpClient = new DefaultHttpClient()
    private ResponseHandler responseHandler = new BasicResponseHandler()

    int getPoolSize() { 1 }

    HttpClientConnector(ClientDolphin clientDolphin, String baseUrl) {
        super(clientDolphin)
        this.baseUrl = baseUrl
    }

    List<Command> transmit(CreatePresentationModelCommand command) {
        def result = new LinkedList()
        for(att in command.attributes) {
            result.addAll transmit(new AttributeCreatedCommand(
                pmId:           command.pmId,
                attributeId:    att.id,
                propertyName:   att.propertyName,
                newValue:       att.value,
                qualifier:      att.qualifier
                ))
        }
        return result
    }

    List<Command> transmit(Command command) {
        def result = new LinkedList<Command>()
        try {
            def url = "$baseUrl/dolphin/${command.id}?"
            def props = command.properties.keySet() - ['id', 'class', 'metaClass']
            url += props.collect { "$it=${command[it]}" }.join('&') // needs URLEncode for the general case

            HttpGet httpGet = new HttpGet(url)

            def response = httpClient.execute(httpGet,responseHandler)

            println response

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

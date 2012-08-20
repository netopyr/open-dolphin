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
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
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

    List<Command> transmit(Command command) {
        def result = new LinkedList<Command>()
        try {
            def url = "$baseUrl/dolphin/"

            def content = codec.encode([command])  // for the moment, there is only one command in the list


            HttpPost httpPost = new HttpPost(url)
            StringEntity entity = new StringEntity(content)
            httpPost.setEntity(entity)


            def response = httpClient.execute(httpPost,responseHandler)

            println response

            result = codec.decode(response)
        }
        catch (ex) {
            log.severe("cannot transmit")
            ex.printStackTrace()
        }
        return result
    }
}

/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.core.client.comm

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.comm.Command
import groovy.util.logging.Log
import org.apache.http.client.ResponseHandler
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

            log.finest response

            result = codec.decode(response)
        }
        catch (ex) {
            log.severe("cannot transmit")
            throw ex
        }
        return result
    }
}

/*
 * Copyright 2012-2013 Canoo Engineering AG.
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

    String servletUrl = "http://localhost:8080/dolphin-grails/dolphin/"

    private DefaultHttpClient httpClient = new DefaultHttpClient()
    private ResponseHandler responseHandler = new BasicResponseHandler()

    int getPoolSize() { 1 }

    HttpClientConnector(ClientDolphin clientDolphin, String servletUrl) {
        super(clientDolphin)
        this.servletUrl = servletUrl
    }

    List<Command> transmit(List<Command> commands) {
        def result
        try {
            def url = "$servletUrl"

            def content = codec.encode(commands)


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

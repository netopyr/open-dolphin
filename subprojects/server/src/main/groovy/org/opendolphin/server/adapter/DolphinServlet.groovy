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
package org.opendolphin.server.adapter

import groovy.util.logging.Log
import org.opendolphin.core.ModelStore
import org.opendolphin.core.comm.Codec
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.comm.ServerConnector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse

@Log
abstract class DolphinServlet extends HttpServlet {
    private static String DOLPHIN_ATTRIBUTE_ID = DolphinServlet.class.name

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        def dolphin = checkDolphinInSession(req)
        def requestJson = req.inputStream.text
        log.finest "received json: $requestJson"
        def commands = dolphin.serverConnector.codec.decode(requestJson)
        def results = new LinkedList()
        commands.each {
            log.finest "processing $it"
            results.addAll dolphin.serverConnector.receive(it)
        }
        def jsonResponse = dolphin.serverConnector.codec.encode(results)
        log.finest "sending json response: $jsonResponse"
        resp.outputStream << jsonResponse
        resp.outputStream.close()
    }

    private ServerDolphin checkDolphinInSession(HttpServletRequest request) {
        def session = request.session
        ServerDolphin dolphin = session.getAttribute(DOLPHIN_ATTRIBUTE_ID)
        if (!dolphin) {
            log.info "creating new dolphin for session $session.id"
            dolphin = new ServerDolphin(new ModelStore(), new ServerConnector(codec: codec))
            dolphin.registerDefaultActions()
            registerApplicationActions(dolphin)
            session.setAttribute(DOLPHIN_ATTRIBUTE_ID, dolphin)
        }
        return dolphin
    }

    protected Codec getCodec() {
        new JsonCodec()
    }

    protected abstract void registerApplicationActions(ServerDolphin serverDolphin)
}

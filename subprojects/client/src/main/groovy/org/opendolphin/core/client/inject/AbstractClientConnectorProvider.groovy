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

package org.opendolphin.core.client.inject

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.UiThreadHandler
import org.opendolphin.core.comm.Codec

import javax.inject.Inject
import javax.inject.Provider

abstract class AbstractClientConnectorProvider implements Provider<ClientConnector> {
    @Inject
    protected ClientDolphin clientDolphin

    @Inject
    protected UiThreadHandler uiThreadHandler

    @Inject
    protected Codec codec

    @Override
    ClientConnector get() {
        ClientConnector connector = instantiateClientConnector()
        connector.codec = codec
        connector.uiThreadHandler = uiThreadHandler
        clientDolphin.clientConnector = connector
        connector
    }

    protected abstract ClientConnector instantiateClientConnector()
}
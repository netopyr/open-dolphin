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

package org.opendolphin.demo
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.GetPresentationModelCommand
import org.opendolphin.core.comm.InitializeAttributeCommand

def config = new JavaFxInMemoryConfig()
def serverDolphin = config.serverDolphin
def clientDolphin = config.clientDolphin
clientDolphin.clientConnector.sleepMillis = 0

// first get only the ids of pms to display as raw data
serverDolphin.action "fullDataRequest", { cmd, response ->
    100000.times {
        response << new DataCommand(id:it)
    }
}

// lazy server callback
serverDolphin.serverConnector.registry.register(GetPresentationModelCommand) { GetPresentationModelCommand cmd, response ->
    response << new InitializeAttributeCommand(pmId: cmd.pmId, propertyName: "detail", newValue: "server: "+cmd.pmId, pmType: "LAZY")
}

LazyLoadingView.show clientDolphin
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

package org.opendolphin.demo

import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel

def config = new JavaFxInMemoryConfig()


config.serverDolphin.action "fillFirst", { cmd, response ->
    ServerPresentationModel pm1 = new ServerPresentationModel("First 1", [new ServerAttribute("value","even")])
    response << CreatePresentationModelCommand.makeFrom(pm1)
    ServerPresentationModel pm2 = new ServerPresentationModel("First 2", [new ServerAttribute("value","odd")])
    response << CreatePresentationModelCommand.makeFrom(pm2)
}

config.serverDolphin.action "fillRelation", { cmd, response ->
    [0,2,4,6,8].each {
        ServerPresentationModel pm = new ServerPresentationModel([
            new ServerAttribute("first", "even" ),
            new ServerAttribute("second", "Second $it" )
        ])
        pm.presentationModelType = "FirstSecondRelation"
        response << CreatePresentationModelCommand.makeFrom(pm)
    }
    [1,3,5,7,9].each {
        ServerPresentationModel pm = new ServerPresentationModel([
            new ServerAttribute("first", "odd" ),
            new ServerAttribute("second", "Second $it" )
        ])
        pm.presentationModelType = "FirstSecondRelation"
        response << CreatePresentationModelCommand.makeFrom(pm)
    }
}

DependentChoiceBoxView.show(config.clientDolphin)
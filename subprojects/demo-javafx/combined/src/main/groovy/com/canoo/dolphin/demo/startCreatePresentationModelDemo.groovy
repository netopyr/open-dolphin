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

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.server.ServerPresentationModel

dumpPm = { pm ->
    Map attrs = pm.attributes.inject([:]) { map, attr ->
        map[attr.propertyName] = attr.value
        map
    }
    println "${pm.id}:${pm.presentationModelType} ${attrs}"
}

counter = 0i
def config = new JavaFxInMemoryConfig()
config.serverDolphin.action('createNewPresentationModel') { cmd, response ->
    ServerPresentationModel model = config.serverDolphin.presentationModel([
            name: "Name-${counter}",
            lastname: "Lastname-${counter}",
    ], "pm-${counter++}", 'person')
    config.serverDolphin.createPresentationModel(response, model) { callbackResponse, pm ->
        dumpPm(pm)
    }
}
config.serverDolphin.action('dumpPresentationModels') { cmd, response ->
    println('=' * 80)
    config.serverDolphin.serverModelStore.listPresentationModels().each { dumpPm(it) }
    println('=' * 80)
}

new CreatePresentationModelView().show(config.clientDolphin)
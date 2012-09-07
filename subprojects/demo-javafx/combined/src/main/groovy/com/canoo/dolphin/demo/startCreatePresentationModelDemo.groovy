package com.canoo.dolphin.demo

import com.canoo.dolphin.core.server.ServerPresentationModel

dumpPm = { pm ->
    Map attrs = pm.attributes.inject([:]) { map, attr ->
        map[attr.propertyName] = attr.value
        map
    }
    Map metadata = pm.dataKeys.inject([:]) { map, key ->
        map[key] = pm.findData(key)
        map
    }
    println "${pm.id}:${pm.presentationModelType} ${attrs}; ${metadata}"
}

counter = 0i
def config = new JavaFxInMemoryConfig()
config.serverDolphin.action('createNewPresentationModel') { cmd, response ->
    ServerPresentationModel model = config.serverDolphin.presentationModel([
            name: "Name-${counter}",
            lastname: "Lastname-${counter}",
    ], "pm-${counter}", 'person')
    model.putData('entityId', counter++)
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
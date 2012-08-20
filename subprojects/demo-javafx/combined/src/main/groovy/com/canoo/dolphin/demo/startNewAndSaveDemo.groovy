package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel

def config = new JavaFxInMemoryConfig()
def dolphin = config.serverDolphin

dolphin.action "saveNewSelectedPerson", { cmd, List<Command> response ->
    def selectedPerson = dolphin.findPresentationModelById('selectedPerson')

    // here: store a new person domain object with the attributes from above and get a new persistent id in return
    def pmId = "person-1" // for demo purposes assume a fixed value

    def newAttributes = selectedPerson.attributes.collect {
        new ServerAttribute(propertyName: it.propertyName, initialValue: it.value, qualifier: "${pmId}.${it.propertyName}")
    }
    response << CreatePresentationModelCommand.makeFrom(new ServerPresentationModel(pmId, newAttributes))
}

new NewAndSaveView().show(config.clientDolphin)
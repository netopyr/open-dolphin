package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.PresentationModel

import com.canoo.dolphin.core.comm.InitializeAttributeCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.GetPresentationModelCommand

class PullPresentationModelValuesAction implements ServerAction {
    private final ModelStore modelStore

    PullPresentationModelValuesAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register GetPresentationModelCommand, { GetPresentationModelCommand command, response ->

            PresentationModel pm = modelStore.findPresentationModelById(command.pmId)
            pm.attributes.each {
                response << new InitializeAttributeCommand(pmId: pm.id, propertyName: it.propertyName, newValue: it.value)
            }
        }
    }
}


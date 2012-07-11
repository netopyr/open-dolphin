package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.BasePresentationModel
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.GetPmCommand
import com.canoo.dolphin.core.comm.InitializeAttributeCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class PullPmValuesAction implements ServerAction {
    private final ModelStore modelStore

    PullPmValuesAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register GetPmCommand, { GetPmCommand command, response ->

            BasePresentationModel pm = modelStore.findPresentationModelById(command.pmId)
            pm.attributes.each {
                response << new InitializeAttributeCommand(pmId: pm.id, propertyName: it.propertyName, newValue: it.value)
            }
        }
    }
}


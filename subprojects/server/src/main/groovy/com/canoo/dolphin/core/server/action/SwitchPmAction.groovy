package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.SwitchPmCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

/**
 * When receiving the instruction to switch presentation models, this switch
 * is run against the current store but not mirrored to the client.
 * It is assumed that when a client sends a switch, he takes care for updating
 * his local state himself (by using actualPm.syncWith(sourcePm)).
 * When a switch originates on the server, though, the server may still send
 * SwitchPmCommands to the client.
 */
class SwitchPmAction implements ServerAction {
    private final ModelStore modelStore

    SwitchPmAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register SwitchPmCommand, { SwitchPmCommand command, response ->
            def actualPm = modelStore.findPresentationModelById(command.pmId)
            def sourcePm = modelStore.findPresentationModelById(command.sourcePmId)

            actualPm.syncWith sourcePm
        }
    }
}


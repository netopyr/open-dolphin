package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.SwitchPresentationModelCommand

/**
 * When receiving the instruction to switch presentation models, this switch
 * is run against the current store but not mirrored to the client.
 * It is assumed that when a client sends a switch, he takes care for updating
 * his local state himself (by using actualPm.syncWith(sourcePm)).
 * When a switch originates on the server, though, the server may still send
 * SwitchPmCommands to the client.
 */
class SwitchPresentationModelAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register SwitchPresentationModelCommand, { SwitchPresentationModelCommand command, response ->
            def actualPm = serverDolphin.serverModelStore.findPresentationModelById(command.pmId)
            def sourcePm = serverDolphin.serverModelStore.findPresentationModelById(command.sourcePmId)

            actualPm.syncWith sourcePm
        }
    }
}


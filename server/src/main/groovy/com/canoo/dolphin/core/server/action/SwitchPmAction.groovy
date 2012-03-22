package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.comm.SwitchPmCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry

class SwitchPmAction {

    def registerIn(ActionRegistry registry) {
        registry.register SwitchPmCommand, { SwitchPmCommand command, response ->
            def actualPm = StoreAttributeAction.instance.modelStore[command.pmId]
            def sourcePm = StoreAttributeAction.instance.modelStore[command.sourcePmId]

            actualPm.syncWith sourcePm
            response << command     // mirror such that switch also happens on the client
        }
    }
}


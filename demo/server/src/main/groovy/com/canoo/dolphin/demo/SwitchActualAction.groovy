package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.comm.SwitchAttributeIdCommand

class SwitchActualAction {

    def impl = { sourcePmId, NamedCommand command, response ->
        def actual   = StoreAttributeAction.instance.modelStore['actualPm']
        def sourcePm = StoreAttributeAction.instance.modelStore[sourcePmId]

        actual.syncWith(sourcePm) { actAtt, sourceAtt ->
            response << new SwitchAttributeIdCommand(pmId: 'actualPm', propertyName: actAtt.propertyName, newId: sourceAtt.id)
        }
    }

    def registerIn(ActionRegistry registry) {
        registry.register 'ActualToPm1', impl.curry('First PM')
        registry.register 'ActualToPm2', impl.curry('Second PM')
    }
}

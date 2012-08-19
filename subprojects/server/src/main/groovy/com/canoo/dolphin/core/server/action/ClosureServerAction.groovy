package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.server.comm.ActionRegistry

/**
 * Groovy-friendly action handling
 */

class ClosureServerAction extends DolphinServerAction {
    final String name
    final Closure namedCommandHandler

    ClosureServerAction(String name, Closure namedCommandHandler) {
        this.name = name
        this.namedCommandHandler = namedCommandHandler
    }

    @Override
    void registerIn(ActionRegistry registry) {
        registry.register(name, namedCommandHandler)
    }

}

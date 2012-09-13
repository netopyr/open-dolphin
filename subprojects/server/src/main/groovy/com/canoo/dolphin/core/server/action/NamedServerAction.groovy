package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.server.comm.ActionRegistry
import com.canoo.dolphin.core.server.comm.NamedCommandHandler

/**
 * Java-friendly action handling
 */

class NamedServerAction extends DolphinServerAction {
    final String name
    final NamedCommandHandler namedCommandHandler

    NamedServerAction(String name, NamedCommandHandler namedCommandHandler) {
        this.name = name
        this.namedCommandHandler = namedCommandHandler
    }

    @Override
    void registerIn(ActionRegistry registry) {
        registry.register(name, namedCommandHandler)
    }
}

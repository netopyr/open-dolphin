package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command

// todo: think about inlining this into receiver and use get/setProperty to ease registration
class ActionRegistry {

    Map actions = new HashMap() // todo: think about proper sizing and synchronization needs

    /** silently overrides old commands **/
    void register(String commandId, Closure serverCommand) {
        actions.put commandId, serverCommand
    }
    void register(Class commandClass, Closure serverCommand) {
        register Command.idFor(commandClass), serverCommand
    }

    Closure getAt(String commandId){
        actions.get commandId
    }


}

package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command

// todo: think about inlining this into receiver and use get/setProperty to ease registration
class ActionRegistry {

    Map actions = new HashMap() // todo: think about proper sizing and synchronization needs

    void register(String commandId, Closure serverCommand) {
        actions.get(commandId, []) << serverCommand
    }

    void register(Class commandClass, Closure serverCommand) {
        register Command.idFor(commandClass), serverCommand
    }

    List<Closure> getAt(String commandId){
        actions.get commandId
    }

    void unregister(String commandId, Closure serverCommand) {
        List commandList = actions.get(commandId, [])
        commandList.remove serverCommand
    }

    void unregister(Class commandClass, Closure serverCommand) {
        unregister Command.idFor(commandClass), serverCommand
    }
}

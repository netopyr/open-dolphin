package com.canoo.dolphin.core.server.comm

class CommandRegistry {

    Map commandRegistry = new HashMap() // todo: think about proper sizing and synchronization needs

    /** silently overrides old commands **/
    void register(String commandId, ServerCommand serverCommand) {
        commandRegistry.put commandId, serverCommand
    }

    ServerCommand getAt(String commandId){
        commandRegistry.get commandId
    }


}

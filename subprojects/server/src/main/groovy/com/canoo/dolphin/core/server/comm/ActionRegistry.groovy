package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command

// todo: think about inlining this into receiver and use get/setProperty to ease registration
class ActionRegistry {

    Map<String, List<CommandHandler<Command>>> actions = [:] // todo: think about proper sizing and synchronization needs

    void register(String commandId, Closure serverCommand) {
        actions.get(commandId, []) << new CommandHandlerClosureAdapter(serverCommand)
    }

    void register(Class commandClass, Closure serverCommand) {
        register Command.idFor(commandClass), new CommandHandlerClosureAdapter(serverCommand)
    }

    void register(String commandId, CommandHandler<Command> serverCommand) {
        actions.get(commandId, []) << serverCommand
    }

    void register(Class commandClass, CommandHandler<Command> serverCommand) {
        register Command.idFor(commandClass), serverCommand
    }

    List<CommandHandler<Command>> getAt(String commandId){
        actions.get commandId
    }

    void unregister(String commandId, Closure serverCommand) {
        List<CommandHandler<Command>> commandList = actions.get(commandId, [])
        commandList.remove new CommandHandlerClosureAdapter(serverCommand)
    }

    void unregister(Class commandClass, Closure serverCommand) {
        unregister Command.idFor(commandClass), new CommandHandlerClosureAdapter(serverCommand)
    }

    void unregister(String commandId, CommandHandler<Command> serverCommand) {
        List<CommandHandler<Command>> commandList = actions.get(commandId, [])
        commandList.remove serverCommand
    }

    void unregister(Class commandClass, CommandHandler<Command> serverCommand) {
        unregister Command.idFor(commandClass), serverCommand
    }
}

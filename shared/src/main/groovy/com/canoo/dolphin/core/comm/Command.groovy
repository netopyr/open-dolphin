package com.canoo.dolphin.core.comm

/**
 * Commands are merely a description of what should happen on the other side.
 * They are sent between client and server.
 * They do not themselves contain any logic of how to execute.
 * This is done by the actions that are registered for this command.
 */
class Command {

    String getId() { idFor this.class }

    static idFor(Class commandClass) {
        commandClass.name - commandClass.package.name - "." - "Command"
    }

    String toString() { "Command: $id" }
}

package com.canoo.dolphin.core.comm

// todo dk: go through all subtypes and apply the new naming convention

/**
 * Commands come in two flavors: *Command (active voice) and *Notification (passive voice).
 * A *Command instructs the other side to do something.
 * A *Notification informs the other side that something has happened.
 * Typically, the server sends commands to the client,
 * the client sends notifications to the server with the notable exception of NamedCommand.
 * Neither commands nor notifications contain any logic themselves.
 * They are only "DTOs" that are sent over the wire.
 * The receiving side is responsible for finding the appropriate action.
 */

class Command {

    String getId() { idFor this.class }

    // todo dk: also remove "Notification"
    static idFor(Class commandClass) {
        commandClass.name - commandClass.package.name - "." - "Command"
    }

    String toString() { "Command: $id" }
}

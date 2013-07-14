package org.opendolphin.core.client.comm

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.EmptyNotification
import org.opendolphin.core.comm.NamedCommand

class CommandAndHandler {
    Command command
    OnFinishedHandler handler

    /** whether this command/handler can be batched */
    boolean isBatchable() {
        if (handler)                                return false
        if (command instanceof NamedCommand)        return false
        if (command instanceof EmptyNotification)   return false
        true
    }
}

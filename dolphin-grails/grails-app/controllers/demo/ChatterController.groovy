package demo

import org.opendolphin.core.server.EventBus

/**
 * This class is deprecated and no longer needed with version 0.9 and the ChatterRelease action.
 * It is left in the Grails distribution to accommodate older clients that connect to the chat server.
 */
class ChatterController {

    EventBus chatterBus

    def release() {
        chatterBus.publish(null, [type: "release"])
        render text:"done"
    }
}

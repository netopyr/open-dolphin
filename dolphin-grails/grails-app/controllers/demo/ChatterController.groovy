package demo

import org.opendolphin.core.server.EventBus

class ChatterController {

    EventBus chatterBus

    def release() {
        chatterBus.publish(null, [type: "release"])
        render text:"done"
    }
}

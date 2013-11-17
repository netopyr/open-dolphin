package dolphin.grails

import org.opendolphin.core.server.EventBus

class SetVelocityController {

    EventBus tachoBus

    def set(int value) {
        value = value.abs() % 100
        tachoBus.publish(null, value)
        render text:value
    }
}

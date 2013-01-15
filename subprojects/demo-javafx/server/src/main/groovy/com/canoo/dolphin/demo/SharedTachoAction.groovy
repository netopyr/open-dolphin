package com.canoo.dolphin.demo

import com.canoo.dolphin.core.Tag
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.EventBus
import com.canoo.dolphin.core.server.action.DolphinServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowQueue

import java.util.concurrent.TimeUnit

@Log
class SharedTachoAction extends DolphinServerAction {

    private EventBus tachoBus
    private final DataflowQueue speedQueue = new DataflowQueue()

    private final int EVENT_PROVIDER_WAIT_MS = 500
    private final int EVENT_CONSUMER_WAIT_MS = 5000
    private int waitMillis = EVENT_CONSUMER_WAIT_MS

    SharedTachoAction subscribedTo(EventBus tachoBus) {
        this.tachoBus = tachoBus
        tachoBus.subscribe(speedQueue)
        return this
    }

    void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            def attribute = modelStore.findAttributeById(command.attributeId)
            if (attribute) {
                if (attribute.qualifier == "train.speed") {
                    def value = command.newValue//.toInteger()
                    tachoBus.publish(speedQueue, value)
                    log.info "published train speed $value"
                    waitMillis = EVENT_PROVIDER_WAIT_MS
                }
            }
        }

        registry.register("poll.train.speed") { NamedCommand command, response ->

            def modelStore = serverDolphin.serverModelStore
            def attribute = modelStore.findAllAttributesByQualifier("train.speed").find{ it.tag == Tag.VALUE }
            if (! attribute) return

            def speed = speedQueue.getVal(waitMillis, TimeUnit.MILLISECONDS)    // take the last value
            def lastSpeed = speed
            while (null != speed) {
                lastSpeed = speed
                speed = speedQueue.getVal(20, TimeUnit.MILLISECONDS)
            }
            if (null != lastSpeed) {
                waitMillis = EVENT_CONSUMER_WAIT_MS
                log.info "got speed notification: $lastSpeed"
                serverDolphin.changeValue(response, attribute, lastSpeed)
            }
            return response
        }
    }
}

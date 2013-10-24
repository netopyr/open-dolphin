package org.opendolphin.demo

import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import java.util.concurrent.TimeUnit

@Log
class SmallFootprintAction extends DolphinServerAction {

    private EventBus smallFootprintBus
    private final DataflowQueue rectQueue = new DataflowQueue()

    SmallFootprintAction subscribedTo(EventBus smallFootprintBus) {
        this.smallFootprintBus = smallFootprintBus
        smallFootprintBus.subscribe(rectQueue)
        return this
    }

    void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            def attribute = modelStore.findAttributeById(command.attributeId)
            if (attribute) {
                if (attribute.qualifier == "sfTrigger") {
                    def value = command.newValue
                    smallFootprintBus.publish(rectQueue, value)
                }
            }
        }

        registry.register("poll.sfTrigger") { NamedCommand command, response ->
            def max = 20

            def value = rectQueue.getVal(1, TimeUnit.MINUTES)    // typical long-poll wait
            while (null != value) { // for efficiency read all there is until quiet
                response << new DataCommand(value: value)
                if (! max--) break
                value = rectQueue.getVal(20, TimeUnit.MILLISECONDS) // quiet time
            }
            return response
        }
    }
}

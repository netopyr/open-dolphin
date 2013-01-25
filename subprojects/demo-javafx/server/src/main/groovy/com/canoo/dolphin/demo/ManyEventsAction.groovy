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
class ManyEventsAction extends DolphinServerAction {

    private EventBus manyEventsBus
    private final DataflowQueue speedQueue = new DataflowQueue()

    private final int EVENT_CONSUMER_WAIT_MS = 5000
    private int waitMillis = EVENT_CONSUMER_WAIT_MS

    ManyEventsAction subscribedTo(EventBus manyEventsBus) {
        this.manyEventsBus = manyEventsBus
        manyEventsBus.subscribe(speedQueue)
        return this
    }

    void registerIn(ActionRegistry registry) {

        registry.register("many.events") { NamedCommand command, response ->

            def atSpeed = serverDolphin.getAt("ManyEvents")?.getAt("speed")
            if (! atSpeed) return
            def atColor = serverDolphin.getAt("ManyEvents")?.getAt("color")
            if (! atColor) return

            def post = speedQueue.getVal(waitMillis, TimeUnit.MILLISECONDS)    // return all values
            while (null != post) {
                def (speed, color) = post
                log.info "got speed notification: $speed"
                changeValue(atSpeed, speed)
                changeValue(atColor, color)
                post = speedQueue.getVal(20, TimeUnit.MILLISECONDS)
            }
            log.info "returning speed data"
            return response
        }
    }
}

package demo

import org.opendolphin.core.server.EventBus

class ManyEventsController {

    EventBus manyEventsBus

    def index() {
        def soMany = params.times?.toInteger() ?: 10
        int color = 0
        soMany.times {
            for (speed in 1..100) {
                manyEventsBus.publish(null, [speed, color])
                sleep (params.sleep?.toInteger() ?: 20)
            }
            color++
        }
        render text:"done"
    }
}

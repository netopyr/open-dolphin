package com.canoo.dolphin.core.server

import groovyx.gpars.dataflow.DataflowQueue;
import spock.lang.Specification;

public class EventBusSpec extends Specification {

    void 'no notification without registration'() {
        given :
        def bus = new EventBus()
        def flowOne = new DataflowQueue()
        def flowTwo = new DataflowQueue()
        when:
        bus.publish(flowOne, 1)
        then:
        null == flowOne.poll()
        null == flowTwo.poll()
    }

    void 'receiver is notified once, sender is not notified on publish'() {
        given :
        def bus = new EventBus()
        def flowOne = new DataflowQueue()
        def flowTwo = new DataflowQueue()
        bus.subscribe(flowOne)
        bus.subscribe(flowTwo)
        when:
        bus.publish(flowOne, 1)
        then:
        null == flowOne.poll()
        1 == flowTwo.val
        null == flowTwo.poll()
    }

    void 'unsubscribe stops event notification'() {
        given :
        def bus = new EventBus()
        def flowOne = new DataflowQueue()
        def flowTwo = new DataflowQueue()
        bus.subscribe(flowOne)
        bus.subscribe(flowTwo)
        bus.publish(flowOne, 1)
        1 == flowTwo.val
        bus.unSubscribe(flowTwo)
        when:
        bus.publish(flowOne, 2)
        then:
        null == flowOne.poll()
        null == flowTwo.poll()
    }
}

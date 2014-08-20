package org.opendolphin.core.server

import groovyx.gpars.dataflow.DataflowQueue
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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

    void 'null-safe protection'() {
        given:
        def nullRefs = []
        when:
        def done = EventBus.nullProtectionDone(null, 'x', nullRefs)
        then:
        done == true
        'x' in nullRefs

        when:
        done = EventBus.nullProtectionDone('not-null', 'y', nullRefs)
        then:
        done == false
        ! ('y' in nullRefs)
    }

    void 'memory leak protection'() {
        given: "an event bus with maxQueueLength of 1"
        def bus  = new EventBus(1)
        def flow = new DataflowQueue()
        def done = new CountDownLatch(10)
        flow.wheneverBound {
            assert flow.length() < 2
            done.countDown()
        }
        bus.subscribe(flow)
        when: "we publish values 0..9"
        10.times { bus.publish(null, it) }
        then: "the queue does not overflow, the max length is retained and least recent values are gone"
        done.await(2, TimeUnit.SECONDS)
        flow.val == 9
        null == flow.poll()
    }
}
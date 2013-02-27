/*
 * Copyright 2012-2013 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.core.server

import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.DataflowQueue

import java.lang.ref.WeakReference

/**
 * A classical event bus for the publish-subscribe pattern.
 * It is thread-safe and can be used across multiple server sessions,
 * e.g. as a Spring Bean with scope="singleton".
 * Subscribers should properly unsubscribe but if they forget to do so,
 * they will be unsubscribed automatically when their reference is
 * garbage-collected.
 */
class EventBus {

    private final Agent subscribers = Agent.agent(new LinkedList())

    void subscribe(DataflowQueue queue){
        assert queue
        subscribers { it << new WeakReference(queue) }
    }

    void unSubscribe(DataflowQueue queue){
        assert queue
        subscribers { List<WeakReference> list ->
            def receiver = list.findIndexOf { queue.is it.get() }
            if (receiver > -1) list.remove receiver
        }
    }

    /**
     * @param sender the DataflowQueue that is _not_ notified
     * @param value should be immutable
     */
    void publish(DataflowQueue sender, value) {
        subscribers { list ->
            def nullRefs = new LinkedList()
            for (queueRef in list) {
                def queue = queueRef.get()
                if (queue == null) {
                    nullRefs << queueRef
                    continue
                }
                if ( ! sender.is(queue)) queue << value
            }
            list.removeAll nullRefs
        }
    }
}

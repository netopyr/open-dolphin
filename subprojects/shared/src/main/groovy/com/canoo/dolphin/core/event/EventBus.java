package com.canoo.dolphin.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Collections.synchronizedList;

public class EventBus {
    private final List<ModelListener> listeners = synchronizedList(new ArrayList<ModelListener>());
    private final BlockingQueue<Runnable> deferredEvents = new LinkedBlockingQueue<Runnable>();

    private static final Object[] LOCK = new Object[0];
    private static int count = 1;

    private static int identifier() {
        synchronized (LOCK) {
            return count++;
        }
    }

    public EventBus() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        deferredEvents.take().run();
                    } catch (InterruptedException e) {
                        // ignore ?
                    }
                }
            }
        }, "EventBus-" + identifier()).start();
    }

    public void publishAsync(ModelEvent event) {
        deferredEvents.offer(buildPublisher(event));
    }

    public void publishSync(ModelEvent event) {
        buildPublisher(event).run();
    }

    private Runnable buildPublisher(final ModelEvent event) {
        return new Runnable() {
            public void run() {
                synchronized (listeners) {
                    for (ModelListener listener : listeners) {
                        if (listener.getPredicate().matches(event)) {
                            listener.handleEvent(event);
                        }
                    }
                }
            }
        };
    }

    public void addModelListener(ModelListener listener) {
        if (null == listener || listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void removeModelListener(ModelListener listener) {
        if (null == listener) return;
        listeners.remove(listener);
    }
}

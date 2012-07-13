package com.canoo.dolphin.core.event;

public interface ModelListener {
    EventPredicate getPredicate();

    void handleEvent(ModelEvent event);
}

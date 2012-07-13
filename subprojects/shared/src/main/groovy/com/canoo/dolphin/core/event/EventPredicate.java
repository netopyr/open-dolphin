package com.canoo.dolphin.core.event;

public interface EventPredicate {
    boolean matches(ModelEvent event);
}

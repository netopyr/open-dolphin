package com.canoo.dolphin.core.server.action;

import com.canoo.dolphin.core.server.comm.ActionRegistry;

public interface ServerAction {
    void registerIn(ActionRegistry registry);
}

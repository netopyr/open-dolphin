package com.canoo.dolphin.core.server.action;

import com.canoo.dolphin.core.comm.Command;
import com.canoo.dolphin.core.comm.NamedCommand;
import com.canoo.dolphin.core.server.ServerPresentationModel;

import java.util.List;

public interface CreatePresentationModelHandler {
    public void call(List<Command> response, ServerPresentationModel presentationModel);
}

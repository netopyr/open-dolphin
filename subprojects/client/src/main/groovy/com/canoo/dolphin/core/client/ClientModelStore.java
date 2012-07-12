package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.client.comm.ClientConnector;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;

public class ClientModelStore extends ModelStore {
    private final ClientConnector communicator;

    public ClientModelStore(ClientConnector communicator) {
        this.communicator = communicator;
        this.communicator.setClientModelStore(this);
    }

    public ClientConnector getCommunicator() {
        return communicator;
    }

    @Override
    public boolean add(PresentationModel model) {
        boolean success = super.add(model);
        if (success) {
            communicator.send(new CreatePresentationModelCommand(model));
        }

        return success;
    }
}

package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.Attribute;
import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.client.comm.ClientConnector;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;

import java.util.List;

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
            /*

            TODO: handle attribute registration with the current communicator

            List<Attribute> attributes = model.getAttributes();
            synchronized (attributes) {
                for (Attribute attribute : attributes) {
                    attribute.addPropertyChangeListener("value", communicator);
                }
            }
            */
            communicator.send(new CreatePresentationModelCommand(model));
        }

        return success;
    }

    public void updateAttributeId(Attribute attribute, long id) {
        removeAttributeById(attribute);
        attribute.setId(id);
        addAttributeById(attribute);
    }
}

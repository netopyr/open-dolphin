package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.Attribute;
import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;

import java.util.List;

public class ClientModelStore extends ModelStore {
    @Override
    public boolean add(PresentationModel model) {
        boolean success = super.add(model);
        if (success) {
            List<Attribute> attributes = model.getAttributes();
            synchronized (attributes) {
                for (Attribute attribute : attributes) {
                    attribute.addPropertyChangeListener("value", Dolphin.getClientConnector());
                }
            }
            Dolphin.getClientConnector().send(new CreatePresentationModelCommand(model));
        }

        return success;
    }

    @Override
    public void registerAttribute(Attribute attribute) {
        super.registerAttribute(attribute);
        attribute.addPropertyChangeListener("value", Dolphin.getClientConnector());
    }

    public void updateAttributeId(Attribute attribute, long id) {
        removeAttributeById(attribute);
        attribute.setId(id);
        addAttributeById(attribute);
        attribute.addPropertyChangeListener("value", Dolphin.getClientConnector());
    }
}

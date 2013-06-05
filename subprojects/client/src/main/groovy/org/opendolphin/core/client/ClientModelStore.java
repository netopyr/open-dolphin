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

package org.opendolphin.core.client;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.ModelStore;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;
import org.opendolphin.core.client.comm.WithPresentationModelHandler;
import org.opendolphin.core.comm.*;

import java.util.LinkedList;
import java.util.List;

public class ClientModelStore extends ModelStore {
    private final ClientDolphin clientDolphin;

    public ClientModelStore(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
    }

    protected ClientConnector getClientConnector() {
        return clientDolphin.getClientConnector();
    }

    @Override
    public boolean add(PresentationModel model) {
        boolean success = super.add(model);
        if (success) {
            List<Attribute> attributes = model.getAttributes();
            for (Attribute attribute : attributes) {
                attribute.addPropertyChangeListener(getClientConnector());
            }
            getClientConnector().send(CreatePresentationModelCommand.makeFrom(model));
        }

        return success;
    }

    public boolean addClientSideOnly(PresentationModel model) {
        return super.add(model);
    }

    @Override
    public boolean remove(PresentationModel model) {
        boolean success = super.remove(model);
        for (Attribute attribute : model.getAttributes()) {
            attribute.removePropertyChangeListener(getClientConnector());
        }
        return success;
    }

    @Override
    public void registerAttribute(Attribute attribute) {
        super.registerAttribute(attribute);
        attribute.addPropertyChangeListener(getClientConnector());
    }

    public void withPresentationModel(final String requestedPmId, final WithPresentationModelHandler withPmHandler) {
        ClientPresentationModel result = (ClientPresentationModel) findPresentationModelById(requestedPmId);
        if (result != null) {
            withPmHandler.onFinished(result);
            return;
        }

        GetPresentationModelCommand cmd = new GetPresentationModelCommand();
        cmd.setPmId(requestedPmId);

        OnFinishedHandler callBack = new OnFinishedHandlerAdapter() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                ClientPresentationModel theOnlyOne = presentationModels.get(0);
                assert theOnlyOne.getId().equals(requestedPmId); // sanity check
                withPmHandler.onFinished(theOnlyOne);
            }
        };
        getClientConnector().send(cmd, callBack);
    }

    public void delete(ClientPresentationModel model) {
        delete(model, true);
    }

    public void delete(ClientPresentationModel model, boolean notify) {
        if (model == null) return;
        if (containsPresentationModel(model.getId())) {
            remove(model);
            if (!notify) return;
            if (model.isClientSideOnly()) return;
            getClientConnector().send(new DeletedPresentationModelNotification(model.getId()));
        }
    }

    public void deleteAllPresentationModelsOfType(String presentationModelType) {
        List<PresentationModel> models = new LinkedList<PresentationModel>(findAllPresentationModelsByType(presentationModelType));
        for (PresentationModel model: models) {
            delete(((ClientPresentationModel) model), false);
        }
        getClientConnector().send(new DeletedAllPresentationModelsOfTypeNotification(presentationModelType));
    }

}

/*
 * Copyright 2012 Canoo Engineering AG.
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

package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.Attribute;
import com.canoo.dolphin.core.Link;
import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.client.comm.ClientConnector;
import com.canoo.dolphin.core.client.comm.OnFinishedHandler;
import com.canoo.dolphin.core.client.comm.WithPresentationModelHandler;
import com.canoo.dolphin.core.comm.*;

import java.util.*;

public class ClientModelStore extends ModelStore {
    private final Map<String, Set<PresentationModelListChangedListener>> pmType2Listeners = new HashMap<String, Set<PresentationModelListChangedListener>>();

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
            notifyAdded((ClientPresentationModel) model);
            getClientConnector().send(CreatePresentationModelCommand.makeFrom(model));
        }

        return success;
    }

    @Override
    public boolean remove(PresentationModel model) {
        boolean success = super.remove(model);
        for (Attribute attribute : model.getAttributes()) {
            attribute.removePropertyChangeListener(getClientConnector());
        }
        if (success) {
            notifyRemoved((ClientPresentationModel) model);
        }
        return success;
    }

    @Override
    public void registerAttribute(Attribute attribute) {
        super.registerAttribute(attribute);
        attribute.addPropertyChangeListener(getClientConnector());
    }

    public void updateAttributeId(Attribute attribute, long id) {
        removeAttributeById(attribute);
        attribute.setId(id);
        addAttributeById(attribute);
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

        OnFinishedHandler callBack = new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                ClientPresentationModel theOnlyOne = presentationModels.get(0);
                assert theOnlyOne.getId().equals(requestedPmId); // sanity check
                withPmHandler.onFinished(theOnlyOne);
            }
        };
        getClientConnector().send(cmd, callBack);
    }

    public void onPresentationModelListChanged(String pmType, PresentationModelListChangedListener listener) {
        Set<PresentationModelListChangedListener> set = pmType2Listeners.get(pmType);
        if (set == null) {
            set = new HashSet<PresentationModelListChangedListener>();
            pmType2Listeners.put(pmType, set);
        }
        set.add(listener);
    }

    protected void notifyAdded(final ClientPresentationModel model) {
        notifyChanged(model, new ListenerAction() {
            @Override
            public void doIt(PresentationModelListChangedListener listener) {
                listener.added(model);
            }
        });

    }

    protected void notifyRemoved(final ClientPresentationModel model) {
        notifyChanged(model, new ListenerAction() {
            @Override
            public void doIt(PresentationModelListChangedListener listener) {
                listener.removed(model);
            }
        });
    }

    private void notifyChanged(ClientPresentationModel model, ListenerAction doIt) {
        final String type = model.getPresentationModelType();
        if (type == null) {
            return;
        }
        Set<PresentationModelListChangedListener> set = pmType2Listeners.get(type);
        if (set == null) {
            return;
        }
        for (PresentationModelListChangedListener listener : set) {
            doIt.doIt(listener);
        }
    }

    public void save(String modelId) {
        save(findPresentationModelById(modelId));
    }

    public void save(PresentationModel model) {
        if (model == null) return;
        if (!containsPresentationModel(model.getId())) {
            add(model);
        }
        getClientConnector().send(new SavePresentationModelCommand(model.getId()));
    }

    public void delete(String modelId) {
        delete(findPresentationModelById(modelId));
    }

    public void delete(PresentationModel model) {
        if (model == null) return;
        if (containsPresentationModel(model.getId())) {
            remove(model);
            getClientConnector().send(new DeletePresentationModelCommand(model.getId()));
        }
    }

    private interface ListenerAction {
        void doIt(PresentationModelListChangedListener listener);
    }

    @Override
    public Link link(PresentationModel a, PresentationModel b, String type) {
        if (null == type || !containsPresentationModel(a) || !containsPresentationModel(b)) {
            return null;
        }

        Link link = null;
        if (!linkExists(a, b, type)) {
            link = super.link(a, b, type);
            getClientConnector().send(new AddPresentationModelLinkCommand(a.getId(), b.getId(), type));
        } else {
            link = findLink(a, b, type);
        }
        return link;
    }

    @Override
    public boolean unlink(PresentationModel a, PresentationModel b, String type) {
        boolean linkWasRemoved = super.unlink(a, b, type);
        if (linkWasRemoved) {
            getClientConnector().send(new RemovePresentationModelLinkCommand(a.getId(), b.getId(), type));
        }
        return linkWasRemoved;
    }

    @Override
    public boolean unlink(Link link) {
        boolean linkWasRemoved = super.unlink(link);
        if (linkWasRemoved) {
            getClientConnector().send(new RemovePresentationModelLinkCommand(link.getStart().getId(), link.getEnd().getId(), link.getType()));
        }
        return linkWasRemoved;
    }

    @Override
    protected boolean unlink(PresentationModel model) {
        List<Link> links = findAllLinksByModel(model);
        for (Link link : links) {
            getClientConnector().send(new RemovePresentationModelLinkCommand(link.getStart().getId(), link.getEnd().getId(), link.getType()));
        }
        return super.unlink(model);
    }
}

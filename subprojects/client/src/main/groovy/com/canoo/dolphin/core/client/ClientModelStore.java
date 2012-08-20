package com.canoo.dolphin.core.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.canoo.dolphin.core.Attribute;
import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.client.comm.ClientConnector;
import com.canoo.dolphin.core.client.comm.OnFinishedHandler;
import com.canoo.dolphin.core.client.comm.WithPresentationModelHandler;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;
import com.canoo.dolphin.core.comm.GetPresentationModelCommand;
import com.canoo.dolphin.core.comm.SavePresentationModelCommand;

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
                attribute.addPropertyChangeListener("value", getClientConnector());
            }
            notifyAdded((ClientPresentationModel) model);
            getClientConnector().send(CreatePresentationModelCommand.makeFrom(model));
        }

		return success;
	}

	@Override
	public boolean remove(PresentationModel model) {
		boolean success = super.remove(model);
		if (success){
			notifyRemoved((ClientPresentationModel)model);
		}
		return success;
	}

	@Override
	public void registerAttribute(Attribute attribute) {
		super.registerAttribute(attribute);
		attribute.addPropertyChangeListener("value", getClientConnector());
	}

	public void updateAttributeId(Attribute attribute, long id) {
		removeAttributeById(attribute);
		attribute.setId(id);
		addAttributeById(attribute);
		attribute.addPropertyChangeListener("value", getClientConnector());
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
		notifyChanged(model, new ListenerAction(){

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

	private interface ListenerAction {
		void doIt(PresentationModelListChangedListener listener);
	}
}

package com.canoo.dolphin.core.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.canoo.dolphin.core.Attribute;
import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;

public class ClientModelStore extends ModelStore {

	private final Map<String, Set<PresentationModelListChangedListener>> pmType2Listeners = new HashMap<String, Set<PresentationModelListChangedListener>>();

	@Override
	public boolean add(PresentationModel model) {
        boolean success = super.add(model);
        if (success) {
            List<Attribute> attributes = model.getAttributes();
            for (Attribute attribute : attributes) {
                attribute.addPropertyChangeListener("value", Dolphin.getClientConnector());
            }
            notifyAdded((ClientPresentationModel) model);
            Dolphin.getClientConnector().send(new CreatePresentationModelCommand(model));
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
		attribute.addPropertyChangeListener("value", Dolphin.getClientConnector());
	}

	public void updateAttributeId(Attribute attribute, long id) {
		removeAttributeById(attribute);
		attribute.setId(id);
		addAttributeById(attribute);
		attribute.addPropertyChangeListener("value", Dolphin.getClientConnector());
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

	private interface ListenerAction {
		void doIt(PresentationModelListChangedListener listener);
	}



}

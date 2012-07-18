package com.canoo.dolphin.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelStore {
    private final Map<String, PresentationModel> presentationModels = new ConcurrentHashMap<String, PresentationModel>();
    private final Map<Long, Attribute> attributesPerId = new ConcurrentHashMap<Long, Attribute>();
    private final Map<String, List<Attribute>> attributesPerDataId = new ConcurrentHashMap<String, List<Attribute>>();

    private final PropertyChangeListener ATTRIBUTE_WORKER = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            Attribute attribute = (Attribute) event.getSource();
            String oldDataId = (String) event.getOldValue();
            String newDataId = (String) event.getNewValue();

            if (null != oldDataId) removeAttributeByDataId(attribute, oldDataId);
            if (null != newDataId) addAttributeByDataId(attribute);
        }
    };

    public boolean add(PresentationModel model) {
        if (null == model) return false;

	    if(presentationModels.containsKey(model.getId())) {
		    throw new IllegalArgumentException("there already is a PM with id " + model.getId());
	    }
        boolean added = false;
        if (!presentationModels.containsValue(model)) {
            presentationModels.put(model.getId(), model);
            for (Attribute attribute : model.getAttributes()) {
                addAttributeById(attribute);
                attribute.addPropertyChangeListener(Attribute.DATA_ID_PROPERTY, ATTRIBUTE_WORKER);
                if (!isBlank(attribute.getDataId())) addAttributeByDataId(attribute);
            }
            added = true;
        }
        return added;
    }

    public boolean remove(PresentationModel model) {
        if (null == model) return false;
        boolean removed = false;
        if (presentationModels.containsValue(model)) {
            presentationModels.remove(model.getId());
            for (Attribute attribute : model.getAttributes()) {
                removeAttributeById(attribute);
                attribute.removePropertyChangeListener(Attribute.DATA_ID_PROPERTY, ATTRIBUTE_WORKER);
                if (!isBlank(attribute.getDataId())) removeAttributeByDataId(attribute);
            }
            removed = true;
        }
        return removed;
    }

    protected void addAttributeById(Attribute attribute) {
        if (null == attribute || attributesPerId.containsKey(attribute.getId())) return;
        attributesPerId.put(attribute.getId(), attribute);
    }

    protected void removeAttributeById(Attribute attribute) {
        if (null == attribute) return;
        attributesPerId.remove(attribute.getId());
    }

    protected void addAttributeByDataId(Attribute attribute) {
        if (null == attribute) return;
        String dataId = attribute.getDataId();
        if (isBlank(dataId)) return;
        List<Attribute> list = attributesPerDataId.get(dataId);
        if (null == list) {
            list = new ArrayList<Attribute>();
            attributesPerDataId.put(dataId, list);
        }
        if (!list.contains(attribute)) list.add(attribute);
    }

    protected void removeAttributeByDataId(Attribute attribute) {
        if (null == attribute) return;
        String dataId = attribute.getDataId();
        if (isBlank(dataId)) return;
        List<Attribute> list = attributesPerDataId.get(dataId);
        if (null != list) {
            list.remove(attribute);
        }
    }

    protected void removeAttributeByDataId(Attribute attribute, String dataId) {
        if (isBlank(dataId)) return;
        List<Attribute> list = attributesPerDataId.get(dataId);
        if (null == list) return;
        list.remove(attribute);
    }

    public PresentationModel findPresentationModelById(String id) {
        return presentationModels.get(id);
    }

    public boolean containsPresentationModel(String id) {
        return presentationModels.containsKey(id);
    }

    public Attribute findAttributeById(long id) {
        return attributesPerId.get(id);
    }

    public List<Attribute> findAllAttributesByDataId(String dataId) {
        if (isBlank(dataId) || !attributesPerDataId.containsKey(dataId)) return Collections.emptyList();
        return Collections.unmodifiableList(attributesPerDataId.get(dataId));
    }

    public void registerAttribute(Attribute attribute) {
        if (null == attribute) return;
        boolean listeningAlready = false;
        for (PropertyChangeListener listener : attribute.getPropertyChangeListeners(Attribute.DATA_ID_PROPERTY)) {
            if (ATTRIBUTE_WORKER == listener) {
                listeningAlready = true;
                break;
            }
        }

        if (!listeningAlready) {
            attribute.addPropertyChangeListener(Attribute.DATA_ID_PROPERTY, ATTRIBUTE_WORKER);
        }

        addAttributeByDataId(attribute);
        addAttributeById(attribute);
    }

    public static boolean isBlank(String str) {
        return null == str || str.trim().length() == 0;
    }
}

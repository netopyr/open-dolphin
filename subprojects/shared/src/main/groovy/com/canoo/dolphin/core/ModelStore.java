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

            if (null != oldDataId) removeAttributeByDataId(oldDataId);
            if (null != newDataId) addAttributeByDataId(attribute);
        }
    };

    public boolean add(PresentationModel model) {
        if (null == model) return false;
        boolean added = false;
        if (!presentationModels.containsValue(model)) {
            presentationModels.put(model.getId(), model);
            for (Attribute attribute : model.getAttributes()) {
                if (null == attribute.getDataId()) continue;
                addAttributeById(attribute);
                addAttributeByDataId(attribute);
                attribute.addPropertyChangeListener(Attribute.DATA_ID_PROPERTY, ATTRIBUTE_WORKER);
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
                if (null == attribute.getDataId()) continue;
                removeAttributeById(attribute);
                removeAttributeByDataId(attribute);
                attribute.removePropertyChangeListener(Attribute.DATA_ID_PROPERTY, ATTRIBUTE_WORKER);
            }
            removed = true;
        }
        return removed;
    }

    private void addAttributeById(Attribute attribute) {
        if (null == attribute) return;
        attributesPerId.put(attribute.getId(), attribute);
    }

    private void removeAttributeById(Attribute attribute) {
        if (null == attribute) return;
        attributesPerId.remove(attribute.getId());
    }

    private void addAttributeByDataId(Attribute attribute) {
        if (null == attribute) return;
        String dataId = attribute.getDataId();
        if (null == dataId) return;
        List<Attribute> list = attributesPerDataId.get(dataId);
        if (null == list) {
            list = new ArrayList<Attribute>();
            attributesPerDataId.put(dataId, list);
        }
        if (!list.contains(attribute)) list.add(attribute);
    }

    private void removeAttributeByDataId(Attribute attribute) {
        if (null == attribute) return;
        String dataId = attribute.getDataId();
        if (null == dataId) return;
        List<Attribute> list = attributesPerDataId.get(dataId);
        if (null != list) {
            list.remove(attribute);
        }
    }

    private void removeAttributeByDataId(String dataId) {
        if (null == dataId) return;
        List<Attribute> list = attributesPerDataId.get(dataId);
        if (null == list) return;
        Attribute attribute = null;
        for (Attribute attr : list) {
            if (dataId.equals(attr.getDataId())) {
                attribute = attr;
                break;
            }
        }
        if (null != attribute) list.remove(attribute);
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
        if (null == dataId || !attributesPerDataId.containsKey(dataId)) return Collections.emptyList();
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
}

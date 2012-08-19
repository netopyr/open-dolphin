package com.canoo.dolphin.core;

import groovy.lang.MissingPropertyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A BasePresentationModel is a collection of {@link BaseAttribute}s.
 * PresentationModels are not meant to be extended for the normal use, i.e. you typically don't need something like
 * a specialized "PersonPresentationModel" or so.
 */

public class BasePresentationModel extends AbstractObservable implements PresentationModel {
    protected final List<Attribute> attributes = new LinkedList<Attribute>();
    private final String id;
    private String presentationModelType;
    private boolean dirty = false;

    private final PropertyChangeListener DIRTY_FLAG_CHECKER = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            for (Attribute attr : attributes) {
                if (attr.isDirty()) {
                    setDirty(true);
                    return;
                }
            }
            setDirty(false);
        }
    };

    /**
     * @throws AssertionError if the list of attributes is null or empty  *
     */
    public BasePresentationModel(List<Attribute> attributes) {
        this(null, attributes);
    }

    /**
     * @throws AssertionError if the list of attributes is null or empty  *
     */
    public BasePresentationModel(String id, List<? extends Attribute> attributes) {
        this.id = id != null ? id : makeId(this);
        this.attributes.addAll(attributes);
        for (Attribute attr : attributes) {
            attr.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, DIRTY_FLAG_CHECKER);
        }
    }

    public String getId() {
        return id;
    }

    public String getPresentationModelType() {
        return presentationModelType;
    }

    public void setPresentationModelType(String presentationModelType) {
        this.presentationModelType = presentationModelType;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    private void setDirty(boolean dirty) {
        firePropertyChange(DIRTY_PROPERTY, this.dirty, this.dirty = dirty);
    }

    /**
     * @return the immutable internal representation
     */
    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    protected static String makeId(PresentationModel instance) {
        return String.valueOf(System.identityHashCode(instance));
    }

    public Attribute findAttributeByPropertyName(String propertyName) {
        if (null == propertyName) return null;
        for (Attribute attribute : attributes) {
            if (attribute.getPropertyName().equals(propertyName)) {
                return attribute;
            }
        }
        return null;
    }

    public Attribute findAttributeByQualifier(String qualifier) {
        if (null == qualifier) return null;
        for (Attribute attribute : attributes) {
            if (qualifier.equals(attribute.getQualifier())) {
                return attribute;
            }
        }
        return null;
    }

    public Attribute findAttributeById(long id) {
        for (Attribute attribute : attributes) {
            if (attribute.getId() == id) {
                return attribute;
            }
        }
        return null;
    }

    public Object propertyMissing(String propName) {
        Attribute result = findAttributeByPropertyName(propName);
        if (null == result) {
            throw new MissingPropertyException("The presentation model doesn't understand '" + propName + "'.", propName, this.getClass());
        }
        return result;
    }

    public void syncWith(PresentationModel sourcePresentationModel) {
        for (Attribute targetAttribute : attributes) {
            Attribute sourceAttribute = sourcePresentationModel.findAttributeByPropertyName(targetAttribute.getPropertyName());
            if (sourceAttribute != null) targetAttribute.syncWith(sourceAttribute);
        }
    }

    public void addAttribute(Attribute attribute) {
        if (null == attribute || attributes.contains(attribute)) return;
        this.attributes.add(attribute);
        attribute.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, DIRTY_FLAG_CHECKER);
    }
}
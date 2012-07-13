package com.canoo.dolphin.core;

import groovy.lang.MissingPropertyException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A BasePresentationModel (PM) is an non-empty, unmodifiable collection of {@link BaseAttribute}s.
 * This allows to bind against PMs (i.e. their Attributes) without the need for GRASP-like
 * PresentationModelSwitches.
 * PMs are not meant to be extended for the normal use, i.e. you typically don't need something like
 * a specialized "PersonPresentationModel" or so.
 */

public class BasePresentationModel implements PresentationModel {
    protected final List<Attribute> attributes = Collections.synchronizedList(new LinkedList<Attribute>());
    private final String id;
    private String presentationModelType;

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

    /**
     * @return the immutable internal representation
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    protected static String makeId(PresentationModel instance) {
        return String.valueOf(System.identityHashCode(instance));
    }

    public Attribute findAttributeByPropertyName(String propertyName) {
        if (null == propertyName) return null;
        synchronized (attributes) {
            for (Attribute attribute : attributes) {
                if (attribute.getPropertyName().equals(propertyName)) {
                    return attribute;
                }
            }
        }
        return null;
    }

    public Attribute findAttributeByDataId(String dataId) {
        if (null == dataId) return null;
        synchronized (attributes) {
            for (Attribute attribute : attributes) {
                if (dataId.equals(attribute.getDataId())) {
                    return attribute;
                }
            }
        }
        return null;
    }

    public Attribute findAttributeById(long id) {
        synchronized (attributes) {
            for (Attribute attribute : attributes) {
                if (attribute.getId() == id) {
                    return attribute;
                }
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
        synchronized (attributes) {
            for (Attribute targetAttribute : attributes) {
                Attribute sourceAttribute = sourcePresentationModel.findAttributeByPropertyName(targetAttribute.getPropertyName());
                if (sourceAttribute != null) targetAttribute.syncWith(sourceAttribute);
            }
        }
    }
}
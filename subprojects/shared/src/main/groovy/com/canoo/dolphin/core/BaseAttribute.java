package com.canoo.dolphin.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The value may be null as long as the BaseAttribute is used as a "placeholder".
 */

public abstract class BaseAttribute implements Attribute {
    private final String propertyName;
    private final PropertyChangeSupport pcs;

    private long id = System.identityHashCode(this); // todo: dk: has to change to tell client from server
    private String dataId; // application specific semantics apply

    public BaseAttribute(String propertyName) {
        this.propertyName = propertyName;
        pcs = new PropertyChangeSupport(this);
    }

    public String toString() {
        return new StringBuilder().append(id).append(" : ").append(propertyName).append(" (").append(dataId).append(")").toString();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        firePropertyChange(DATA_ID_PROPERTY, this.dataId, this.dataId = dataId);
    }

    public void syncWith(Attribute source) {
        if (this == source || null == source) return;
        setDataId(source.getDataId());
        setValue(source.getValue());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }
}

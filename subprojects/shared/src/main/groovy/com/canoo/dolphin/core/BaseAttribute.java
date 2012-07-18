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
    private Object value;
    private Object initialValue;
    private boolean dirty = false;

    private long id = System.identityHashCode(this); // todo: dk: has to change to tell client from server
    private String dataId; // application specific semantics apply

    public BaseAttribute(String propertyName) {
        this(propertyName, null);
    }

    public BaseAttribute(String propertyName, Object initialValue) {
        this.propertyName = propertyName;
        this.initialValue = initialValue;
        this.value = initialValue;
        pcs = new PropertyChangeSupport(this);
    }

    public boolean isDirty() {
        return dirty;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        firePropertyChange("value", this.value, this.value = value);
        setDirty(initialValue == null ? value != null : !initialValue.equals(value));
    }

    private void setDirty(boolean dirty) {
        firePropertyChange(DIRTY_PROPERTY, this.dirty, this.dirty = dirty);
    }

    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(" : ")
                .append(propertyName)
                .append(" (")
                .append(dataId).append(") ")
                .append(value).toString();
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
        if (listener == null || containsListener(listener, getPropertyChangeListeners(propertyName))) return;
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || containsListener(listener, getPropertyChangeListeners(propertyName))) return;
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
        if (event != null && event.getOldValue() == event.getNewValue()) return;
        pcs.firePropertyChange(event);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue == newValue) return;
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    private boolean containsListener(PropertyChangeListener listener, PropertyChangeListener[] listeners) {
        for (PropertyChangeListener subject : listeners) {
            if (subject == listener) return true;
        }
        return false;
    }
}

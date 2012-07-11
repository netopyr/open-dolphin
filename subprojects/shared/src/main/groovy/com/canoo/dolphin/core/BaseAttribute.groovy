package com.canoo.dolphin.core

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 The value may be null as long as the BaseAttribute is used as a "placeholder".
 */

abstract class BaseAttribute implements Attribute {

    long id = System.identityHashCode(this) // todo: dk: has to change to tell client from server
    final String propertyName
    String dataId // application specific semantics apply


    private final PropertyChangeSupport pcs

    abstract Object getValue()

    abstract void setValue(Object value)

    String getDataId() {
        return dataId
    }

    void setDataId(String dataId) {
        firePropertyChange(DATA_ID_PROPERTY, this.dataId, this.dataId = dataId)
    }

    BaseAttribute(String propertyName) {
        assert propertyName     // todo: think about using GContract for the precondition
        this.propertyName = propertyName
        pcs = new PropertyChangeSupport(this)
    }

    String toString() { "$id : $propertyName ($dataId)" }
    // more may come later

    void syncWith(BaseAttribute source) {
        assert source           // todo: think about using GContract for the precondition
        if (this.is(source)) return
        id = source.id
        setValue source.value // go through setter to make sure PCLs are triggered
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

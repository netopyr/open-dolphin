package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a ServerAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * PropertyChangeListener.
 */

class ClientAttribute extends BaseAttribute {
    private ObjectProperty value = new SimpleObjectProperty()

    ClientConnector communicator = InMemoryClientConnector.instance // todo: make configurable
    private final PropertyChangeSupport pcs

    ClientAttribute(String propertyName) {
        this(propertyName, null)
    }

    final Property valueProperty() {
        value
    }

    Object getValue() {
        value.get()
    }

    void setValue(Object newValue) {
        // todo: thread-safe concerns !!
        Object oldValue = this.value.get()
        value.set(newValue)
        firePropertyChange("value", oldValue, newValue)
    }

    ClientAttribute(String propertyName, initialValue) {
        super(propertyName)
        value.set(initialValue)
        pcs = new PropertyChangeSupport(this)
        addPropertyChangeListener 'value', communicator
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

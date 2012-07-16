package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a ServerAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * PropertyChangeListener that connects the ClientAttribute with a Connector.
 * One can bind against a ClientAttribute in two ways
 * a) as a PropertyChangeListener
 * b) through the valueProperty() method for JavaFx
 */

class ClientAttribute extends BaseAttribute {
    private ObjectProperty value = new SimpleObjectProperty()

    ClientConnector communicator = InMemoryClientConnector.instance // todo: make configurable

    ClientAttribute(String propertyName) {
        this(propertyName, null)
    }

    ClientAttribute(String propertyName, initialValue) {
        super(propertyName)
        value.set(initialValue)
        addPropertyChangeListener 'value', communicator
    }

    ClientAttribute(Map props) {
        this(props.propertyName, props.initialValue)
        this.dataId = props.dataId
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

    String toString() { "$id : $propertyName = ${getValue()}" }
}

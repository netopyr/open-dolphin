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
 * One can bind against a ClientAttribute in two ways
 * a) as a PropertyChangeListener
 * b) through the valueProperty() method for JavaFx
 */

class ClientAttribute extends BaseAttribute {
    ClientAttribute(String propertyName) {
        this(propertyName, null)
    }

    ClientAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    ClientAttribute(Map props) {
        this(props.propertyName, props.initialValue)
        this.dataId = props.dataId
    }
}

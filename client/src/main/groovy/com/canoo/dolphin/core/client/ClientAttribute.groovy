package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a server side ClientAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * PropertyChangeListener.
 */

class ClientAttribute extends BaseAttribute {

    ClientConnector communicator = InMemoryClientConnector.instance // todo: make configurable

    ClientAttribute(String propertyName) {
        super(propertyName)
        addPropertyChangeListener 'value', communicator
    }
}

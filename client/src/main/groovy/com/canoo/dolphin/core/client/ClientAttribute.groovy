package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BaseAttribute

import com.canoo.dolphin.core.client.comm.ClientCommunicator
import com.canoo.dolphin.core.client.comm.InMemoryClientCommunicator

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a server side ClientAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * PropertyChangeListener.
 */

class ClientAttribute extends BaseAttribute {

    ClientCommunicator communicator = InMemoryClientCommunicator.instance // todo: make configurable

    long id = System.identityHashCode(this)

    ClientAttribute(Class beanType, String propertyName) {
        super(beanType, propertyName)
        addPropertyChangeListener 'value', communicator
    }
    
    String toString() { "id: $id, $beanType : $propertyName" }
}

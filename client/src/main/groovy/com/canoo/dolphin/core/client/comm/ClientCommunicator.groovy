package com.canoo.dolphin.core.client.comm

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

abstract class ClientCommunicator implements PropertyChangeListener {

    Codec codec

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        log.info "sending -> ${evt.source} -> ${evt.newValue}"

        send constructValueChangedCommand(evt)
    }

    ValueChangedClientCommand constructValueChangedCommand(PropertyChangeEvent evt){
        new ValueChangedClientCommand(
                userId:       null,
                commandId:   "ValueChanged",
                attributeId: evt.source.id,
                oldValue:    evt.oldValue,
                newValue:    evt.newValue
                )
    }

    /**
    Implementations may choose to batch up send requests and sending commands immediately or later.
    They may react differently on various types of commands by overloading this method. (I love the Groovy)
    */
    abstract void send(ClientCommand command)
    
}

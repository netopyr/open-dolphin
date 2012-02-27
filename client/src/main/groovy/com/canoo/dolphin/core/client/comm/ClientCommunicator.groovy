package com.canoo.dolphin.core.client.comm

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.ConcurrentHashMap
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.comm.Command

abstract class ClientCommunicator implements PropertyChangeListener {

    Codec codec
    ConcurrentHashMap<String, ClientPresentationModel> modelStore = new ConcurrentHashMap<String, ClientPresentationModel>()// later, this may live somewhere else

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        log.info "sending -> ${evt.source} -> ${evt.newValue}"

        send constructValueChangedCommand(evt)
    }

    void register(String pmId, ClientPresentationModel cpModel){
        modelStore.put pmId, cpModel
    }

    void registerAndSend (String pmId, ClientPresentationModel cpm, ClientAttribute ca){
        register pmId, cpm
        send constructAttributeCreatedCommand(pmId, ca)
    }

    ValueChangedClientCommand constructValueChangedCommand(PropertyChangeEvent evt){
        new ValueChangedClientCommand(
                userId:       null,             // todo: set user id
                commandId:   "ValueChanged",
                attributeId: evt.source.id,
                oldValue:    evt.oldValue,
                newValue:    evt.newValue
                )
    }

    AttributeCreatedCommand constructAttributeCreatedCommand(String pmId, ClientAttribute attribute) {
        new AttributeCreatedCommand(
                userId:         null,
                pmId:           pmId,
                commandId:      "AttributeCreated",
                attributeId:    attribute.id,
                propertyName:   attribute.propertyName
        )
    }

    /**
    Implementations may choose to batch up send requests and sending commands immediately or later.
    They may react differently on various types of commands by overloading this method. (I love the Groovy)
    */
    abstract void send(Command command)
    
}

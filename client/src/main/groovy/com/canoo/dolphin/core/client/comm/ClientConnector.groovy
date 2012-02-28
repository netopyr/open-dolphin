package com.canoo.dolphin.core.client.comm

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.ConcurrentHashMap
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.comm.Command

import com.canoo.dolphin.core.comm.ValueChangedCommand
import groovy.util.logging.Log
import com.canoo.dolphin.core.comm.Codec

@Log
abstract class ClientConnector implements PropertyChangeListener {

    Codec codec
    ConcurrentHashMap<String, ClientPresentationModel> modelStore = new ConcurrentHashMap<String, ClientPresentationModel>()// later, this may live somewhere else

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        send constructValueChangedCommand(evt)
    }

    void register(String pmId, ClientPresentationModel cpModel){
        modelStore.put pmId, cpModel
    }

    void registerAndSend (String pmId, ClientPresentationModel cpm, ClientAttribute ca){
        register pmId, cpm
        send constructAttributeCreatedCommand(pmId, ca)
    }

    ValueChangedCommand constructValueChangedCommand(PropertyChangeEvent evt){
        new ValueChangedCommand(
                attributeId: evt.source.id,
                oldValue:    evt.oldValue,
                newValue:    evt.newValue
                )
    }

    AttributeCreatedCommand constructAttributeCreatedCommand(String pmId, ClientAttribute attribute) {
        new AttributeCreatedCommand(
                pmId:           pmId,
                attributeId:    attribute.id,
                propertyName:   attribute.propertyName
        )
    }

    abstract List<Command> transmit(Command command)

    void send(Command command) {
        log.info "C: transmitting $command"
        List<Command> response = transmit(command) // there is no need for encoding since we are in-memory
        log.info "C: server responded with ${response?.size()} command(s): ${response?.id}"

        for (serverCommand in response) {
            switch (serverCommand) {
                case ValueChangedCommand:
                    ClientAttribute ca = findClientAttributeById(serverCommand.attributeId)
                    if (ca){
                        log.info "C: updating attribute id '$serverCommand.attributeId' with value '$serverCommand.newValue'"
                        ca.value = serverCommand.newValue
                    } else {
                        log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
                    }
                    break
                // todo more cases to come
                default: log.warning "C: cannot handle $serverCommand"
            }
        }
    }

    ClientAttribute findClientAttributeById(long id) {
        modelStore.values().attributes.flatten().find { it.id == id } // todo: be more efficient
    }
    
}

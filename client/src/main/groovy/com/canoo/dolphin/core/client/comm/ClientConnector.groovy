package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.comm.AttributeCreatedCommand
import com.canoo.dolphin.core.comm.Codec
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.comm.ValueChangedCommand
import groovy.util.logging.Log

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.ConcurrentHashMap

@Log
abstract class ClientConnector implements PropertyChangeListener {

    Codec codec
    ConcurrentHashMap<String, ClientPresentationModel> modelStore = new ConcurrentHashMap<String, ClientPresentationModel>()// later, this may live somewhere else

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        send constructValueChangedCommand(evt)
    }

    void register(ClientPresentationModel cpModel) {
        modelStore.put cpModel.id, cpModel
    }

    void registerAndSend(ClientPresentationModel cpm, ClientAttribute ca) {
        register cpm
        send constructAttributeCreatedCommand(cpm.id, ca)
    }

    ValueChangedCommand constructValueChangedCommand(PropertyChangeEvent evt) {
        new ValueChangedCommand(
                attributeId: evt.source.id,
                oldValue: evt.oldValue,
                newValue: evt.newValue
        )
    }

    AttributeCreatedCommand constructAttributeCreatedCommand(String pmId, ClientAttribute attribute) {
        new AttributeCreatedCommand(
                pmId: pmId,
                attributeId: attribute.id,
                propertyName: attribute.propertyName
        )
    }

    abstract List<Command> transmit(Command command)

    List<ClientAttribute> findAllClientAttributesById(long id) {
        modelStore.values().attributes.flatten().findAll { it.id == id } // todo: be more efficient
    }

    void send(Command command) {
        log.info "C: transmitting $command"
        List<Command> response = transmit(command)
        log.info "C: server responded with ${ response?.size() } command(s): ${ response?.id }"

        for (serverCommand in response) {
            handle serverCommand
        }
    }

    def handle(Command serverCommand) {
        log.warning "C: cannot handle $serverCommand"
    }

    def handle(ValueChangedCommand serverCommand) {
        List<ClientAttribute> clientAttributes = findAllClientAttributesById(serverCommand.attributeId)
        if (!clientAttributes) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
            return
        }
        clientAttributes.findAll{ it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating attribute id '$serverCommand.attributeId' with value '$serverCommand.newValue'"
            outdated.value = serverCommand.newValue
        }
    }
}

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
import com.canoo.dolphin.core.comm.SwitchAttributeIdCommand
import com.canoo.dolphin.core.comm.SwitchPmCommand

@Log
abstract class ClientConnector implements PropertyChangeListener {

    Codec codec
    Map<String, ClientPresentationModel> modelStore = new ObservableMap<String, ClientPresentationModel>()// later, this may live somewhere else

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

    def handle(SwitchAttributeIdCommand serverCommand) {
        def sourceAtt = modelStore.values().attributes.flatten().find { it.id == serverCommand.newId } // one is enough
        if (!sourceAtt) {
            log.warning "C: attribute with id '$serverCommand.newId' not found, cannot switch"
            return
        }
        def switchPm = modelStore[serverCommand.pmId]
        if (!switchPm) {
            log.warning "C: pm with id '$serverCommand.pmId' not found, cannot switch"
            return
        }
        def switchAtt = switchPm[serverCommand.propertyName]
        if (!switchAtt) {
            log.warning "C: pm '$serverCommand.pmId' has no attribute of name '$serverCommand.propertyName'. Cannot switch"
            return
        }
        switchAtt.syncWith sourceAtt
    }

    def handle(SwitchPmCommand serverCommand) {
        def switchPm = modelStore[serverCommand.pmId]
        if (!switchPm) {
            log.warning "C: switch pm with id '$serverCommand.pmId' not found, cannot switch"
            return
        }
        def sourcePm = modelStore[serverCommand.sourcePmId]
        if (!sourcePm) {
            log.warning "C: source pm with id '$serverCommand.sourcePmId' not found, cannot switch"
            return
        }
        switchPm.syncWith sourcePm
    }

    def handle(AttributeCreatedCommand serverCommand){
        def attribute = new ClientAttribute(serverCommand.propertyName)
        if (!modelStore.containsKey(serverCommand.pmId)) {
            modelStore[serverCommand.pmId] = new ClientPresentationModel(serverCommand.pmId, [attribute])
            return
        }
        def pm = modelStore[serverCommand.pmId]
        pm.addAttribute(attribute)
    }

}

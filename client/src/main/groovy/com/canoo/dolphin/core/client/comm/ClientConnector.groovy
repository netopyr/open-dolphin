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
import java.util.concurrent.ConcurrentHashMap
import groovyx.gpars.GParsPool

import static groovyx.gpars.GParsPool.withPool
import groovyx.gpars.dataflow.Dataflow

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.DataflowVariable
import javafx.application.Platform
import groovyx.gpars.group.DefaultPGroup
import java.util.concurrent.CountDownLatch
import com.canoo.dolphin.core.comm.InitializeAttributeCommand

@Log
abstract class ClientConnector implements PropertyChangeListener {

    Codec codec

    def modelStore = new ClientModelStore().modelStore

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
                propertyName: attribute.propertyName,
                newValue: attribute.value
        )
    }

    abstract List<Command> transmit(Command command)

    abstract int getPoolSize()

    List<ClientAttribute> findAllClientAttributesById(long id) {
        modelStore.values().attributes.flatten().findAll { it.id == id } // todo: be more efficient
    }

    def group = new DefaultPGroup(poolSize)

    void send(Command command, Closure onFinished = null ) {
        def result = new DataflowVariable()
        processAsync {
            log.info "C: transmitting $command"
            result << transmit(command)
            insideUiThread {
                List<Command> response = result.get()
                log.info "C: server responded with ${ response?.size() } command(s): ${ response?.id }"

                Set<String> pmIds = []
                for (serverCommand in response) {
                    def pms = handle serverCommand
                    if (pms && pms in String) pmIds << pms
                }
                if (onFinished) onFinished pmIds
            }
        }
    }

    void processAsync(Closure processing) {
        group.task processing
    }

    void insideUiThread(Closure processing) {
        Platform.runLater processing
    }

    def handle(Command serverCommand, Set pmIds) {
        log.warning "C: cannot handle $serverCommand"
    }

    def handle(ValueChangedCommand serverCommand) {
        List<ClientAttribute> clientAttributes = findAllClientAttributesById(serverCommand.attributeId)
        if (!clientAttributes) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
            return
        }
        clientAttributes.findAll{ it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating '$outdated.propertyName' id '$serverCommand.attributeId' from '$outdated.value' to '$serverCommand.newValue'"
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
        serverCommand.pmId
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
        return serverCommand.pmId
    }

    def handle(InitializeAttributeCommand serverCommand){
        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue)
        transmit(new AttributeCreatedCommand(pmId: serverCommand.pmId, attributeId: attribute.id, propertyName: serverCommand.propertyName, newValue:serverCommand.newValue))

        if (!modelStore.containsKey(serverCommand.pmId)) {
            modelStore[serverCommand.pmId] = new ClientPresentationModel(serverCommand.pmId, [attribute])
            return serverCommand.pmId
        }
        def pm = modelStore[serverCommand.pmId]
        pm.addAttribute(attribute)
        return null // already there, no need to return it
    }

}

package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.group.DefaultPGroup
import javafx.application.Platform

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import com.canoo.dolphin.core.comm.*
import groovyx.gpars.group.PGroup

@Log
abstract class ClientConnector implements PropertyChangeListener {
    Codec codec

    ClientModelStore clientModelStore = new ClientModelStore()
    UiThreadHandler uiThreadHandler = new JavaFXUiThreadHandler()

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        send constructValueChangedCommand(evt)
        // also inform all other attributes of the same id. This may recurse into this method!
        List<ClientAttribute> clientAttributes = findAllClientAttributesById(evt.source.id)
        clientAttributes.remove { it.value == evt.newValue } // well, better be safe than sorry
        clientAttributes.each { it.value = evt.newValue }
    }

    void registerAndSend(ClientPresentationModel cpm, ClientAttribute ca) {
        clientModelStore.register(cpm)
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

    void switchPmAndSend(ClientPresentationModel switcher, ClientPresentationModel newSource) {
        switcher.syncWith newSource
        send new SwitchPmCommand(pmId: switcher.id, sourcePmId: newSource.id)
    }

    abstract List<Command> transmit(Command command)

    abstract int getPoolSize()

    List<ClientAttribute> findAllClientAttributesById(long id) {
        clientModelStore.findAllClientAttributesById(id)
    }

    PGroup group = new DefaultPGroup(poolSize)

    void send(Command command, Closure onFinished = null) {
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

    void processAsync(Runnable processing) {
        group.task processing
    }

    void insideUiThread(Runnable processing) {
        uiThreadHandler.executeInsideUiThread(processing)
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
        clientAttributes.findAll { it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating '$outdated.propertyName' id '$serverCommand.attributeId' from '$outdated.value' to '$serverCommand.newValue'"
            outdated.value = serverCommand.newValue
        }
    }

    def handle(SwitchAttributeIdCommand serverCommand) {
        def sourceAtt = clientModelStore.findFirstAttributeById(serverCommand.newId) // one is enough
        if (!sourceAtt) {
            log.warning "C: attribute with id '$serverCommand.newId' not found, cannot switch"
            return
        }
        def switchPm = clientModelStore.findPmById(serverCommand.pmId)
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
        def switchPm = clientModelStore.findPmById(serverCommand.pmId)
        if (!switchPm) {
            log.warning "C: switch pm with id '$serverCommand.pmId' not found, cannot switch"
            return
        }
        def sourcePm = clientModelStore.findPmById(serverCommand.sourcePmId)
        if (!sourcePm) {
            log.warning "C: source pm with id '$serverCommand.sourcePmId' not found, cannot switch"
            return
        }
        switchPm.syncWith sourcePm
        return serverCommand.pmId
    }

    def handle(InitializeAttributeCommand serverCommand) {
        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue)
        transmit(new AttributeCreatedCommand(pmId: serverCommand.pmId, attributeId: attribute.id, propertyName: serverCommand.propertyName, newValue: serverCommand.newValue))

        if (!clientModelStore.containsPm(serverCommand.pmId)) {
            clientModelStore.storePm(serverCommand.pmId, new ClientPresentationModel(serverCommand.pmId, [attribute]))
            return serverCommand.pmId
        }
        def pm = clientModelStore.findPmById(serverCommand.pmId)
        pm.addAttribute(attribute)
        return serverCommand.pmId // todo dk: check and test
    }

    def handle(InitializeSharedAttributeCommand serverCommand) {

        // todo: check if attribute for that PM already exists and use it

        ClientPresentationModel sharedPm = clientModelStore.findPmById(serverCommand.sharedPmId)
        if (!sharedPm) {
            storeAndSendAttributeWithNewPm serverCommand.sharedPmId, serverCommand.sharedPropertyName, serverCommand.newValue
        }

        ClientAttribute sharedAttr = findAttributeByPmIdAndPropName(serverCommand.sharedPmId, serverCommand.sharedPropertyName)

        if (!sharedAttr) {
            sharedAttr = storeAndSendAttributeWithExistingPm serverCommand.sharedPmId, serverCommand.sharedPropertyName, serverCommand.newValue
        }

        if (null != serverCommand.newValue) {
            sharedAttr.value = serverCommand.newValue
        }

        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue)
        attribute.syncWith(sharedAttr)

        if (!clientModelStore.containsPm(serverCommand.pmId)) {
            clientModelStore.storePm(serverCommand.pmId, new ClientPresentationModel(serverCommand.pmId, [attribute]))
            return serverCommand.pmId
        }
        def pm = clientModelStore.findPmById(serverCommand.pmId)
        pm.addAttribute(attribute)

        return serverCommand.pmId
    }

    /** May return null if Attribute not found but throws no MissingPropertyException */
    BaseAttribute findAttributeByPmIdAndPropName(String pmId, propertyName) {
        clientModelStore.findPmById(pmId).attributes.find { it.propertyName == propertyName }
    }

    ClientAttribute storeAndSendAttributeWithExistingPm(String pmId, String propertyName, newValue) {
        ClientAttribute attribute = new ClientAttribute(propertyName, newValue)
        transmit(new AttributeCreatedCommand(pmId: pmId, attributeId: attribute.id, propertyName: propertyName, newValue: newValue))
        clientModelStore.findPmById(pmId).addAttribute(attribute)
        return attribute
    }

    ClientAttribute storeAndSendAttributeWithNewPm(String pmId, String propertyName, newValue) {
        def attribute = new ClientAttribute(propertyName, newValue)
        transmit(new AttributeCreatedCommand(pmId: pmId, attributeId: attribute.id, propertyName: propertyName, newValue: newValue))
        clientModelStore.storePm(pmId, new ClientPresentationModel(pmId, [attribute]))
        return attribute
    }


    void withPm(String viewPmId, String discriminator, Closure onFinished) {
        ClientPresentationModel result = clientModelStore.findPmById("$viewPmId-$discriminator")
        if (result) {
            onFinished result
            return
        }
        send(new GetPmCommand(pmType: viewPmId, selector: discriminator)) { pmIds ->
            def theOnlyOne = pmIds.toList().first()
            assert theOnlyOne == "$viewPmId-$discriminator" // sanity check
            result = clientModelStore.findPmById(theOnlyOne)
            onFinished result
        }
    }
}
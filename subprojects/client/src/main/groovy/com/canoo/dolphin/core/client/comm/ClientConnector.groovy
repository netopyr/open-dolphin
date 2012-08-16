package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import com.canoo.dolphin.core.comm.*

@Log
abstract class ClientConnector implements PropertyChangeListener {
    Codec codec

    UiThreadHandler uiThreadHandler // must be set from the outside - toolkit specific

    DataflowVariable<Throwable> exceptionHappened
    protected ClientDolphin clientDolphin

    ClientConnector(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin
        exceptionHappened = new DataflowVariable<Throwable>()
        exceptionHappened.whenBound {
            throw exceptionHappened.val
        }
    }

    protected getClientModelStore() {
        clientDolphin.clientModelStore
    }

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        send constructValueChangedCommand(evt)
        List<Attribute> attributes = clientModelStore.findAllAttributesByQualifier(evt.source.qualifier)
        attributes.each { it.value = evt.newValue }
    }

    ValueChangedCommand constructValueChangedCommand(PropertyChangeEvent evt) {
        new ValueChangedCommand(
                attributeId: evt.source.id,
                oldValue: evt.oldValue,
                newValue: evt.newValue
        )
    }

    void switchPresentationModelAndSend(ClientPresentationModel switcher, ClientPresentationModel newSource) {
        switcher.syncWith newSource
        send new SwitchPresentationModelCommand(pmId: switcher.id, sourcePmId: newSource.id)
    }

    abstract List<Command> transmit(Command command)

    abstract int getPoolSize()

    PGroup group = new DefaultPGroup(poolSize)

    void send(Command command, OnFinishedHandler callback = null) {
        def result = new DataflowVariable()
        processAsync {
            log.info "C: transmitting $command"
            result << transmit(command)
            insideUiThread {
                List<Command> response = result.get()
                log.info "C: server responded with ${ response?.size() } command(s): ${ response?.id }"

                List<ClientPresentationModel> pms = []
                for (serverCommand in response) {
                    def pmId = handle serverCommand
                    if (pmId && pmId instanceof String) {
                        pms << clientModelStore.findPresentationModelById(pmId)
                    }
                }
                if (callback) callback.onFinished( pms.unique {it.id} )
            }
        }
    }

    void processAsync(Runnable processing) {
        group.task {
            doExceptionSafe processing
        }
    }

    void doExceptionSafe(Runnable processing) {
        try {
            processing.run()
        } catch (e) {
            exceptionHappened << e
            throw e
        }
    }

    void insideUiThread(Runnable processing) {
        doExceptionSafe {
            if (uiThreadHandler) {
                uiThreadHandler.executeInsideUiThread(processing)
            } else {
                log.warning("please provide howToProcessInsideUI handler")
                processing.run()
            }
        }
    }

    def handle(Command serverCommand, Set pmIds) {
        log.warning "C: cannot handle $serverCommand"
    }

    String handle(CreatePresentationModelCommand serverCommand) {
        // check if we already have serverCommand.pmId in our store
        // if true we simply update attribute ids and add any missing attributes

        if (clientModelStore.containsPresentationModel(serverCommand.pmId)) {
            ClientPresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
            serverCommand.attributes.each { attr ->
                ClientAttribute attribute = model.findAttributeByPropertyName(attr.propertyName)
                if (null == attribute) {
                    attribute = new ClientAttribute(attr.propertyName, attr.value)
                    attribute.value = attr.value
                    attribute.id = attr.id
                    attribute.qualifier = attr.qualifier
                    model.addAttribute(attribute)
                    clientModelStore.registerAttribute(attribute)
                } else {
                    clientModelStore.updateAttributeId(attribute, attr.id)
                }
            }
        } else {
            List<ClientAttribute> attributes = []
            serverCommand.attributes.each { attr ->
                ClientAttribute attribute = new ClientAttribute(attr.propertyName, attr.value)
                attribute.value = attr.value
                attribute.id = attr.id
                attribute.qualifier = attr.qualifier
                attributes << attribute
            }
            ClientPresentationModel model = new ClientPresentationModel(serverCommand.pmId, attributes)
            model.presentationModelType = serverCommand.pmType
            clientModelStore.add(model)
        }
        serverCommand.pmId
    }

    String handle(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
            return null
        }

        log.info "C: updating '$attribute.propertyName' id '$serverCommand.attributeId' from '$attribute.value' to '$serverCommand.newValue'"
        attribute.value = serverCommand.newValue

        List<Attribute> clientAttributes = clientModelStore.findAllAttributesByQualifier(attribute.qualifier)
        clientAttributes.findAll { it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating '$outdated.propertyName' id '$serverCommand.attributeId' from '$outdated.value' to '$serverCommand.newValue'"
            outdated.value = serverCommand.newValue
        }
        return null
    }

    String handle(SwitchPresentationModelCommand serverCommand) {
        def switchPm = clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (!switchPm) {
            log.warning "C: switch pm with id '$serverCommand.pmId' not found, cannot switch"
            return
        }
        def sourcePm = clientModelStore.findPresentationModelById(serverCommand.sourcePmId)
        if (!sourcePm) {
            log.warning "C: source pm with id '$serverCommand.sourcePmId' not found, cannot switch"
            return
        }
        switchPm.syncWith sourcePm
        return serverCommand.pmId
    }

    String handle(InitializeAttributeCommand serverCommand) {
        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue)
        attribute.qualifier = serverCommand.qualifier

        // todo: add check for no-value; null is a valid value
        if (serverCommand.qualifier) {
            def copies = clientModelStore.findAllAttributesByQualifier(serverCommand.qualifier)
            if (copies) {
                if (null == serverCommand.newValue) {
                    attribute.value = copies.first()?.value
                } else {
                    copies.each { attr ->
                        attr.value = attribute.value
                    }
                }
            }
        }

        if (!clientModelStore.containsPresentationModel(serverCommand.pmId)) {
            clientModelStore.add(new ClientPresentationModel(serverCommand.pmId, [attribute]))
            return serverCommand.pmId
        }
        def pm = clientModelStore.findPresentationModelById(serverCommand.pmId)
        pm.addAttribute(attribute)
        clientModelStore.registerAttribute(attribute)
        return serverCommand.pmId // todo dk: check and test
    }

    String handle(PresentationModelSavedCommand serverCommand) {
        if (!serverCommand.pmId) return null
        PresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
        // save locally first
        model.attributes*.save()
        // inform server of changes
        model.attributes.each { attribute -> send(new InitialValueChangedCommand(attributeId: attribute.id)) }
        model.id
    }

}
package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.Dolphin
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import com.canoo.dolphin.core.comm.*

import java.util.concurrent.CountDownLatch

@Log
abstract class ClientConnector implements PropertyChangeListener {
    Codec codec

    UiThreadHandler uiThreadHandler // must be set from the outside - toolkit specific

    protected DataflowVariable<Throwable> exceptionHappened

    ClientConnector() {
        exceptionHappened = new DataflowVariable<Throwable>()
        exceptionHappened.whenBound {
            println Thread.currentThread()
            throw exceptionHappened.val
        }
    }

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        send constructValueChangedCommand(evt)
        List<Attribute> attributes = Dolphin.clientModelStore.findAllAttributesByQualifier(evt.source.qualifier)
        attributes.each { it.value = evt.newValue }
    }

    void registerAndSend(ClientPresentationModel cpm, ClientAttribute ca) {
        Dolphin.clientModelStore.add(cpm)
        Dolphin.clientModelStore.registerAttribute(ca)
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
                newValue: attribute.value,
                qualifier: attribute.qualifier
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
                        pms << Dolphin.clientModelStore.findPresentationModelById(pmId)
                    }
                }
                if (callback) callback.onFinished( pms.unique {it.id} )
            }
        }
    }

    void processAsync(Runnable processing) {
        group.task {
            try {
                processing.run()
            } catch (e) {
                exceptionHappened << e
                throw e
            }
        }
    }

    void insideUiThread(Runnable processing) {
        try {
            if (uiThreadHandler) {
                uiThreadHandler.executeInsideUiThread(processing)
            } else {
                log.warning("please provide howToProcessInsideUI handler")
                processing.run()
            }
        } catch (e) {
            exceptionHappened << e
            throw e
        }
    }

    def handle(Command serverCommand, Set pmIds) {
        log.warning "C: cannot handle $serverCommand"
    }

    String handle(CreatePresentationModelCommand serverCommand) {
        // check if we already have serverCommand.pmId in our store
        // if true we simply update attribute ids and add any missing attributes

        if (Dolphin.clientModelStore.containsPresentationModel(serverCommand.pmId)) {
            ClientPresentationModel model = Dolphin.clientModelStore.findPresentationModelById(serverCommand.pmId)
            serverCommand.attributes.each { attr ->
                ClientAttribute attribute = model.findAttributeByPropertyName(attr.propertyName)
                if (null == attribute) {
                    attribute = new ClientAttribute(attr.propertyName, attr.value)
                    attribute.value = attr.value
                    attribute.id = attr.id
                    attribute.qualifier = attr.qualifier
                    model.addAttribute(attribute)
                    Dolphin.clientModelStore.registerAttribute(attribute)
                } else {
                    Dolphin.clientModelStore.updateAttributeId(attribute, attr.id)
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
            Dolphin.clientModelStore.add(model)
        }
        serverCommand.pmId
    }

    String handle(ValueChangedCommand serverCommand) {
        Attribute attribute = Dolphin.clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
            return null
        }

        log.info "C: updating '$attribute.propertyName' id '$serverCommand.attributeId' from '$attribute.value' to '$serverCommand.newValue'"
        attribute.value = serverCommand.newValue

        List<Attribute> clientAttributes = Dolphin.clientModelStore.findAllAttributesByQualifier(attribute.qualifier)
        clientAttributes.findAll { it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating '$outdated.propertyName' id '$serverCommand.attributeId' from '$outdated.value' to '$serverCommand.newValue'"
            outdated.value = serverCommand.newValue
        }
        return null
    }

    String handle(SwitchPresentationModelCommand serverCommand) {
        def switchPm = Dolphin.clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (!switchPm) {
            log.warning "C: switch pm with id '$serverCommand.pmId' not found, cannot switch"
            return
        }
        def sourcePm = Dolphin.clientModelStore.findPresentationModelById(serverCommand.sourcePmId)
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
            def copies = Dolphin.clientModelStore.findAllAttributesByQualifier(serverCommand.qualifier)
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
        if (!Dolphin.clientModelStore.containsPresentationModel(serverCommand.pmId)) {
            def pm = new ClientPresentationModel(serverCommand.pmId, [attribute])
            pm.presentationModelType = serverCommand.pmType
            Dolphin.clientModelStore.add(pm)
            return serverCommand.pmId
        }
        def pm = Dolphin.clientModelStore.findPresentationModelById(serverCommand.pmId)
        pm.addAttribute(attribute)
        Dolphin.clientModelStore.registerAttribute(attribute)
        return serverCommand.pmId // todo dk: check and test
    }

    String handle(PresentationModelSavedCommand serverCommand) {
        if (!serverCommand.pmId) return null
        PresentationModel model = Dolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId)
        // save locally first
        model.attributes*.save()
        // inform server of changes
        model.attributes.each { attribute -> send(new InitialValueChangedCommand(attributeId: attribute.id)) }
        model.id
    }

    void withPresentationModel(String viewPmId, String selector, Closure onFinished) {
        PresentationModel result = Dolphin.clientModelStore.findPresentationModelById("$viewPmId-$selector")
        if (result) {
            onFinished result
            return
        }
        send(new GetPresentationModelCommand(pmType: viewPmId, selector: selector), { pmIds ->
            def theOnlyOne = pmIds.toList().first()
            assert theOnlyOne == "$viewPmId-$selector" // sanity check
            result = Dolphin.clientModelStore.findPresentationModelById(theOnlyOne)
            onFinished result
        } as OnFinishedHandler )
    }
}
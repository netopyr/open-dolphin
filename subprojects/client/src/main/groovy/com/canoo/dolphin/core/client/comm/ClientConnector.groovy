package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import com.canoo.dolphin.core.comm.*
import com.canoo.dolphin.core.client.Dolphin

@Log
abstract class ClientConnector implements PropertyChangeListener {
    Codec codec

    UiThreadHandler uiThreadHandler // must be set from the outside - toolkit specific

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        send constructValueChangedCommand(evt)
        List<Attribute> attributes = Dolphin.clientModelStore.findAllAttributesByDataId([evt.source.dataId])
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
                dataId: attribute.dataId
        )
    }

    void switchPmAndSend(ClientPresentationModel switcher, ClientPresentationModel newSource) {
        switcher.syncWith newSource
        send new SwitchPmCommand(pmId: switcher.id, sourcePmId: newSource.id)
    }

    abstract List<Command> transmit(Command command)

    abstract int getPoolSize()

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
                    if (pms && pms instanceof String) pmIds << pms
                }
                if (onFinished) onFinished pmIds
            }
        }
    }

    void processAsync(Runnable processing) {
        group.task processing
    }

    void insideUiThread(Runnable processing) {
        if (uiThreadHandler) {
            uiThreadHandler.executeInsideUiThread(processing)
        } else {
            log.warning("please provide howToProcessInsideUI handler")
            processing.run()
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
                    attribute = new ClientAttribute(attr.propertyName)
                    attribute.value = attr.value
                    attribute.id = attr.id
                    attribute.dataId = attr.dataId
                    model.attributes << attribute
                    Dolphin.clientModelStore.registerAttribute(attribute)
                } else {
                    Dolphin.clientModelStore.updateAttributeId(attribute, attr.id)
                }
            }
        } else {
            List<ClientAttribute> attributes = []
            serverCommand.attributes.each { attr ->
                ClientAttribute attribute = new ClientAttribute(attr.propertyName)
                attribute.value = attr.value
                attribute.id = attr.id
                attribute.dataId = attr.dataId
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

        List<Attribute> clientAttributes = Dolphin.clientModelStore.findAllAttributesByDataId(attribute.dataId)
        clientAttributes.findAll { it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating '$outdated.propertyName' id '$serverCommand.attributeId' from '$outdated.value' to '$serverCommand.newValue'"
            outdated.value = serverCommand.newValue
        }
        return null
    }

    String handle(SwitchPmCommand serverCommand) {
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
        attribute.dataId = serverCommand.dataId

        // todo: add check for no-value; null is a valid value
        if (serverCommand.dataId) {
            def copies = Dolphin.clientModelStore.findAllAttributesByDataId(serverCommand.dataId)
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
            Dolphin.clientModelStore.add(new ClientPresentationModel(serverCommand.pmId, [attribute]))
            return serverCommand.pmId
        }
        def pm = Dolphin.clientModelStore.findPresentationModelById(serverCommand.pmId)
        pm.attributes << attribute
        Dolphin.clientModelStore.registerAttribute(attribute)
        return serverCommand.pmId // todo dk: check and test
    }

    void withPm(String viewPmId, String discriminator, Closure onFinished) {
        PresentationModel result = Dolphin.clientModelStore.findPresentationModelById("$viewPmId-$discriminator")
        if (result) {
            onFinished result
            return
        }
        send(new GetPmCommand(pmType: viewPmId, selector: discriminator)) { pmIds ->
            def theOnlyOne = pmIds.toList().first()
            assert theOnlyOne == "$viewPmId-$discriminator" // sanity check
            result = Dolphin.clientModelStore.findPresentationModelById(theOnlyOne)
            Dolphin.clientModelStore.add result
            onFinished result
        }
    }
}
package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.ModelStore
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

@Log
abstract class ClientConnector implements PropertyChangeListener {
    Codec codec

    ModelStore clientModelStore
    UiThreadHandler uiThreadHandler // must be set from the outside - toolkit specific

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.oldValue == evt.newValue) return
        send constructValueChangedCommand(evt)
        List<Attribute> attributes = clientModelStore.findAllAttributesByDataId([evt.source.dataId])
        attributes.each { it.value = evt.newValue }
    }

    void registerAndSend(ClientPresentationModel cpm, ClientAttribute ca) {
        clientModelStore.add(cpm)
        clientModelStore.registerAttribute(ca)
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

    def handle(CreatePresentationModelCommand serverCommand) {
        List<ClientAttribute> attributes = []
        serverCommand.attributes.each { attr ->
            ClientAttribute attribute = new ClientAttribute(attr.propertyName)
            attribute.value = attr.value
            attribute.id = attr.id
            attribute.dataId = attr.dataId
            attributes << attribute
        }
        PresentationModel model = new ClientPresentationModel(serverCommand.pmId, attributes)
        model.presentationModelType = serverCommand.pmType
        clientModelStore.add(model)
        model.id
    }

    def handle(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
            return
        }

        log.info "C: updating '$attribute.propertyName' id '$serverCommand.attributeId' from '$attribute.value' to '$serverCommand.newValue'"
        attribute.value = serverCommand.newValue

        List<Attribute> clientAttributes = clientModelStore.findAllAttributesByDataId(attribute.dataId)
        clientAttributes.findAll { it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating '$outdated.propertyName' id '$serverCommand.attributeId' from '$outdated.value' to '$serverCommand.newValue'"
            outdated.value = serverCommand.newValue
        }
    }

    def handle(SwitchAttributeIdCommand serverCommand) {
        def sourceAtt = clientModelStore.findAttributeById(serverCommand.newId) // one is enough
        if (!sourceAtt) {
            log.warning "C: attribute with id '$serverCommand.newId' not found, cannot switch"
            return
        }
        def switchPm = clientModelStore.findPresentationModelById(serverCommand.pmId)
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

    def handle(InitializeAttributeCommand serverCommand) {
        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue)
        attribute.dataId = serverCommand.dataId
        /*

        Why do we send the command back to the server again?
        We just received this command from the server anyway.

        transmit(new AttributeCreatedCommand(
                pmId: serverCommand.pmId,
                attributeId: attribute.id,
                propertyName: serverCommand.propertyName,
                newValue: serverCommand.newValue,
                dataId: attribute.dataId))
        */

        // todo: add check for no-value; null is a valid value
        if (serverCommand.dataId) {
            def copies = clientModelStore.findAllAttributesByDataId(serverCommand.dataId)
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

    void withPm(String viewPmId, String discriminator, Closure onFinished) {
        PresentationModel result = clientModelStore.findPresentationModelById("$viewPmId-$discriminator")
        if (result) {
            onFinished result
            return
        }
        send(new GetPmCommand(pmType: viewPmId, selector: discriminator)) { pmIds ->
            def theOnlyOne = pmIds.toList().first()
            assert theOnlyOne == "$viewPmId-$discriminator" // sanity check
            result = clientModelStore.findPresentationModelById(theOnlyOne)
            onFinished result
        }
    }
}
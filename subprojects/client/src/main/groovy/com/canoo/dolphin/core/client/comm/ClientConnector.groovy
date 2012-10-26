/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup
import org.codehaus.groovy.runtime.StackTraceUtils

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
        if (evt.propertyName == Attribute.DIRTY_PROPERTY) {
            // ignore
        } else if (evt.propertyName == Attribute.VALUE) {
            if (evt.oldValue == evt.newValue) return
            send constructValueChangedCommand(evt)
            List<Attribute> attributes = clientModelStore.findAllAttributesByQualifier(evt.source.qualifier)
            attributes.each { it.value = evt.newValue }
        } else if (evt.propertyName == Attribute.INITIAL_VALUE) {
            if (evt.oldValue == evt.newValue) return
            send constructInitialValueChangedCommand(evt)
            List<Attribute> attributes = clientModelStore.findAllAttributesByQualifier(evt.source.qualifier)
            attributes.each { it.rebase() }
        } else {
            // we assume the change is on a metadata property such as qualifier
            send constructChangeAttributeMetadataCommand(evt)
        }
    }

    private ValueChangedCommand constructValueChangedCommand(PropertyChangeEvent evt) {
        new ValueChangedCommand(
                attributeId: evt.source.id,
                oldValue: evt.oldValue,
                newValue: evt.newValue
        )
    }

    private InitialValueChangedCommand constructInitialValueChangedCommand(PropertyChangeEvent evt) {
        new InitialValueChangedCommand(
                attributeId: evt.source.id
        )
    }

    private ChangeAttributeMetadataCommand constructChangeAttributeMetadataCommand(PropertyChangeEvent evt) {
        new ChangeAttributeMetadataCommand(
                attributeId: evt.source.id,
                metadataName: evt.propertyName,
                value: evt.newValue
        )
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
                    def pm = handle serverCommand
                    if (pm && pm instanceof ClientPresentationModel) {
                        pms << pm
                    }
                }
                if (callback) callback.onFinished(pms.unique {it.id})
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
            StackTraceUtils.deepSanitize(e)
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

    ClientPresentationModel handle(DeletePresentationModelCommand serverCommand){
        ClientPresentationModel model = clientDolphin.findPresentationModelById(serverCommand.pmId)
        if (!model) return null
        clientModelStore.delete(model)
        return model
    }

    ClientPresentationModel handle(CreatePresentationModelCommand serverCommand) {
        // check if we already have serverCommand.pmId in our store
        // if true we simply update attribute ids and add any missing attributes
        if (clientModelStore.containsPresentationModel(serverCommand.pmId)) {
            return mergeAttributes(serverCommand.pmId, serverCommand.attributes)
        }
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
        return model
    }

    def ClientPresentationModel mergeAttributes(String pmId, List<Map<String, Object>> attributes) {
        ClientPresentationModel model = clientModelStore.findPresentationModelById(pmId)
        attributes.each { attr ->
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
        return model
    }

    ClientPresentationModel handle(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
            return null
        }

        log.info "C: updating '$attribute.propertyName' id '$serverCommand.attributeId' from '$attribute.value' to '$serverCommand.newValue'"
        attribute.value = serverCommand.newValue

        /* // todo dk: what is up here?
        List<Attribute> clientAttributes = clientModelStore.findAllAttributesByQualifier(attribute.qualifier)
        clientAttributes.findAll { it.value != serverCommand.newValue }.each { outdated ->
            log.info "C: updating '$outdated.propertyName' id '$serverCommand.attributeId' from '$outdated.value' to '$serverCommand.newValue'"
            outdated.value = serverCommand.newValue
        }
        */
        return null // this command is not expected to be sent explicitly, so no pm needs to be returned
    }

    ClientPresentationModel handle(InitialValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update"
            return null
        }
        log.info "C: updating id '$serverCommand.attributeId' setting initialValue to '$attribute.value'"
        attribute.rebase()
        return null // this command is not expected to be sent explicitly, so no pm needs to be returned
    }

    ClientPresentationModel handle(SwitchPresentationModelCommand serverCommand) {
        def switchPm = clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (!switchPm) {
            log.warning "C: switch pm with id '$serverCommand.pmId' not found, cannot switch"
            return null
        }
        def sourcePm = clientModelStore.findPresentationModelById(serverCommand.sourcePmId)
        if (!sourcePm) {
            log.warning "C: source pm with id '$serverCommand.sourcePmId' not found, cannot switch"
            return null
        }
        switchPm.syncWith sourcePm                  // ==  clientDolphin.apply sourcePm to switchPm
        return (ClientPresentationModel) switchPm
    }

    ClientPresentationModel handle(InitializeAttributeCommand serverCommand) {
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
        def presentationModel = clientModelStore.findPresentationModelById(serverCommand.pmId)
        if (!presentationModel) {
            presentationModel = new ClientPresentationModel(serverCommand.pmId, [])
            presentationModel.setPresentationModelType(serverCommand.pmType)
            clientModelStore.add(presentationModel)
        }
        presentationModel.addAttribute(attribute)
        clientModelStore.registerAttribute(attribute)
        send new AttributeCreatedCommand(
                pmId: presentationModel.id,
                attributeId: attribute.id,
                propertyName: attribute.propertyName,
                newValue: attribute.value,
                qualifier: attribute.qualifier
        )
        return presentationModel // todo dk: check and test
    }

    ClientPresentationModel handle(SavedPresentationModelNotification serverCommand) {
        if (!serverCommand.pmId) return null
        PresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
        model.attributes*.rebase() // rebase sends update command if needed through PCL
        return model
    }

    ClientPresentationModel handle(PresentationModelResetedCommand serverCommand) {
        if (!serverCommand.pmId) return null
        PresentationModel model = clientModelStore.findPresentationModelById(serverCommand.pmId)
        // rebase locally first
        model.attributes*.reset()
        // inform server of changes
        // todo dk: this should already have been sent by the PCL
        model.attributes.each { attribute -> send(new ValueChangedCommand(attributeId: attribute.id)) }
        return model
    }

    ClientPresentationModel handle(AttributeMetadataChangedCommand serverCommand) {
        ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) return null
        attribute[serverCommand.metadataName] = serverCommand.value
        return null
    }

    ClientPresentationModel handle(PresentationModelLinkAddedCommand serverCommand) {
        PresentationModel a = clientModelStore.findPresentationModelById(serverCommand.startId)
        PresentationModel b = clientModelStore.findPresentationModelById(serverCommand.endId)
        if (null == a || null == b || null == serverCommand.type) return null
        clientModelStore.link(a, b, serverCommand.type)
        return null
    }

    ClientPresentationModel handle(PresentationModelLinkRemovedCommand serverCommand) {
        PresentationModel a = clientModelStore.findPresentationModelById(serverCommand.startId)
        PresentationModel b = clientModelStore.findPresentationModelById(serverCommand.endId)
        if (null == a || null == b || null == serverCommand.type) return null
        clientModelStore.unlink(a, b, serverCommand.type)
        return null
    }
}
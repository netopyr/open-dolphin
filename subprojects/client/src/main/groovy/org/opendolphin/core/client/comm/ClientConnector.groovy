/*
 * Copyright 2012-2013 Canoo Engineering AG.
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

package org.opendolphin.core.client.comm

import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.comm.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup
import org.codehaus.groovy.runtime.StackTraceUtils

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

@Log
abstract class ClientConnector implements PropertyChangeListener {
    Codec codec

    UiThreadHandler uiThreadHandler // must be set from the outside - toolkit specific
    Closure onException = { Throwable up ->
        def out = new StringWriter()
        up.printStackTrace(new PrintWriter(out))
        log.severe("onException reached, rethrowing in UI Thread, consider setting ClientConnector.onException\n${out.buffer}")
        uiThreadHandler.executeInsideUiThread { throw up } // not sure whether this is a good default
    }

    protected ClientDolphin clientDolphin

    ClientConnector(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin
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
        } else if (evt.propertyName == Attribute.BASE_VALUE) {
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

    @CompileStatic
    void send(Command command, OnFinishedHandler callback = null) {
        def me = this
        processAsync {
            def result = new DataflowVariable()
            me.info "C: transmitting $command"
            result << transmit(command)
            insideUiThread {
                List<Command> response = result.get() as List<Command>
                me.info "C: server responded with ${ response?.size() } command(s): ${ response?.id }"

                List<ClientPresentationModel> pms = new LinkedList<ClientPresentationModel>()
                List<Map> maps = new LinkedList<Map>()
                for (Command serverCommand in response) {
                    def pm = me.dispatchHandle serverCommand
                    if (pm && pm instanceof ClientPresentationModel) {
                        pms << (ClientPresentationModel) pm
                    } else if (pm && pm instanceof Map) {
                        maps << (Map) pm
                    }
                }
                if (callback) {
                    callback.onFinished( (List<ClientPresentationModel>) pms.unique { ((ClientPresentationModel) it).id})
                    callback.onFinishedData(maps)
                }
            }
        }
    }

    void info (Object message) {
        log.info message
    }

    Object dispatchHandle(Command command) {
        handle command
    }

    @CompileStatic
    void processAsync(Runnable processing) {
        group.task {
            doExceptionSafe processing
        }
    }

    @CompileStatic
    void doExceptionSafe(Runnable processing) {
        try {
            processing.run()
        } catch (e) {
            StackTraceUtils.deepSanitize(e)
            onException e
        }
    }

    @CompileStatic
    void insideUiThread(Runnable processing) {
        doExceptionSafe {
            if (uiThreadHandler) {
                uiThreadHandler.executeInsideUiThread(processing)
            } else {
                println("please provide howToProcessInsideUI handler")
                processing.run()
            }
        }
    }

    //TODO: db: @Dierk : can this method be removed ?
    def handle(Command serverCommand, Set pmIds) {
        log.warning "C: cannot handle $serverCommand"
    }

    Map handle(DataCommand serverCommand){
        return serverCommand.data
    }

    ClientPresentationModel handle(DeletePresentationModelCommand serverCommand){
        ClientPresentationModel model = clientDolphin.findPresentationModelById(serverCommand.pmId)
        if (!model) return null
        clientModelStore.delete(model)
        return model
    }

    @CompileStatic
    ClientPresentationModel handle(CreatePresentationModelCommand serverCommand) {
        // check if we already have serverCommand.pmId in our store
        // if true we simply update attribute ids and add any missing attributes
        if (((ClientModelStore)clientModelStore).containsPresentationModel(serverCommand.pmId)) {
            throw new IllegalStateException("There already is a presentation model with id '$serverCommand.pmId' known to the client.")
        }
        List<ClientAttribute> attributes = []
        for (attr in serverCommand.attributes) {
            ClientAttribute attribute = new ClientAttribute(attr.propertyName.toString(), attr.value)
            attribute.qualifier = attr.qualifier
            attributes << attribute
        }
        ClientPresentationModel model = new ClientPresentationModel(serverCommand.pmId, attributes)
        model.presentationModelType = serverCommand.pmType
        if (serverCommand.clientSideOnly) {
            model.clientSideOnly = true
            ((ClientModelStore)clientModelStore).addClientSideOnly(model)
        } else {
            ((ClientModelStore)clientModelStore).add(model)
        }
        return model
    }

    ClientPresentationModel handle(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot update old value '$serverCommand.oldValue' to new value '$serverCommand.newValue'"
            return null
        }
        log.info "C: updating '$attribute.propertyName' id '$serverCommand.attributeId' from '$attribute.value' to '$serverCommand.newValue'"
        attribute.value = serverCommand.newValue
        return null // this command is not expected to be sent explicitly, so no pm needs to be returned
    }

    ClientPresentationModel handle(InitialValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.attributeId)
        if (!attribute) {
            log.warning "C: attribute with id '$serverCommand.attributeId' not found, cannot set initial value."
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
        def attribute = new ClientAttribute(serverCommand.propertyName, serverCommand.newValue, serverCommand.qualifier, serverCommand.tag)

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
        def presentationModel = null
        if (serverCommand.pmId) presentationModel = clientModelStore.findPresentationModelById(serverCommand.pmId)
        // if there is no pmId, it is most likely an error and CreatePresentationModelCommand should have been used
        if (!presentationModel) {
            presentationModel = new ClientPresentationModel(serverCommand.pmId, [])
            presentationModel.setPresentationModelType(serverCommand.pmType)
            clientModelStore.add(presentationModel)
        }
        clientDolphin.addAttributeToModel(presentationModel, attribute)
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
        if (!model) return null
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

}
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

package org.opendolphin.core.client
import org.opendolphin.core.Dolphin
import org.opendolphin.core.ModelStore
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter
import org.opendolphin.core.comm.AttributeCreatedNotification
import org.opendolphin.core.comm.EmptyNotification
import org.opendolphin.core.comm.NamedCommand

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
// makes use of dynamic dispatch, do not use @CompileStatic
public class ClientDolphin extends Dolphin {

    // todo dk: the client model store should become a secret of the ClientDolphin
    ClientModelStore clientModelStore

    ClientConnector clientConnector

    @Override
    ModelStore getModelStore() {
        return clientModelStore
    }

    /** Convenience method for a creating a ClientPresentationModel with initial null values for the attributes
     */
    ClientPresentationModel presentationModel(String id, List<String> attributeNames) {
        def result = new ClientPresentationModel(id, attributeNames.collect() { new ClientAttribute(it)})
        clientModelStore.add result
        return result
    }

    /** groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values*/
    ClientPresentationModel presentationModel(String id, String presentationModelType = null, Map<String, Object> attributeNamesAndValues) {
        presentationModel(attributeNamesAndValues, id, presentationModelType)
    }

    /** groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values*/
    ClientPresentationModel presentationModel(Map<String, Object> attributeNamesAndValues, String id, String presentationModelType = null) {
        def attributes = attributeNamesAndValues.collect {key, value -> new ClientAttribute(key, value) }
        def result = new ClientPresentationModel(id, attributes)
        result.presentationModelType = presentationModelType
        clientModelStore.add result
        return result
    }

    /** both groovy- and java-friendly full-control factory */
    ClientPresentationModel presentationModel(String id, String presentationModelType = null, ClientAttribute... attributes) {
        def result = new ClientPresentationModel(id, attributes as List)
        result.presentationModelType = presentationModelType
        clientModelStore.add result
        return result
    }

    /** java-friendly convenience method for sending a named command*/
    void send(String commandName, OnFinishedHandler onFinished = null) {
        clientConnector.send new NamedCommand(commandName), onFinished
    }

    /** groovy-friendly convenience method for sending a named command that expects only PM responses */
    void send(String commandName, Closure onFinished) {
        clientConnector.send(new NamedCommand(commandName), new OnFinishedHandlerAdapter(){
            void onFinished(List<ClientPresentationModel> presentationModels) {
                onFinished(presentationModels)
            }
        })
    }

    /** both java- and groovy-friendly convenience method to send an empty command, which will have no
     * presentation models nor data in the callback */
    void sync(Runnable runnable) {
        println "in sync"
        clientConnector.send(new EmptyNotification(), new OnFinishedHandlerAdapter() {
            void onFinished(List<ClientPresentationModel> presentationModels) {
               println "about to run the runnable"
               runnable.run()
           }
        })
    }

    /** groovy-friendly convenience method for sending a named command that expects only data responses*/
    void data(String commandName, Closure onFinished) {
        clientConnector.send(new NamedCommand(commandName), new OnFinishedHandlerAdapter(){
            void onFinishedData(List<Map> data) {
                onFinished(data)
            }
        })
    }

    /** start of a fluent api: apply source to target. Use for selection changes in master-detail views. */
    ApplyToAble apply(ClientPresentationModel source) {
        new ApplyToAble(dolphin: this, source: source)
    }

    /** Removes the modelToDelete from the client model store,
     * detaches all model store listeners,
     * and notifies the server if successful */
    public void delete(ClientPresentationModel modelToDelete) {
        clientModelStore.delete(modelToDelete)
    }

    /** Removes the models of a given type from the model store just like delete()
     * but sends only one notification to the server */
    public void deleteAllPresentationModelsOfType(String presentationModelType) {
        clientModelStore.deleteAllPresentationModelsOfType(presentationModelType)
    }

    /**
     * Tags the attribute by
     * adding a new attribute with the given tag and value to the model store
     * inside the given presentation model and for the given property name.
     * @return the ClientAttribute that carries the tag value
     */
    public ClientAttribute tag(ClientPresentationModel model, String propertyName, Tag tag, def value) {
        def attribute = new ClientAttribute(propertyName, value, null, tag)
        addAttributeToModel(model, attribute)
        return attribute
    }

    public void addAttributeToModel(PresentationModel presentationModel, ClientAttribute attribute) {
        presentationModel._internal_addAttribute(attribute)
        clientModelStore.registerAttribute(attribute)
        if (!((ClientPresentationModel)presentationModel).clientSideOnly) {
            clientConnector.send new AttributeCreatedNotification(
                    pmId: presentationModel.id,
                    attributeId: attribute.id,
                    propertyName: attribute.propertyName,
                    newValue: attribute.value,
                    qualifier: attribute.qualifier,
                    tag: attribute.tag
            )
        }
    }
}

class ApplyToAble {
    ClientDolphin dolphin
    ClientPresentationModel source

    void to(ClientPresentationModel target) {
        target.syncWith source
        // at this point, all notifications about value and meta-inf changes
        // have been sent and that way the server is synchronized
    }
}

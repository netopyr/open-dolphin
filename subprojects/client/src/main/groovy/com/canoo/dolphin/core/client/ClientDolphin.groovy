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

package com.canoo.dolphin.core.client
import com.canoo.dolphin.core.Dolphin
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.Tag
import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.client.comm.OnFinishedHandlerAdapter
import com.canoo.dolphin.core.comm.AttributeCreatedNotification
import com.canoo.dolphin.core.comm.NamedCommand
/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
public class ClientDolphin extends Dolphin {

    // todo dk: the client model store should become a secret of the ClientDolphin
    ClientModelStore clientModelStore

    ClientConnector clientConnector

    @Override
    ModelStore getModelStore() {
        return clientModelStore
    }

    /** Convenience method for a typical case of creating a ClientPresentationModel.
     * @deprecated it is very unlikely that setting attributes without initial values makes any sense.
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
        presentationModel.addAttribute(attribute)
        clientModelStore.registerAttribute(attribute)
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

class ApplyToAble {
    ClientDolphin dolphin
    ClientPresentationModel source

    void to(ClientPresentationModel target) {
        target.syncWith source
        // at this point, all notifications about value and meta-inf changes
        // have been sent and that way the server is synchronized
    }
}

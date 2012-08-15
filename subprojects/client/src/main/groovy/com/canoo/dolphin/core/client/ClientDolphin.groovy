package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.client.comm.ClientConnector
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.comm.NamedCommand;

import java.util.List;

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
public class ClientDolphin {

    // todo dk: the client model store should become a secret of the ClientDolphin
    ClientModelStore clientModelStore

    ClientConnector clientConnector

    /** Convenience method for a typical case of creating a ClientPresentationModel.
     * @deprecated it is very unlikely that setting attributes without initial values makes any sense.
     */
    ClientPresentationModel presentationModel(String id, List<String> attributeNames) {
        def result = new ClientPresentationModel(id, attributeNames.collect() { new ClientAttribute(it)} )
        clientModelStore.add result
        return result
    }

    /** groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values*/
    ClientPresentationModel presentationModel(Map<String, Object> attributeNamesAndValues, String id, String presentationModelType = null) {
        def attributes = attributeNamesAndValues.collect {key, value -> new ClientAttribute(key, value) }
        def result = new ClientPresentationModel(id, attributes)
        result.presentationModelType = presentationModelType
        clientModelStore.add result
        return result
    }

    /** java-friendly convenience method for sending a named command*/
    void send(String commandName, OnFinishedHandler onFinished = null) {
        clientConnector.send new NamedCommand(commandName), onFinished
    }

    /** groovy-friendly convenience method for sending a named command*/
    void send(String commandName, Closure onFinished) {
        clientConnector.send(new NamedCommand(commandName), onFinished as OnFinishedHandler)
    }

    /** java-friendly convenience method */
    void onPresentationModelListChanged(String pmType, PresentationModelListChangedListener handler){
        clientModelStore.onPresentationModelListChanged(pmType, handler)
    }

    /** groovy-friendly convenience method */
    void onPresentationModelListChanged(String pmType, Map handler){
        clientModelStore.onPresentationModelListChanged(pmType, handler as PresentationModelListChangedListener)
    }
    /** groovy-friendly convenience method for the invisible-map style */
    void onPresentationModelListChanged(Map handler, String pmType){
        clientModelStore.onPresentationModelListChanged(pmType, handler as PresentationModelListChangedListener)
    }
}

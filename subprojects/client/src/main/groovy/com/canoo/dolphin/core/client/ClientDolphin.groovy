package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.client.comm.ClientConnector;

import java.util.List;

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
public class ClientDolphin {

    // todo dk: the client model store should become a secret of the ClientDolphin
    static ClientModelStore clientModelStore

    static ClientConnector clientConnector

    /** Convenience method for a typical case of creating a ClientPresentationModel */
    static ClientPresentationModel presentationModel(String id, List<String> attributeNames) {
        def result = new ClientPresentationModel(id, attributeNames.collect() { new ClientAttribute(it)} )
        ClientDolphin.clientModelStore.add result
        return result
    }
}

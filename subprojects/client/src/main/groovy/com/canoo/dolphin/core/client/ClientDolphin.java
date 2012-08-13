package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.client.comm.ClientConnector;

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
public class ClientDolphin {
    private static ClientModelStore clientModelStore;

    private static ClientConnector clientConnector;

    public static ClientConnector getClientConnector() {
        return clientConnector;
    }

    public static void setClientConnector(ClientConnector clientConnector) {
        ClientDolphin.clientConnector = clientConnector;
    }

    public static ClientModelStore getClientModelStore() {
        return clientModelStore;
    }

    public static void setClientModelStore(ClientModelStore clientModelStore) {
        ClientDolphin.clientModelStore = clientModelStore;
    }
}

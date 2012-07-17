package com.canoo.dolphin.core.client;

import com.canoo.dolphin.core.client.comm.ClientConnector;

public class Dolphin {
    private static ClientModelStore clientModelStore;

    private static ClientConnector clientConnector;

    public static ClientConnector getClientConnector() {
        return clientConnector;
    }

    public static void setClientConnector(ClientConnector clientConnector) {
        Dolphin.clientConnector = clientConnector;
    }

    public static ClientModelStore getClientModelStore() {
        return clientModelStore;
    }

    public static void setClientModelStore(ClientModelStore clientModelStore) {
        Dolphin.clientModelStore = clientModelStore;
    }
}

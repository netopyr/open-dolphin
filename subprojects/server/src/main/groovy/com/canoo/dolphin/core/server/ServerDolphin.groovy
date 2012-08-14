package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.server.comm.ServerConnector

/**
 * The main Dolphin facade on the server side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with server model store and current response.
 * Threading model: confined to a single controller thread.
 */

class ServerDolphin {

    /** the server model store is unique per user session */
    final ModelStore serverModelStore

    /** the serverConnector is unique per user session */
    final ServerConnector serverConnector

    ServerDolphin(ModelStore serverModelStore, ServerConnector serverConnector) {
        this.serverModelStore = serverModelStore
        this.serverConnector = serverConnector
    }

    ServerDolphin() {
        this(new ModelStore(), new ServerConnector())
    }

    void registerDefaultActions() {
        serverConnector.registerDefaultActions(serverModelStore)
    }

    /** store additional data, if present override and return the old one */
    def putData(ServerPresentationModel serverPresentationModel, String key, Object value) {
        serverPresentationModel.putData key, value
    }

    /** @return the additional data or null if not present */
    def findData(ServerPresentationModel serverPresentationModel, String key) {
        serverPresentationModel.findData key
    }

    /** store additional data, if present override and return the old one */
    def putData(ServerAttribute serverAttribute, String key, Object value) {
        serverAttribute.putData key, value
    }

    /** @return the additional data or null if not present */
    def findData(ServerAttribute serverAttribute, String key) {
        serverAttribute.findData key
    }
}

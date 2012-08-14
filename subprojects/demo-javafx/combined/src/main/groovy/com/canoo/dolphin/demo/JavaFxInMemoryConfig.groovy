package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.JavaFXUiThreadHandler
import com.canoo.dolphin.core.comm.DefaultInMemoryConfig

class JavaFxInMemoryConfig extends DefaultInMemoryConfig {

    JavaFxInMemoryConfig() {
        clientDolphin.clientConnector.uiThreadHandler = new JavaFXUiThreadHandler()
        serverDolphin.registerDefaultActions()
    }

}

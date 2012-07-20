package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.JavaFXUiThreadHandler
import com.canoo.dolphin.core.comm.DefaultInMemoryConfig

class CustomJavaFxInMemoryConfig extends DefaultInMemoryConfig {

    CustomJavaFxInMemoryConfig() {
        connector.uiThreadHandler = new JavaFXUiThreadHandler()
    }

    void registerDefaultActions() {
        super.registerDefaultActions()
        register new CustomAction(modelStore) // todo dk: we may want to add this more specifically per starter
    }

}

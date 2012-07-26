package com.canoo.dolphin.demo

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.client.ClientModelStore
import com.canoo.dolphin.core.client.Dolphin
import com.canoo.dolphin.core.client.comm.GrailsClientConnector
import com.canoo.dolphin.core.client.comm.JavaFXUiThreadHandler


LogConfig.logCommunication()
def connector = new GrailsClientConnector()
connector.uiThreadHandler = new JavaFXUiThreadHandler()
Dolphin.setClientConnector(connector)
Dolphin.setClientModelStore(new ClientModelStore())

PushView.show()

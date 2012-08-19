package com.canoo.dolphin.demo

import com.canoo.dolphin.core.server.action.SavePresentationModelAction
import com.canoo.dolphin.core.server.action.StoreInitialValueChangeAction

def config = new JavaFxInMemoryConfig()
config.serverDolphin.serverConnector.register(new SavePresentationModelAction(  config.serverDolphin.serverModelStore))
config.serverDolphin.serverConnector.register(new StoreInitialValueChangeAction(config.serverDolphin.serverModelStore))

new SaveView().show(config.clientDolphin)
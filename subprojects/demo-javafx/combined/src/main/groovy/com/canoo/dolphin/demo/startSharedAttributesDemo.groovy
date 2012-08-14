package com.canoo.dolphin.demo

def config = new JavaFxInMemoryConfig()
config.serverDolphin.serverConnector.register new CustomAction(config.serverDolphin.serverModelStore)

SharedAttributesView.show(config.clientDolphin)
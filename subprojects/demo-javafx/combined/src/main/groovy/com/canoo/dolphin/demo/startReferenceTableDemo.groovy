package com.canoo.dolphin.demo

def config = new JavaFxInMemoryConfig()
config.serverDolphin.serverConnector.register new ReferenceTableDemoAction(config.serverDolphin.serverModelStore)

new ReferenceTableView().show(config.clientDolphin)
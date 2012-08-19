package com.canoo.dolphin.demo

def config = new JavaFxInMemoryConfig()
config.serverDolphin.serverConnector.register(new DemoSearchAction(config.serverDolphin.serverModelStore));

DemoSearchView.show(config.clientDolphin)
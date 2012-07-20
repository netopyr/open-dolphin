package com.canoo.dolphin.demo

def config = new CustomJavaFxInMemoryConfig()
//config.connector.sleepMillis = 500
config.registerDefaultActions()
config.register(new DemoSearchAction(config.modelStore));

DemoSearchView.show()
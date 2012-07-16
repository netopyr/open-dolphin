package com.canoo.dolphin.demo

def config = new InMemoryConfig()
//config.connector.sleepMillis = 500
config.withActions()
config.register(new DemoSearchAction(config.modelStore));

DemoSearchView.show()
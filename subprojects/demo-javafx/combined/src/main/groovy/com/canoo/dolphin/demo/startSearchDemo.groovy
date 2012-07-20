package com.canoo.dolphin.demo

def config = new JavaFxInMemoryConfig()
//config.connector.sleepMillis = 500
config.register(new DemoSearchAction(config.modelStore));

DemoSearchView.show()
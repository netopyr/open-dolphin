package com.canoo.dolphin.demo

def config = new JavaFxInMemoryConfig()
config.register new ReferenceTableDemoAction(config.modelStore)

new ReferenceTableView().show()
package com.canoo.dolphin.demo

import static com.canoo.dolphin.demo.VehicleProperties.*

def config = new JavaFxInMemoryConfig()
config.serverDolphin.action CMD_PULL, new PullVehiclesActionHandler()

BindListView.show config.clientDolphin

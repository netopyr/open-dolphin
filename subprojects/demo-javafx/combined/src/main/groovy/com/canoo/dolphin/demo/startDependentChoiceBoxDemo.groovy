package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.comm.InitializeAttributeCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.action.ServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry

def config = new JavaFxInMemoryConfig()


config.serverDolphin.action "fillFirst", { cmd, response ->
    ServerPresentationModel pm1 = new ServerPresentationModel("First 1", [new ServerAttribute("value","even")])
    response << CreatePresentationModelCommand.makeFrom(pm1)
    ServerPresentationModel pm2 = new ServerPresentationModel("First 2", [new ServerAttribute("value","odd")])
    response << CreatePresentationModelCommand.makeFrom(pm2)
}

config.serverDolphin.action "fillRelation", { cmd, response ->
    [0,2,4,6,8].each {
        ServerPresentationModel pm = new ServerPresentationModel([
            new ServerAttribute("first", "even" ),
            new ServerAttribute("second", "Second $it" )
        ])
        pm.presentationModelType = "FirstSecondRelation"
        response << CreatePresentationModelCommand.makeFrom(pm)
    }
    [1,3,5,7,9].each {
        ServerPresentationModel pm = new ServerPresentationModel([
            new ServerAttribute("first", "odd" ),
            new ServerAttribute("second", "Second $it" )
        ])
        pm.presentationModelType = "FirstSecondRelation"
        response << CreatePresentationModelCommand.makeFrom(pm)
    }
}

DependentChoiceBoxView.show(config.clientDolphin)
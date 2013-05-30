package org.opendolphin.demo.crud

class SampleController {
    def model
    def builder

    def onReadyEnd = { app ->
        app.bindings.dolphin.send(PortfolioConstants.CMD.PULL) { portfolioPms ->
            for (pm in portfolioPms) {
                model.observableListOfPortfolios << pm
            }
            builder.fadeTransition(1.s, node: builder.portfolios, to: 1).playFromStart()
        }
    }
}

package com.canoo.dolphin.demo.crud

import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.server.ServerDolphin

import static com.canoo.dolphin.demo.crud.CrudConstants.*

class CrudActions {

    static void register(ServerDolphin serverDolphin, CrudService crudService) {

        serverDolphin.action CMD_PULL_PORTFOLIOS, { cmd, response ->
            def portfolios = crudService.listPortfolios()
            portfolios.eachWithIndex { portfolio, index ->
                def pm = serverDolphin.presentationModel(portfolio, pmId(TYPE_PORTFOLIO, index), TYPE_PORTFOLIO)
                response << CreatePresentationModelCommand.makeFrom(pm)
            }
        }

        serverDolphin.action CMD_PULL_POSITIONS, { cmd, response ->
            def selectedPortfolio = serverDolphin.findPresentationModelById(PM_SELECTED_PORTFOLIO)
            def positions = crudService.listPositions()
            positions.eachWithIndex { position, index ->
                position.portfolioId = selectedPortfolio.domainId.value
                def pm = serverDolphin.presentationModel(position, pmId(TYPE_POSITION, index), TYPE_POSITION)
                response << CreatePresentationModelCommand.makeFrom(pm)
            }
        }

        serverDolphin.action CMD_UPDATE_TOTAL, { cmd, response ->
            def selectedPortfolio = serverDolphin.findPresentationModelById(PM_SELECTED_PORTFOLIO)
            def portfolioDomainId = selectedPortfolio.domainId.value
            def allPositions = serverDolphin.findAllPresentationModelsByType(TYPE_POSITION)
            def positions = allPositions.findAll { it.portfolioId.value == portfolioDomainId }
            def sum = positions.sum { it.weight.value }
            response << selectedPortfolio.total.changeValueCommand(sum) // todo: serverDolphin.setValue(attribute, newValue)
        }
    }
}

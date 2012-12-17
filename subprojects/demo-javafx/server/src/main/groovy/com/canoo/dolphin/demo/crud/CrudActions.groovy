package com.canoo.dolphin.demo.crud

import com.canoo.dolphin.core.server.Slot
import com.canoo.dolphin.core.server.action.DolphinServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry

import static com.canoo.dolphin.demo.crud.CrudConstants.*

class CrudActions extends DolphinServerAction {

    CrudService crudService

    void registerIn(ActionRegistry registry) {

        serverDolphin.action CMD_PULL_PORTFOLIOS, { cmd, response ->
            def portfolios = crudService.listPortfolios(1L) // fixed value until we have users
            portfolios.eachWithIndex { portfolioDTO, index ->
                presentationModel pmId(TYPE_PORTFOLIO, index), TYPE_PORTFOLIO, portfolioDTO
            }
        }

        serverDolphin.action CMD_PULL_POSITIONS, { cmd, response ->
            def visiblePortfolio  = serverDolphin.findPresentationModelById(PM_SELECTED_PORTFOLIO)
            def selectedPortfolio = serverDolphin.findPresentationModelById(visiblePortfolio[ATT_PORTFOLIO_ID].value)
            def positions = crudService.listPositions(selectedPortfolio[ATT_DOMAIN_ID].value.toLong())
            positions.eachWithIndex { positionDTO, index ->
                positionDTO.slots << new Slot(ATT_PORTFOLIO_ID, selectedPortfolio[ATT_DOMAIN_ID].value)
                presentationModel null, TYPE_POSITION, positionDTO
            }
        }

        serverDolphin.action CMD_UPDATE_TOTAL, { cmd, response ->
            def visiblePortfolio = serverDolphin.findPresentationModelById(PM_SELECTED_PORTFOLIO)
            def currentPortfolio = serverDolphin.findPresentationModelById(visiblePortfolio[ATT_PORTFOLIO_ID].value)

            def portfolioDomainId = currentPortfolio[ATT_DOMAIN_ID].value
            def allPositions = serverDolphin.findAllPresentationModelsByType(TYPE_POSITION)
            def positions = allPositions.findAll { it[ATT_PORTFOLIO_ID].value == portfolioDomainId }
            def sum = positions.sum { it[ATT_WEIGHT].value }

            changeValue(currentPortfolio[ATT_TOTAL], sum)
        }
    }
}

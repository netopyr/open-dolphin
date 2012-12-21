package com.canoo.dolphin.demo.crud

import com.canoo.dolphin.core.server.Slot
import com.canoo.dolphin.core.server.action.DolphinServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry

import static PortfolioConstants.*
import static com.canoo.dolphin.demo.crud.PortfolioConstants.ATT.DOMAIN_ID
import static com.canoo.dolphin.demo.crud.PortfolioConstants.ATT.TOTAL
import static com.canoo.dolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED
import static com.canoo.dolphin.demo.crud.PortfolioConstants.TYPE.PORTFOLIO
import static com.canoo.dolphin.demo.crud.PositionConstants.ATT.PORTFOLIO_ID
import static com.canoo.dolphin.demo.crud.PositionConstants.ATT.WEIGHT
import static com.canoo.dolphin.demo.crud.PositionConstants.TYPE.POSITION

class CrudActions extends DolphinServerAction {

    CrudService crudService

    void registerIn(ActionRegistry registry) {

        serverDolphin.action PortfolioConstants.CMD.PULL, { cmd, response ->
            def portfolios = crudService.listPortfolios(1L) // fixed value until we have users
            portfolios.eachWithIndex { portfolioDTO, index ->
                presentationModel pmId(PORTFOLIO, index), PORTFOLIO, portfolioDTO
            }
        }

        serverDolphin.action PositionConstants.CMD.PULL, { cmd, response ->
            def visiblePortfolio  = serverDolphin.findPresentationModelById(SELECTED)
            def selectedPortfolio = serverDolphin.findPresentationModelById(visiblePortfolio[PORTFOLIO_ID].value as String)
            def positions = crudService.listPositions(selectedPortfolio[DOMAIN_ID].value.toLong())
            positions.eachWithIndex { positionDTO, index ->
                positionDTO.slots << new Slot(PORTFOLIO_ID, selectedPortfolio[DOMAIN_ID].value)
                presentationModel null, POSITION, positionDTO
            }
        }

        serverDolphin.action PortfolioConstants.CMD.UPDATE, { cmd, response ->
            def visiblePortfolio = serverDolphin.findPresentationModelById(SELECTED)
            def currentPortfolio = serverDolphin.findPresentationModelById(visiblePortfolio[PORTFOLIO_ID].value)

            def portfolioDomainId = currentPortfolio[DOMAIN_ID].value
            def allPositions = serverDolphin.findAllPresentationModelsByType(POSITION)
            def positions = allPositions.findAll { it[PORTFOLIO_ID].value == portfolioDomainId }
            def sum = positions.sum { it[WEIGHT].value }

            changeValue(currentPortfolio[TOTAL], sum)
        }
    }
}

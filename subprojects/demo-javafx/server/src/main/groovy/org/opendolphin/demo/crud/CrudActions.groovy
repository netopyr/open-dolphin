package org.opendolphin.demo.crud

import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import static org.opendolphin.demo.crud.PortfolioConstants.*
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.DOMAIN_ID
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.TOTAL
import static org.opendolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED
import static org.opendolphin.demo.crud.PortfolioConstants.TYPE.PORTFOLIO
import static org.opendolphin.demo.crud.PositionConstants.ATT.PORTFOLIO_ID
import static org.opendolphin.demo.crud.PositionConstants.ATT.WEIGHT
import static org.opendolphin.demo.crud.PositionConstants.TYPE.POSITION

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

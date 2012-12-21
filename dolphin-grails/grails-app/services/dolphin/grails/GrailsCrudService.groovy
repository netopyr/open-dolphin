package dolphin.grails

import com.canoo.dolphin.core.server.DTO
import com.canoo.dolphin.core.server.Slot
import com.canoo.dolphin.demo.crud.CrudService

import static com.canoo.dolphin.demo.crud.PortfolioConstants.ATT.*
import static com.canoo.dolphin.demo.crud.PositionConstants.ATT.*

class GrailsCrudService implements CrudService {

    // procedural variant
    List<DTO> listPortfolios(long ownerId) {
        List<DTO> result = new LinkedList<DTO>();
        Portfolio
            .findAllByOwner(User.read(ownerId))
            .each { portfolio ->
                result.add(
                    new DTO(
                        new Slot(DOMAIN_ID, portfolio.id),
                        new Slot(NAME,      portfolio.name),
                        new Slot(TOTAL,     Position.findAllByPortfolio(portfolio).sum { it.weight } ),
                        new Slot(FIXED,     portfolio.fixed)
                    )
                )
            }
        return result;
    }

    // functional variant
    List<DTO> listPositions(long portfolioId) {
        Position
            .findAllByPortfolio(Portfolio.read(portfolioId))
            .collect {
                new DTO (
                    new Slot(INSTRUMENT, it.instrument.name),
                    new Slot(WEIGHT,     it.weight)
                )
            }
    }
}



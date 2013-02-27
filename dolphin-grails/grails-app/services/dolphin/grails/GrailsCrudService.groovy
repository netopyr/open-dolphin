package dolphin.grails

import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot
import org.opendolphin.demo.crud.CrudService

import static org.opendolphin.demo.crud.PortfolioConstants.ATT.*
import static org.opendolphin.demo.crud.PositionConstants.ATT.*

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



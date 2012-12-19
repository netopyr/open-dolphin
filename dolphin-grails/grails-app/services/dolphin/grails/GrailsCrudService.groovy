package dolphin.grails

import com.canoo.dolphin.core.server.DTO
import com.canoo.dolphin.core.server.Slot
import com.canoo.dolphin.demo.crud.CrudService
import static com.canoo.dolphin.demo.crud.CrudConstants.*

class GrailsCrudService implements CrudService {

    // procedural variant
    List<DTO> listPortfolios(long ownerId) {
        List<DTO> result = new LinkedList<DTO>();
        Portfolio
            .findAllByOwner(User.read(1L))  // fixed value until we have users
            .each { portfolio ->
                result.add(
                    new DTO(
                        new Slot(ATT_DOMAIN_ID, portfolio.id),
                        new Slot(ATT_NAME,      portfolio.name),
                        new Slot(ATT_TOTAL,     Position.findAllByPortfolio(portfolio).sum { it.weight } ),
                        new Slot(ATT_FIXED,     portfolio.fixed)
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
                    new Slot(ATT_INSTRUMENT, it.instrument.name),
                    new Slot(ATT_WEIGHT,     it.weight)
                )
            }
    }
}



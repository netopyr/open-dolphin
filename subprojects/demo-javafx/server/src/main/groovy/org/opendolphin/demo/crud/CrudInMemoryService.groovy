package org.opendolphin.demo.crud

import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot

import static org.opendolphin.demo.crud.PortfolioConstants.ATT.*
import static org.opendolphin.demo.crud.PositionConstants.ATT.*

class CrudInMemoryService implements CrudService {

    // Java-like variant
    List<DTO> listPortfolios(long ownerId) {
        List<DTO> result = new LinkedList<DTO>();
        result.add(new DTO(
            new Slot(DOMAIN_ID, 1),
            new Slot(NAME, 'Balanced'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        result.add(new DTO(
            new Slot(DOMAIN_ID, 2),
            new Slot(NAME, 'Growth'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        result.add(new DTO(
            new Slot(DOMAIN_ID, 3),
            new Slot(NAME, 'Risky'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        result.add(new DTO(
            new Slot(DOMAIN_ID, 4),
            new Slot(NAME, 'Insane'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        return result;
    }

    // Groovy-like variant
    List<DTO> listPositions(long portfolioId) {
        [
            [(INSTRUMENT): 'ORCL', (WEIGHT): 10],
            [(INSTRUMENT): 'APPL', (WEIGHT): 40],
            [(INSTRUMENT): 'IBM',  (WEIGHT): 30],
            [(INSTRUMENT): 'UBSN', (WEIGHT): 20],
        ].collect { new DTO(Slot.slots(it)) }
    }
}
